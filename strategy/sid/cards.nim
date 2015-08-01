from math import random, randomize

type
  Rank* = enum
    Ace = 1, Two = 2, Three = 3, Four = 4, Five = 5, Six = 6, Seven = 7,
    Eight = 8, Nine = 9, Ten = 10, Jack = 11, Queen = 12, King = 13
  Suit* = enum Clubs = 0, Diamonds = 1, Hearts = 2, Spades = 3
  Card* = tuple[rank: Rank, suit: Suit]
  Deck* = seq[Card]

const
  ranks* = [Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten,
           Jack, Queen, King]
  rankNames* = ["A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"]

  suits* = [Clubs, Diamonds, Hearts, Spades]
  suitNames* = ["C", "D", "H", "S"]

proc name*(r: Rank): string =
  rankNames[r.int - 1]

proc name*(s: Suit): string =
  suitNames[s.int]

proc `$`*(c: Card): string =
  c.rank.name & c.suit.name

proc newDeck*: Deck =
  newSeq(result, 0)
  for rank in ranks:
    for suit in suits:
      result.add((rank, suit))

proc shuffle*(deck: Deck): Deck =
  result = deck
  var i = deck.len - 1
  while i > 0:
    let j = random(i + 1)
    let tmp = result[i]
    result[i] = result[j]
    result[j] = tmp
    i -= 1

proc deal*(deck: var Deck): Card =
  result = deck[0]
  deck.delete(0)

randomize()

proc toRank*(s: string): Rank =
  let rankIndex = rankNames.find(s)
  if rankIndex < 0:
    raise newException(ValueError, "invalid rank " & s)
  return ranks[rankIndex]

proc toSuit*(s: string): Suit =
  let suitIndex = suitNames.find(s)
  if suitIndex < 0:
    raise newException(ValueError, "invalid suit " & s)
  return suits[suitIndex]

proc toCard*(s: string): Card =
  if s.len != 2:
    raise newException(ValueError, "card strings are two characters long")
  
  let rank = s[0..0].toRank()
  let suit= s[1..1].toSuit()

  (rank, suit)
  
