Code for the 2015 Mathcamp Russian BS AI Tournament project

## Directory structure

The project root should contain only the python files necessary for the dealer.
All strategy files should go in `strategy/your-name`.

## How to run

```
$ python Dealer2.py strategy-executable1 strategy-executable2 [strategy-executable3 ...]
```

If you're using Python (or any other scripting language) to make your
strategies, `strategy-executable` needs to be `"python path/to/script.py"`
(with the quotes!) **You no longer need the shell script hack to run
non-standalone programs as strategies.**

For example, if you want to pit Jalex's strategies against each other,
run this from the project root:

```
$ python Dealer2.py "python strategy/jalex/bs_or_one_card.py" "python strategy/jalex/truth_or_random_call.py"
```

## Important note

I've changed up the dealer output to make it easier for client programs to parse.
I've fixed Jalex's strategy files to work with this format. Here is the format:

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

## To-Do list

Here's what I'm going to be working on, probably in order:

 * More strategies that involve lying and intelligent calling
 * Some kind of way to present the game as it is being played (for the project fair)
