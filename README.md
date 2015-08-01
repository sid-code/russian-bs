Code for the 2015 Mathcamp Russian BS AI Tournament project

## Directory structure

The project root should contain only the python files necessary for the dealer.
All strategy files should go in `strategy/your-name`.

## How to run

```
$ python Dealer2.py strategy-executable1 strategy-executable2 [strategy-executable3 ...]
```

The files need to be executable. On Unix-based systems, this means that if
you're writing it in a interpreted language like Python, you'll need to use
Jalex's hack (see the top of his strategy files.) Also, the file needs to
be marked as executable (`chmod +x file`).

If you're not using a Unix-based system, it won't work without modifications
to the dealer program.

## Important note

I've changed up the dealer output to make it easier for client programs to parse.
For the time being, Jalex's strategies won't work but I'll fix this soon. Here
is the format:

Board file:

```
PLAYER|P0|strategy-executable   # player entry
START|P0                        # who starts the game
PLAY|P0|4T|22                   # PLAY|who-played|claim ("four tens")|how many cards left in their hand
CALL|P0|BS|TH,TS,TC,TD|BAD|P0   # CALL|who-called|call type|revealed cards|good call?|who takes cards
```

Hand file:
```
PLAYER|P0                       # Your name
HAND|2H,2C,2D,3H, ...           # List of cards, separated by comma
```
