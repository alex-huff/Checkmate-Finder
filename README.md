# CheckmateFinder
## About
CheckmateFinder is a Java application that can find forced checkmates given an initial board state. A "forced" checkmate means that one side, given optimal play, can force the opponent into a checkmate. For a forced checkmate, it does not matter what moves the opponent plays, there is always a way to checkmate them. This often occurs towards the end of a chess match, and can be surpisingly difficult to find. My original motivation to make CheckmateFinder came from chess puzzles. Many chess puzzles expect the player to find the first move in a checkmate sequence. CheckmateFinder can often find this first move.

Since the checkmate sequence depends on what moves the opponent plays, CheckmateFinder will only provide the first move if it finds a forced checkmate. After the opponent makes their next move (and the new game state is entered into the program), CheckmateFinder is guarenteed to find the next move since it has already proved that there is a forced checkmate from the initial board state.

Currently, CheckmateFinder has no graphical interface, and can only be controlled by manually entering the [FEN (Forsyth–Edwards Notation)](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) of the game into the program. Along with the FEN, a depth must be provided that tells CheckmateFinder how many moves ahead to search. Another depth, the "check depth," must also be provided that tells CheckmateFinder how many turns it should consider non-check moves (this drastically increases the search time). The higher the depths, the more likely it is that CheckmateFinder will find a forced mate. With higher depths also comes increased search time, and with unreasonably high depths, the program will not terminate in your lifetime. CheckmateFinder is not an optimized program, but an absolute brute-forced approach to calculating the next move that is only useful when a game is near completion.

A proper chess engine will use advanced heuristics along with hard coded solutions to certain patterns to ensure that it can make a good move in a reasonable amount of time. CheckmateFinder is not a chess engine, it is not even guarenteed to provide you with a move. CheckmateFinder is primarily a proof of concept, and was made out of my frustration with a puzzle that could be solved much easier by a computer (or somebody reasonably good at chess).

## How it works
CheckmateFinder under the hood tries to find a move, where all possible opponent counter-moves result in a new board state where a new move can be performed by the player that results in a new board state where this process is recursed upon to prove that in the end, there are no moves the opponent can make to escape a checkmate given optimal play. This process recurses to a max depth, where if there still exists a move for the opponent to make that gets them out of check, the program terminates without a returned move. CheckmateFinder only returns a move when it has proven that no sequence of opponent counter moves can ultimately get them out of an inevitable checkmate.

It is easier to understand how CheckmateFinder works with a visual.

![graph](https://user-images.githubusercontent.com/38389408/225073488-0eed7c2a-ff1c-4b13-97d2-2c3c4f978450.png)

In this tree, blue dots represent game states. The leftmost dot is the initial game state. Assuming it's white's turn, each white line represents a move that white can make. If we are still below the check depth, we consider all possible moves that white can play. If we are above the check depth, only check moves are considered. Each black line is a valid move for black. To have a forced checkmate, the entire tree must have this recursive property:

(Initial state is at depth 0)
- A leaf who is at an odd depth (blacks's turn), is a forced checkmate. (black has no possible moves)
- A subtree whose root is at an odd depth (black's turn), and whose branches are exclusively forced checkmates, is also a forced checkmate. (any of black's moves still result in a forced checkmate)
- A subtree whose root is at an even depth (white's turn), and whose branches contain at least one forced checkmate, is also a forced checkmate. (there exists a move for white that forces checkmate on black given optimal play)

This example tree does have this recursive property, and so it represents a forced checkmate. In fact, for this example, any of white's initial moves can lead to a forced checkmate.

## Verifying compliance with chess rules (en passant, promotion, castling)
To verify that CheckmateFinder had no bugs regarding compliance with the rules of chess, [Perft](https://www.chessprogramming.org/Perft) was used to verify, from an initial game state, that the calculated number of possible moves for a limited depth matched that of a known-good engine. This was tested for 6 initial game states, and led to the discovery of a few bugs that would have likely gone unnoticed. 
