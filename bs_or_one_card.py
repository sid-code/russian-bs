#! /bin/sh
""":"
exec python2 $0 ${1+"$@"}
"""
import sys
import random
from Player import calculate_legal_move, calculate_maximal_rank


# This strategy always tells the truth if it can, playing a maximal number of cards.
# If it can't play cards, it calls BS or Believe at random.

def initalize():
	board_file = open(sys.argv[1], 'r')
	last_move = board_file.readlines()[-1]
	board_file.close()

	hand_file = open(sys.argv[2], 'r')
	history = hand_file.readlines()	
	hand_file.close()

	last_to_play = int(last_move.split()[0][1])

	card_string = history[-1].strip('Hand: ')
	position = int(history[0][1])
	return (history, last_move, last_to_play, card_string, position)

def play(move):
	# print_to_screen('P%d move: '%position+move)
	print move
	exit()

def print_to_screen(message):
	sys.stderr.write(message)


const_ranks = "A23456789TJKQ"
const_suits = "SCDH"
const_cards = [a + b for a in const_ranks for b in const_suits]


history, last_move, last_to_play, card_string, position = initalize()
cards = card_string.split(',')
# print_to_screen('Hand log for P%d: %s\n'%(position, history))

# if last_move.split()[1] in ['calls', 'starts']:
# 	call_is_legal = False
# 	legal_rank = 'wild' #wild
# else:
# 	call_is_legal = True
# 	legal_rank = last_move.split(',')[0][-1]
call_is_legal, legal_rank = calculate_legal_move(last_move)

if not call_is_legal:
	rank_counts = map(lambda x: card_string.count(x), const_ranks)
	legal_rank = const_ranks[rank_counts.index(max(rank_counts))]

# print_to_screen('Move calculation: '+str(legal_rank) + str(call_is_legal) + '\n')

if call_is_legal:
	play('BS')
else:
	play("1%s;%s"%(legal_rank, cards[0]))
