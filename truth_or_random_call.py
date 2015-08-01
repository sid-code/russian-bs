#! /bin/sh
""":"
exec python2 $0 ${1+"$@"}
"""
import sys
import random
from Player import *


# This strategy always tells the truth if it can, playing a maximal number of cards.
# If it can't play cards, it calls BS or Believe at random.


history, last_move, last_to_play, card_string, position = initalize()
cards = card_string.split(',')

call_is_legal, legal_rank = calculate_legal_move(last_move)

if not call_is_legal:
	legal_rank = calculate_maximal_rank(card_string)

# print_to_screen('Move calculation: '+str(legal_rank) + str(call_is_legal) + '\n')

legal_cards = filter(lambda x: x.count(legal_rank), cards)
if legal_cards == []:
	play(random.choice(['BS','BELIEVE']))
else:
	play("%d%s;%s"%(len(legal_cards),legal_rank,','.join(legal_cards)))

