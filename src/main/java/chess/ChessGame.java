package chess;

public
class ChessGame
{

    public static
    void main(String[] args)
    {
        new ChessGame();
    }

    public
    ChessGame()
    {
        //        String[] testcases = new String[] {
        //            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -",
        //            "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -",
        //            "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -",
        //            "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -",
        //            "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ -",
        //            "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - -",
        //        };
        //
        //        int[] testcasesRange = new int[] {
        //            7, 6, 8, 6, 6, 6
        //        };
        //
        //        for (int t = 0; t < testcases.length; t++) {
        //            String string = testcases[t];
        //            Board board = Board.getBoardFromFEN(string);
        //
        //            //System.out.println(board);
        //
        //            for (int i = 0; i < testcasesRange[t]; i++) {
        //                System.out.println(board.numMoves(i));
        //            }
        //        }
        //
        //        1
        //        20
        //        400
        //        8902
        //        197281
        //        4865609
        //        119060324
        //        1
        //        48
        //        2039
        //        97862
        //        4085603
        //        193690690
        //        1
        //        14
        //        191
        //        2812
        //        43238
        //        674624
        //        11030083
        //        178633661
        //        1
        //        6
        //        264
        //        9467
        //        422333
        //        15833292
        //        706045033
        //        1
        //        44
        //        1486
        //        62379
        //        2103487
        //        89941194
        //        1
        //        46
        //        2079
        //        89890
        //        3894594
        //        164075551

//        Board board = Board.getBoardFromFEN("rn1r2k1/1pq2p1p/p2p1bpB/3P4/P3Q3/2PB4/5PPP/2R1R1K1 w - -", false);
//        Board board = Board.getBoardFromFEN("3k4/6p1/3K1pPp/3PrP2/p7/P7/3Q3P/1q6 w - -");
//        Board board = Board.getBoardFromFEN("8/2R3pk/n4b1p/3B4/8/4P1P1/5PKP/1r6 w - -");
//        Board board = Board.getBoardFromFEN("1KBR4/1P3qPP/P7/Q4P2/3P4/1prk1b1p/2p3p1/8 b - -", true);
//        Board board = Board.getBoardFromFEN("8/4p3/1B6/2N5/2k5/1R4K1/8/7B w - -");
        Board board = Board.getBoardFromFEN("rn1r2k1/1pq2p1p/p2p1bpB/3P4/P3Q3/2PB4/5PPP/2R1R1K1 w - -", false);
        System.out.println(board.toString(false));

        for (Move move : board.getForceMateMoves(4, 0))
        {
            System.out.println("First move: " + move);
        }
    }

}
