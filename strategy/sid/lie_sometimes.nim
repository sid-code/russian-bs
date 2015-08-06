import os, strutils, math
import cards, rbslib

const
  # If we're about to lie, this stops it from happening
  chanceToNotLie = 0.2

# This proc searches a list of cards for one for which there exist others
# in the list with the same rank. This is to avoid playing important cards
# when lying.
proc findAppropriateCard(cs: seq[Card]): Card =
  for c in cs:
    let number = cs.findRank(c.rank).len
    if number > 1:
      return c

  return cs[0]

# Check if the last play was definitely a lie
# true = maybe truth
# false = definitely lying
#
# we cannot prove that they are telling the truth
proc checkLastPlay(game: RBSGame, cardsInHand: seq[Card]): bool =
    let (who, lastPlay) = game.history[^1]
    discard who

    if lastPlay.kind != aPlay:
      # We can't call BS or BELIEVE on another call
      return true

    let (cnum, crank) = lastPlay.play
    # If there's more than (4 - cnum) cards of rank crank, then they're definitely
    # lying

    let knownNumber = game.discardPile.findRank(crank).len + cardsInHand.findRank(crank).len

    return knownNumber + cnum <= 4

# See how many cards of a certain rank have been just put down
proc findCurrentRun(game: RBSGame, rank: Rank): int =
  result = 0
  var index = 1
  while true:
    let (who, action) = game.history[^index]
    if action.kind != aPlay or action.play.rank != rank: break

    result += action.play.number
    index += 1
    if index >= game.history.len: break

proc calculateMove(game: RBSGame, hand: seq[Card]): string =
  if hand.len == 0:
    # We have to make the correct call.
    if not checkLastPlay(game, hand):
      return "BS"
    else:
      # There could be better logic here but for now let's just believe it
      return "BELIEVE"

  if game.rankLock:
    if not checkLastPlay(game, hand):
      return "BS"

    let
      rank = game.currentClaim
      possibleCards = hand.findRank(rank)
      numCards = possibleCards.len

    if numCards > 0:
      return makePlay(possibleCards, rank)
    else:
      # We don't have the cards, but why not still play?
      #
      # If there are less than three in the discard pile, then we'll go
      # out on a limb and claim one
      #
      # The actual card that we'll place down is going to be a card
      # that we have another of. If there's only one 3 in the hand, don't
      # place it because that might get us in this situation again.
      #
      # There will also be a probability of just making a call. However,
      # the best call to make in this case is BELIEVE because it was
      # already determined that BS isn't a great call.
      #
      # One more thing to consider is that lying about a card when people
      # have already claimed to place 4 down is a bad idea because the
      # person next may not have the card and be forced to call BS. Even
      # worse, they have the card and call BS and get to play it as well.


      let numberInDiscardPile = game.discardPile.findRank(rank).len
      let numberAlreadyClaimed = findCurrentRun(game, rank)
      let randomFactor = random(1.0)

      if numberInDiscardPile < 3 and numberAlreadyClaimed < 4 and randomFactor < chanceToNotLie:
        let cardToPlace = findAppropriateCard(hand)
        return makePlay(@[cardToPlace], rank)
      else:
        return "BELIEVE"
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

randomize()

let args = commandLineParams()
if args.len == 0:
  raise newException(Exception, "This program can only be run from the dealer")

let (game, player, hand) = getGameState(args)

let move = calculateMove(game, hand)
echo move


