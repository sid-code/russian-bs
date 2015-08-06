import subprocess, time, random, os, sys
import pdb

class InvalidMove(Exception):
	def __init__(self, current_move, last_move):
		self.current_move = current_move
		self.last_move = last_move
		print '\nTried to play', current_move, 'after', last_move,'\n\n'

class InvalidMoveSyntax(Exception):
	def __init__(self, move):
		self.move = move
		print '\nTried to play "', move, '"'

const_ranks = "A23456789TJKQ"
const_suits = "SCDH"
const_cards = [a + b for a in const_ranks for b in const_suits]

class Game():
	def __init__(self,prognames, display = False):
	# '''Initializes a game with the given set of player prognames'''
		if not os.path.exists('./logs'):
			os.mkdir('./logs')
		self.id = 1
		while os.path.exists('./logs/%04d.board'%self.id):
			self.id += 1
		self.board = os.path.abspath('./logs/%04d.board'%self.id)
		open(self.board,'a').close()
		self.display = display

		self.players = []
		n=0
		for p in prognames:
			self.players.append(Player(self, n, p))
			self.board_write('PLAYER|P%d|%s'%(n, p))
			n+= 1
		self.n_players = len(self.players)

		self.board_write('START|P0')

		self.winners = []
		self.last_move = {'player':0, 'call':True, 'claim':'', 'cards':''}
		# player is integer index into players
		# call is True, False, 'BS', 'BELIEVE' if previous move was
			# nonexistent, playing cards, bs, or believe
		# claim and cards are the move if call is False
		#
		deck = const_cards[:]
		random.shuffle(deck)
		i = 0
		while deck != []:
			self.players[i].add_cards([deck.pop()])
			i = (i + 1) % self.n_players
		for p in self.players:
			p.log_write('PLAYER|P%d'%p.position)

		self.table = []
		self.removed = []

	def clear_table(self):
		self.removed += self.table[:]
		self.table = []

	def board_write(self, line):
		f = open(self.board, 'a+')
		f.write(line.strip() + '\n')
		f.close()
		if self.display: print line.strip()

	def play_move(self, player):
		'''Updates self.table and self.last_move, and returns the kind of turn that occurred.'''
		move = player.get_move()
		# print 'P%d move again: '%player.position, move
		#return all caps string of move
		def split_move(move):
			m = move.upper()
			try:
				# print 'split_move', m.split(';')
				claim = m.split(';')[0]
				cards = m.split(';')[1].split(',')
				return (claim, cards)
			except:
				raise InvalidMoveSyntax(move)

		if move in ['BS','BELIEVE']:
			if self.last_move['call']:
				raise InvalidMove(move, self.last_move)
			else:
				# print self.last_move['claim'], self.last_move['cards']
				truth = all(map(lambda x: x[0] == self.last_move['claim'][1], self.last_move['cards'].split(',')))
                                # CALL|caller|call_type|reveal|BAD or GOOD|who takes cards

				out = 'CALL|P%d|%s|%s'%(player.position, move, self.last_move['cards'])
                                result = ''
				if (move == 'BS' and truth) or (move == 'BELIEVE' and not truth):
					player.take_board()
					self.board_write(out + '|BAD|P%d'%(player.position))
                                        result = 'bad call'
				elif move == 'BS' and not truth:
					self.players[self.last_move['player']].take_board()
					self.board_write(out + '|GOOD|P%d'%self.last_move['player'])
                                        result = 'good call'
				elif move == 'BELIEVE' and truth:
					self.clear_table()
					self.board_write(out + '|GOOD|P%d'%(player.position))
                                        result = 'good call'
				self.last_move = {'player':player.position, 'call':move, 'claim':'', 'cards':''}
                                return result
		else:
			claim, cards = split_move(move)
			# print claim, cards, self.last_move['claim']
			if (int(claim[0]) != len(cards) or (not self.last_move['call'] and claim[1] != self.last_move['claim'][1])):
				raise InvalidMove(move,self.last_move)
			player.play_cards(cards)
                        self.table += cards[:]
			self.last_move = {'player':player.position, 'call':False, 'claim':claim, 'cards':','.join(cards)}
			self.board_write('PLAY|P%d|%s|%d'%(player.position, claim, len(player.cards)))
			return 'cards'


	def play_game(self):
		self.to_move = 0
		while len(self.winners) < self.n_players - 1:
			if self.to_move in self.winners:
				self.to_move = (self.to_move + 1) % self.n_players
				continue
			else:
				outcome = self.play_move(self.players[self.to_move])
				if outcome in ['good call', 'bad call']:
					for p in self.players[::-1]:
						if p.position in self.winners:
							pass
						elif len(p.cards) == 0:
							self.winners.append(p.position)
							print "WIN|P%d"%(p.position)
				if outcome in ['cards','bad call']:
					self.to_move = (self.to_move + 1) % self.n_players
				elif outcome == 'good call':
					continue
		print "GAME|", ",".join(map(str, self.winners))

class Player():
	def __init__(self, game, position, progname):
		self.game = game
		self.position = position
		self.progname = progname
		if not os.path.exists('./logs'):
			os.mkdir('./logs')
		self.log = os.path.abspath("./logs/%04dP%d.hand"%(self.game.id, self.position))
		open(self.log,'a').close()

		self.cards = []

	def get_move(self):
		'''Writes hand to file, then asks program to write hand to file, returns move'''
		self.log_write('HAND|' + ','.join(self.cards))
		log = open(self.log, 'a+')
		subprocess.call(" ".join([self.progname, self.game.board, self.log]),
                                 stdout = log,
                                 stderr = sys.stdout,
                                 shell = True)

		log.close()
		log = open(self.log, 'r')
		out = [x for x in log.readlines() if x.strip() != '']
		log.close()
		return out[-1].strip().upper()

	def log_write(self, line):
		# if self.progname == 'human':
		# 	print 'P%d:'%self.position,line.strip()
		f = open(self.log, 'a+')
		f.write(line.strip() + '\n')
		f.close()


	def play_cards(self, cards):
		if not set(cards).issubset(set(self.cards)):
			raise InvalidMove(','.join(cards),'having a hand of %s.'%','.join(self.cards))
		else:
			self.cards = sorted(list(set(self.cards).difference(set(cards))))

	def add_cards(self, cards):
		self.cards += cards[:]
		self.cards.sort()

	def take_board(self):
		self.add_cards(self.game.table)
		self.game.table = []

# game = Game(['./player.py'], 'display')
game = Game(sys.argv[1:], 'display')
game.play_game()
# p = game.players[0]
# s=game.players[1]
# # print s.position
# game.play_move(p)
# game.play_move(s)
