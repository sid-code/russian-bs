import strutils, sequtils
import cards

type
  RBSActionType* = enum
    aPlay, aBS, aBEL
  
  RBSAction* = object
    case kind*: RBSActionType
      of aPlay: play*: tuple[number: int, rank: Rank]
      of aBS: goodbs*: bool
      of aBEL: goodbel*: bool

  RBSActionEntry* = tuple[player: RBSPlayer, action: RBSAction]

  RBSPlayer* = ref object
    name*: string
    cards*: int

  RBSGame* = ref object
    players*: seq[RBSPlayer]
    board*: int
    discardPile*: seq[Card]
    history*: seq[RBSActionEntry]
    currentClaim*: Rank
    rankLock*: bool

  Player* = ref object
    cards*: int

  Game* = ref object
    deadCards*: seq[Card]
    boardCards*: int

template echod*(x: expr) =
  stderr.write($x & "\n")

proc newRBSPlayer*(name: string): RBSPlayer =
  new result
  result.name = name
  result.cards = 0

proc newRBSGame*: RBSGame =
  new result
  newSeq(result.players, 0)
  result.board = 0
  newSeq(result.discardPile, 0)
  newSeq(result.history, 0)
  result.currentClaim = Rank(1)
  result.rankLock = false

proc `$`*(player: RBSPlayer): string =
  player.name

proc `$`*(game: RBSGame): string =
  result = ""
  result &= "board: $#, dpile: $#\n" % [$game.board, $game.discardPile]
  if game.rankLock:
    result &= "rank locked: $#\n" % [$game.currentClaim]
  result &= "history: \n"
  for act in game.history:
    result &= " - " & $act & "\n"

proc `$`*(cs: seq[Card]): string =
  cs.map(proc (c: Card): string = $c).join(",")

proc findPlayerByName*(game: RBSGame, name: string): RBSPlayer =
  for player in game.players:
    if player.name == name:
      return player

proc parseCardList*(list: string, sep: string = ","): seq[Card] =
  list.split(",").map(proc (s: string): Card = s.toCard())

proc findRank*(pile: seq[Card], rank: Rank): seq[Card] =
  pile.filter(proc(c: Card): bool = c.rank == rank)

proc makePlay*(cs: seq[Card], rank: Rank): string =
  return "$#$#;$#" % [$cs.len, rank.name, $cs]

proc parseBoardLine(game: var RBSGame, line: string) =
  let
    parts = line.split("|")
    kind = parts[0]
    args = parts[1..^1]

  case kind:
    of "PLAYER":
      let name = args[0]
      let player = newRBSPlayer(name)
      game.players.add(player)
    of "PLAY":
      let
        who = game.findPlayerByName(args[0])
        claimstr = args[1]
        cardsLeft = parseInt(args[2])
        claimNumber = parseInt(claimstr[0..0])
        claimRank = toRank(claimstr[1..1])
        action = RBSAction(kind: aPlay, play: (claimNumber, claimRank))

      game.currentClaim = claimRank
      game.rankLock = true
      game.history.add((who, action))
      who.cards = cardsLeft
    of "CALL":
      let
        who = game.findPlayerByName(args[0])
        kind = args[1]
        reveal = parseCardList(args[2])
        good = args[3] == "GOOD"

      if not (kind == "BELIEVE" and good):
        # someone took cards
        let taker = game.findPlayerByName(args[4])
        taker.cards += reveal.len

      var action: RBSAction
      if kind == "BS":
        action = RBSAction(kind: aBS, goodbs: good)
      elif kind == "BELIEVE":
        if good:
          game.discardPile.add(reveal)
        action = RBSAction(kind: aBEL, goodbel: good)
      else:
        raise newException(Exception, "Invalid call")

      game.rankLock = false
      game.history.add((who, action))

    else: discard

proc parseBoard*(game: var RBSGame, ls: seq[string]) =
  for line in ls:
    if line.strip().len == 0:
      continue
    parseBoardLine(game, line)

proc getGameState*(args: seq[string]):
  tuple[game: RBSGame, player: RBSPlayer, hand: seq[Card]] =

  let board = readFile(args[0]).split("\n")
  let hand = readFile(args[1]).split("\n")

  var game = newRBSGame()
  parseBoard(game, board)

  # The only relevant parts of the hand file are the first line and the last line
  # The first line contains the player name and the last line contains the current
  # cards in hand.

  let player = game.findPlayerByName(hand[0].split("|")[1])
  let cardsInHand = parseCardList(hand[^2].split("|")[1])

  return (game, player, cardsInHand)
