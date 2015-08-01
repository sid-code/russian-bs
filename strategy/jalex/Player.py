#! /bin/sh
""":"
exec python $0 ${1+"$@"}
"""
import sys
import random


# This strategy always tells the truth if it can, playing a maximal number of cards.
# If it can't play cards, it calls BS or Believe at random.

def init():
	board_file = open(sys.argv[1], 'r')
	last_move = board_file.readlines()[-1]
	board_file.close()

	hand_file = open(sys.argv[2], 'r')
	history = hand_file.readlines()	
	hand_file.close()
        exit
	last_to_play = int(last_move.split("|")[1][1])

	card_string = history[-1].strip('HAND|')
	position = int(history[0].strip('PLAYER|P'))
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

def calculate_legal_move(last_move):
	'''Returns whether it is legal to call, and if so, what rank is legal to claim'''
	if last_move.split("|")[0] in ['CALL', 'START']:
		call_is_legal = False
		legal_rank = 'wild' #wild
	else:
		call_is_legal = True
		legal_rank = last_move.split('|')[2][-1]
	return (call_is_legal, legal_rank)

def calculate_maximal_rank(card_string):
	'''Gives a rank of which we have a maximal number of cards'''
	rank_counts = map(lambda x: card_string.count(x), const_ranks)
	return const_ranks[rank_counts.index(max(rank_counts))]

