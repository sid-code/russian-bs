import os, strutils
import cards, rbslib


let args = commandLineParams()
if args.len == 0:
  raise newException(Exception, "This program can only be run from the dealer")

let (game, player, hand) = getGameState(args)

proc calculateMove(game: RBSGame, hand: seq[Card]): string =
  if game.rankLock:
    let
      rank = game.currentClaim
      possibleCards = hand.findRank(rank)
      numCards = possibleCards.len

    if numCards > 0:
      return makePlay(possibleCards, rank)
    else:
      return "BS"
  else:
    var maxRank: Rank = Ace
    var maxNumber = 0
    for rank in ranks:
      let number = hand.findRank(rank).len
      if number > maxNumber:
        maxNumber = number
        maxRank = rank

    let play = hand.findRank(maxRank)
    return makePlay(play, maxRank)



let move = calculateMove(game, hand)
echo move


