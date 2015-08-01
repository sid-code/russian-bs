import os, strutils
import cards, rbslib


let args = commandLineParams()
if args.len == 0:
  raise newException(Exception, "This program can only be run from the dealer")

let board = readFile(args[0]).split("\n")
let hand = readFile(args[1]).split("\n")

echod "Here is the board:"
for line in board:
  echod "   " & line
echod "Here is the hand:"
for line in hand:
  echod "   " & line

var game = newRBSGame()
parseBoard(game, board)

# The only relevant parts of the hand file are the first line and the last line
# The first line contains the player name and the last line contains the current
# cards in hand.

let player = game.findPlayerByName(hand[0].split("|")[1])
discard player
let cardsInHand = parseCardList(hand[^2].split("|")[1])

proc calculateMove(game: RBSGame, cs: seq[Card]): string =
  if game.rankLock:
    let
      rank = game.currentClaim
      possibleCards = cs.findRank(rank)
      numCards = possibleCards.len

    if numCards > 0:
      return makePlay(possibleCards, rank)
    else:
      return "BS"
  else:
    var maxRank: Rank = Ace
    var maxNumber = 0
    for rank in ranks:
      let number = cs.findRank(rank).len
      if number > maxNumber:
        maxNumber = number
        maxRank = rank

    let play = cs.findRank(maxRank)
    return makePlay(play, maxRank)



let move = calculateMove(game, cardsInHand)
echo move


