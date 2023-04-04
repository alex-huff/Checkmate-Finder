# CheckmateFinder
## About
CheckmateFinder is a Java application that can find forced checkmates ("mate in n") given an initial board state. A "forced" checkmate means that one side, given optimal play, can force the opponent into a checkmate. This often occurs towards the end of a chess match, and can be surpisingly difficult to find. My original motivation to make CheckmateFinder came from chess puzzles.

By default, CheckmateFinder will try to provide the first move in a forced checkmate sequence, and the maximum moves till checkmate if played correctly. CheckmateFinder can also generate a JSON representation of the move tree which can be supplied to the included chess-grapher.py script in order to generate a graphic representation of the tree.

CheckmateFinder can be controlled from the command line by providing the [FEN (Forsyth–Edwards Notation)](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) of the game with the --fen argument. Along with the FEN, a depth must be provided (with --depth) that tells CheckmateFinder how many moves ahead to search. Another parameter, the "check depth," must also be provided (with --check-depth), that tells CheckmateFinder how many turns it should consider non-check moves (higher values drastically increases the search time). The higher the depths, the more likely it is that CheckmateFinder will find a forced mate. With higher depths also comes increased search time. CheckmateFinder is not an optimized program, but an absolute brute-force approach to calculating moves that is only useful when a game is near completion.

A proper chess engine will use advanced heuristics along with hard-coded solutions to certain patterns to ensure that it can make a good move in a reasonable amount of time. CheckmateFinder is not a chess engine, it is not even guaranteed to provide you with a move. CheckmateFinder is primarily a proof of concept, and was made out of my frustration with a puzzle that could be solved much easier by a computer (or somebody reasonably good at chess).

## How it works
It is easiest to understand how CheckmateFinder works with a visual:

![graph](https://user-images.githubusercontent.com/38389408/225073488-0eed7c2a-ff1c-4b13-97d2-2c3c4f978450.png)

In this tree, blue dots represent game states. The leftmost dot is the initial game state. Assuming it's white's turn, each white line represents a move that white can make. If we are still below the check depth, we consider all possible moves that white can play. If we are above the check depth, only check moves are considered. Black lines are all valid moves that black can play. To have a forced checkmate, the entire tree must have this recursive property:

(Initial state is at depth 0)
- A leaf who is at an odd depth (opponent's turn), is a forced checkmate if the opponent is in check. (opponent has no possible moves and is in check: checkmate.)
- A subtree whose root is at an odd depth (opponent's turn), and whose branches are exclusively forced checkmates, is also a forced checkmate. (any of the opponent's moves still result in a forced checkmate)
- A subtree whose root is at an even depth (your's turn), and whose branches contain at least one forced checkmate, is also a forced checkmate. (there exists a move for you that forces checkmate on your opponent given optimal play)

This example tree does have this recursive property, and so it represents a forced checkmate. In fact, for this example, any of white's initial moves can lead to a forced checkmate.

## In Action
Let's start with a trivial example:

<table width=100%>
  <tr>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/225085853-4a9d9eb6-b45c-417f-9704-7d090a764d91.png" alt="Initial board state">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/225085872-e71a97d7-980c-4409-b977-08678b9c5fc9.png" alt="White: Qe8+">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/225085957-d1b24bde-f5b4-4ffe-95be-835667d0aeab.png" alt="Black: Rxe8">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/225085994-9b70fe88-3952-4567-bdb1-1c84e1050e0a.png" alt="White: Rxe8#">
    </td>
  </tr>
  <tr>
    <td align=center>
      Initial board state
    </td>
    <td align=center>
      White: Qe8+
    </td>
    <td align=center>
      Black: Rxe8
    </td>
    <td align=center>
      White: Rxe8#
    </td>
  </tr>
</table>

We can use CheckmateFinder to generate a tree that tells us how to checkmate from this initial state. We do this by running `checkmate-finder` to generate a JSON representation of the move tree, which we then pipe into the python graphing program.

Here is the command that generates the graph below:

```shell
java -jar checkmate-finder.jar --fen 'rn1r2k1/1pq2p1p/p2p1bpB/3P4/P3Q3/2PB4/5PPP/2R1R1K1 w - -' \
--depth 4 --check-depth 0 --generate-move-tree | python chess-grapher.py \
--font <path to TTF or OTF Font> --width 1600 --height 900 --max-text-box-height 40 --show-image
```

![m2](https://user-images.githubusercontent.com/38389408/229237680-1010b1a4-09be-4fe5-9f93-f432aeef733f.png)

In this diagram, the red-outlined paths represent moves that lead to a forced checkmate. The purple paths at the end represent the algorithm giving up because maximum depth was exceeded. The green `#`s surround states where the opponent is in checkmate. We can see that the entire `Qe8+ -> Rxe8 -> Rxe8#` sequence that we just played is highlighted red and fits within the maximum depth. But, `Qxg6+` will not work as a first move since both `fxg6` and `hxg6` lead black to escape checkmate (for this search depth).

Here is a more complicated match from thechessworld.com's [3 Hardest Mate-in-4 ever: L. Knotec, “Cekoslovensky Sach”, 1947](https://thechessworld.com/articles/problems/3-hardest-mate-in-4-ever/):

<table width=100%>
  <tr>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754781-68975798-7e3f-4b0e-ac6a-c6a16d66544a.png" alt="Initial board state">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754780-8e95a97f-48ba-4a8b-8231-b624c2f79b5b.png" alt="White: Kg4">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754779-df60cc7b-65fd-4a27-b31d-27860f33fef2.png" alt="Black: Kd4">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754778-4d492949-abda-4212-bf0e-f68dd9c39622.png" alt="White: Rd3+">
    </td>
  </tr>
  <tr>
    <td align=center>
      Initial board state
    </td>
    <td align=center>
      White: Kg4
    </td>
    <td align=center>
      Black: Kd4
    </td>
    <td align=center>
      White: Rd3+
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754777-0b1e7cdd-7ddf-4fcb-8279-6182dadc8006.png" alt="Black: Ke5">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754775-911b3a9d-889b-4cfa-9122-b8554f39e2ee.png" alt="White: Kg5">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754774-f43a8a17-ffde-4c92-9284-7a24328a8e0b.png" alt="Black: e6">
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/38389408/227754773-a26d3d57-231e-4f76-b996-478b5e987a91.png" alt="White: Nd7#">
    </td>
  </tr>
  <tr>
    <td align=center>
      Black: Ke5
    </td>
    <td align=center>
      White: Kg5
    </td>
    <td align=center>
      Black: e6
    </td>
    <td align=center>
      White: Nd7#
    </td>
  </tr>
</table>

This was an example of how this game might be played, but what if black plays different moves? We can use CheckmateFinder to solve this mate in 4 by running the following command:

```shell
java -jar checkmate-finder.jar --fen '8/4p3/1B6/2N5/2k5/1R4K1/8/7B w - -' \
--depth 8 --check-depth 5 --generate-move-tree --skip-wrong-moves | python \
chess-grapher.py --font <path to TTF or OTF Font> --width 1600 --height 1200 \
--max-text-box-height 45 --dont-highlight-force-mate --show-image
```

![m4](https://user-images.githubusercontent.com/38389408/229238060-291ada09-fbd6-462d-8074-7f568f523b3f.png)

Note: this tree is omitting all paths that don't directly lead to a forced mate (--skip-wrong-moves). It would be absolutely massive if all paths were included.

## Verifying compliance with chess rules (en passant, promotion, castling)
To hunt down bugs in CheckmateFinder regarding compliance with the rules of chess, [Perft](https://www.chessprogramming.org/Perft) was used to verify, from an initial game state, that the calculated number of possible moves for a limited depth matched that of a known-good engine. This was tested for 6 initial game states, and led to the discovery of a few bugs that would have likely gone unnoticed.
