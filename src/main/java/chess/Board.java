package chess;

import util.LinkedStack;

import java.util.ArrayList;
import java.util.List;

public
class Board
{

    public
    record MoveTreeGenerationNode(MoveTreeNode moveTreeNode, int minMovesToForceMate)
    {

    }

    public
    record ForceMateMove(Move move, int minMovesToForceMate)
    {

    }

    private final Piece[][]               board  = new Piece[8][8];
    private       Team                    turn;
    private       BoardPos                enTar;
    private       boolean                 canWhiteQueenCastle;
    private       boolean                 canWhiteKingCastle;
    private       boolean                 canBlackQueenCastle;
    private       boolean                 canBlackKingCastle;
    private final LinkedStack<BoardState> states = new LinkedStack<>();

    public
    Board(Team turn, BoardPos enTar, boolean canWhiteQueenCastle, boolean canWhiteKingCastle,
          boolean canBlackQueenCastle, boolean canBlackKingCastle)
    {
        this.turn                = turn;
        this.enTar               = enTar;
        this.canWhiteQueenCastle = canWhiteQueenCastle;
        this.canWhiteKingCastle  = canWhiteKingCastle;
        this.canBlackQueenCastle = canBlackQueenCastle;
        this.canBlackKingCastle  = canBlackKingCastle;
    }

    public
    boolean canQueenSideCastle(Team team)
    {
        if (team.equals(Team.WHITE))
        {
            return this.canWhiteQueenCastle;
        }
        else if (team.equals(Team.BLACK))
        {
            return this.canBlackQueenCastle;
        }

        return false;
    }

    public
    boolean canKingSideCastle(Team team)
    {
        if (team.equals(Team.WHITE))
        {
            return this.canWhiteKingCastle;
        }
        else if (team.equals(Team.BLACK))
        {
            return this.canBlackKingCastle;
        }

        return false;
    }

    public
    void setPieceAt(int row, int col, Piece piece)
    {
        if (this.inBounds(row, col))
        {
            this.board[row][col] = piece;

            if (piece != null)
            {
                piece.updatePos(row, col);
            }
        }
    }

    public
    BoardPos getEnTar()
    {
        return this.enTar;
    }

    public
    void setPieceAt(BoardPos pos, Piece piece)
    {
        this.setPieceAt(pos.getRow(), pos.getCol(), piece);
    }

    public
    boolean inBounds(int row, int col)
    {
        return (row >= 0 && row <= (7)) && (col >= 0 && col <= (7));
    }

    public
    Piece getPieceAt(int row, int col)
    {
        return this.board[row][col];
    }

    public
    Piece getPieceAt(BoardPos pos)
    {
        return this.getPieceAt(pos.getRow(), pos.getCol());
    }

    public
    boolean tileInAttack(int row, int col, Team team)
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece piece = this.getPieceAt(r, c);

                if (piece != null && !piece.getTeam().equals(team))
                {
                    for (BoardPos pos : piece.getSpan())
                    {
                        if (pos.getRow() == row && pos.getCol() == col)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public
    boolean tileInAttack(BoardPos pos, Team team)
    {
        return this.tileInAttack(pos.getRow(), pos.getCol(), team);
    }

    private
    boolean isCheckMove(Move move)
    {
        this.executeMove(move);

        boolean check = this.isInCheck(this.turn);

        this.reverseMove(move);

        return check;
    }

    public
    void addMoveIfNoCheck(List<Move> moves, Move move)
    {
        Team team = this.turn;

        this.executeMove(move);

        if (!this.isInCheck(team))
        {
            moves.add(move);
        }

        this.reverseMove(move);
    }

    public
    List<Move> getAllMoves()
    {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece piece = this.getPieceAt(r, c);

                if (piece != null && piece.getTeam().equals(this.turn))
                {
                    moves.addAll(piece.getLegalMoves());
                }
            }
        }

        return moves;
    }

    public
    List<Move> getAllCheckMoves()
    {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece piece = this.getPieceAt(r, c);

                if (piece != null && piece.getTeam().equals(this.turn))
                {
                    for (Move move : piece.getLegalMoves())
                    {
                        if (this.isCheckMove(move))
                        {
                            moves.add(move);
                        }
                    }
                }
            }
        }

        return moves;
    }

    public
    long numMoves(int maxDepth)
    {
        return numMoves(0, maxDepth);
    }

    public
    MoveTree getMoveTree(int maxDepth, int checkDepth, boolean skipWrongMoves, int skipSuboptimalMovesDepth)
    {
        return this.getMoveTree(maxDepth, checkDepth, null, skipWrongMoves, skipSuboptimalMovesDepth);
    }

    public
    MoveTree getMoveTree(int maxDepth, int checkDepth, Move startMove, boolean skipWrongMoves,
                         int skipSuboptimalMovesDepth)
    {
        MoveTreeGenerationNode moveTreeGenerationNode = this.generateMoveTree(0, maxDepth, checkDepth, startMove,
            skipWrongMoves, skipSuboptimalMovesDepth);
        MoveTree moveTree = new MoveTree(moveTreeGenerationNode.moveTreeNode, this.turn.equals(Team.WHITE));

        this.convertMovesToMoveStrings(moveTree.root);
        return moveTree;
    }

    private
    void convertMovesToMoveStrings(MoveTreeNode moveTreeNode)
    {
        if (moveTreeNode == null)
        {
            return;
        }
        moveTreeNode.moveStrings = new ArrayList<>();
        if (moveTreeNode.moves.size() > 0 && !moveTreeNode.nextNodes.get(0).escaped)
        { // don't generate move strings for escaped paths
            List<Move> allMoves = this.getAllMoves();
            for (int i = 0; i < moveTreeNode.moves.size(); i++)
            {
                Move         move             = moveTreeNode.moves.get(i);
                MoveTreeNode nextMoveTreeNode = moveTreeNode.nextNodes.get(i);
                moveTreeNode.moveStrings.add(this.getAlgebraicNotation(move, allMoves));
                this.executeMove(move);
                this.convertMovesToMoveStrings(nextMoveTreeNode);
                this.reverseMove(move);
            }
        }
        moveTreeNode.moves = null;
    }

    private
    String resolveAlgebraicNotationAmbiguity(Move move, boolean isCheck, boolean isCheckmate, List<Move> allMoves)
    {
        Piece   piece      = this.getPieceAt(move.from);
        boolean specifyRow = false;
        boolean specifyCol = false;
        if (piece instanceof PiecePawn || piece instanceof PieceKing)
        {
            return "";
        }
        for (Move otherMove : allMoves)
        {
            if (move.from.equals(otherMove.from))
            {
                continue;
            }
            if (!move.to.equals(otherMove.to))
            {
                continue;
            }
            Piece otherPiece = this.getPieceAt(otherMove.from);
            if (!piece.getClass().equals(otherPiece.getClass()))
            {
                continue;
            }
            this.executeMove(otherMove);
            boolean otherMoveIsCheck     = this.isInCheck(this.turn);
            boolean otherMoveIsCheckmate = otherMoveIsCheck && this.getAllMoves().size() == 0;
            this.reverseMove(otherMove);
            if (isCheck != otherMoveIsCheck || isCheckmate != otherMoveIsCheckmate)
            {
                continue;
            }
            // if we make it here, there is ambiguity between `move` and `otherMove`
            if (move.from.getCol() == otherMove.from.getCol())
            {
                specifyRow = true;
            }
            else if (move.from.getRow() == otherMove.from.getRow())
            {
                specifyCol = true;
            }
        }
        return (specifyCol ? move.from.getFile() : "") + (specifyRow ? move.from.getRank() : "");
    }

    private
    String getCastleNotation(Move move)
    {
        return move.joint.from.getCol() == 0 ? "0-0-0" : "0-0";
    }

    public
    String getAlgebraicNotation(Move move, List<Move> allMoves)
    {
        if (move.joint != null)
        {
            return this.getCastleNotation(move);
        }
        Piece   piece     = this.getPieceAt(move.from);
        boolean isCapture = move.take != null;
        this.executeMove(move);
        boolean isCheck     = this.isInCheck(this.turn);
        boolean isCheckmate = isCheck && this.getAllMoves().size() == 0;
        this.reverseMove(move);
        String pieceLabel = piece instanceof PiecePawn ? (isCapture ? String.valueOf(move.from.getFile()) : "")
                                                       : String.valueOf(piece.getLabel()).toUpperCase();
        String ambiguityString   = this.resolveAlgebraicNotationAmbiguity(move, isCheck, isCheckmate, allMoves);
        String captureString     = isCapture ? "x" : "";
        String destinationString = move.to.toString();
        String checkString       = !isCheck ? "" : (isCheckmate ? "#" : "+");
        return pieceLabel + ambiguityString + captureString + destinationString + checkString;
    }

    public
    List<ForceMateMove> getForceMateMoves(int maxDepth, int checkDepth)
    {
        List<Move>          moves          = checkDepth > 0 ? this.getAllMoves() : this.getAllCheckMoves();
        List<ForceMateMove> forceMateMoves = new ArrayList<>();

        for (Move move : moves)
        {
            System.out.println("Analyzing move: " + move);
            this.executeMove(move);

            int minMovesToForceMate = this.isForcedMate(1, maxDepth, checkDepth);

            this.reverseMove(move);

            if (minMovesToForceMate != Integer.MAX_VALUE)
            {
                forceMateMoves.add(new ForceMateMove(move, minMovesToForceMate + 1));
            }
        }

        return forceMateMoves;
    }

    public
    int isForcedMate(int depth, int maxDepth, int checkDepth)
    {
        if (depth == maxDepth)
        {
            return Integer.MAX_VALUE;
        }

        if (depth % 2 == 1) // opponent's turn
        {
            int        minMovesToForceMate = 0;
            List<Move> moves               = this.getAllMoves();

            if (moves.size() == 0)
            {
                return this.isInCheck(this.turn) ? 0 : Integer.MAX_VALUE;
            }

            for (Move move : moves)
            {
                this.executeMove(move);

                int minMovesToForceMateNextDepth = this.isForcedMate(depth + 1, maxDepth, checkDepth);

                this.reverseMove(move);

                if (minMovesToForceMateNextDepth == Integer.MAX_VALUE)
                {
                    return Integer.MAX_VALUE;
                }

                minMovesToForceMate = Math.max(minMovesToForceMate, minMovesToForceMateNextDepth);
            }

            return minMovesToForceMate;
        }
        else // your turn
        {
            int        minMovesToForceMate = Integer.MAX_VALUE;
            List<Move> moves               = depth < checkDepth ? this.getAllMoves() : this.getAllCheckMoves();

            for (Move move : moves)
            {
                this.executeMove(move);

                int minMovesToForceMateNextDepth = this.isForcedMate(depth + 1, maxDepth, checkDepth);

                this.reverseMove(move);

                minMovesToForceMate = Math.min(minMovesToForceMate, minMovesToForceMateNextDepth);
            }

            return minMovesToForceMate == Integer.MAX_VALUE ? Integer.MAX_VALUE : minMovesToForceMate + 1;
        }
    }

    public
    MoveTreeGenerationNode generateMoveTree(int depth, int maxDepth, int checkDepth, Move startMove,
                                            boolean skipWrongMoves, int skipSuboptimalMovesDepth)
    {
        if (depth == maxDepth)
        {
            return new MoveTreeGenerationNode(skipWrongMoves ? null : new MoveTreeNode(true, null, null, null),
                Integer.MAX_VALUE);
        }

        List<MoveTreeNode> moveTreeNodes             = new ArrayList<>();
        List<Move>         moveTreeMoves             = new ArrayList<>();
        List<Boolean>      isMoveTreeForcedCheckmate = new ArrayList<>();
        MoveTreeGenerationNode failedMoveTreeGenerationNode = new MoveTreeGenerationNode(
            skipWrongMoves ? null : new MoveTreeNode(false, moveTreeMoves, isMoveTreeForcedCheckmate, moveTreeNodes),
            Integer.MAX_VALUE);

        if (depth % 2 == 1) // opponent's turn
        {
            List<Move> moves = this.getAllMoves();

            if (moves.size() == 0)
            {
                if (this.isInCheck(this.turn))
                {
                    return new MoveTreeGenerationNode(
                        new MoveTreeNode(false, moveTreeMoves, isMoveTreeForcedCheckmate, moveTreeNodes), 0);
                }

                return failedMoveTreeGenerationNode;
            }

            int minMovesToForceMate = 0;

            for (Move move : moves)
            {
                this.executeMove(move);

                MoveTreeGenerationNode moveTreeGenerationNode = this.generateMoveTree(depth + 1, maxDepth, checkDepth,
                    null, skipWrongMoves, skipSuboptimalMovesDepth);
                int     minMovesToForceMateNextDepth = moveTreeGenerationNode.minMovesToForceMate;
                boolean doesMoveForceMate            = moveTreeGenerationNode.minMovesToForceMate != Integer.MAX_VALUE;

                this.reverseMove(move);

                if (!doesMoveForceMate && skipWrongMoves)
                {
                    return new MoveTreeGenerationNode(null, Integer.MAX_VALUE);
                }

                minMovesToForceMate = Math.max(minMovesToForceMate, minMovesToForceMateNextDepth);

                moveTreeNodes.add(moveTreeGenerationNode.moveTreeNode);
                moveTreeMoves.add(move);
                isMoveTreeForcedCheckmate.add(doesMoveForceMate);
            }

            return new MoveTreeGenerationNode(
                new MoveTreeNode(false, moveTreeMoves, isMoveTreeForcedCheckmate, moveTreeNodes), minMovesToForceMate);
        }
        else // your turn
        {
            List<Move>   moves;
            int          minMovesToForceMate = Integer.MAX_VALUE;
            boolean      onlyOptimalMove     = depth >= skipSuboptimalMovesDepth;
            Move         optimalMove         = null;
            MoveTreeNode optimalMoveTreeNode = null;

            moves = startMove != null ? List.of(startMove)
                                      : (checkDepth > 0 ? this.getAllMoves() : this.getAllCheckMoves());

            if (moves.size() == 0)
            {
                return failedMoveTreeGenerationNode;
            }

            for (Move move : moves)
            {
                this.executeMove(move);

                MoveTreeGenerationNode moveTreeGenerationNode = this.generateMoveTree(depth + 1, maxDepth, checkDepth,
                    null, skipWrongMoves, skipSuboptimalMovesDepth);
                int     minMovesToForceMateNextDepth = moveTreeGenerationNode.minMovesToForceMate;
                boolean doesMoveForceMate            = moveTreeGenerationNode.minMovesToForceMate != Integer.MAX_VALUE;

                this.reverseMove(move);

                if (!skipWrongMoves || (doesMoveForceMate && !onlyOptimalMove))
                {
                    moveTreeNodes.add(moveTreeGenerationNode.moveTreeNode);
                    moveTreeMoves.add(move);
                    isMoveTreeForcedCheckmate.add(doesMoveForceMate);
                }

                if (minMovesToForceMateNextDepth <= minMovesToForceMate)
                {
                    optimalMove         = move;
                    optimalMoveTreeNode = moveTreeGenerationNode.moveTreeNode;
                    minMovesToForceMate = minMovesToForceMateNextDepth;
                }
            }

            if (onlyOptimalMove && Integer.MAX_VALUE != minMovesToForceMate)
            {
                moveTreeNodes.add(optimalMoveTreeNode);
                moveTreeMoves.add(optimalMove);
                isMoveTreeForcedCheckmate.add(true);
            }

            if (minMovesToForceMate == Integer.MAX_VALUE)
            {
                return failedMoveTreeGenerationNode;
            }

            return new MoveTreeGenerationNode(
                new MoveTreeNode(false, moveTreeMoves, isMoveTreeForcedCheckmate, moveTreeNodes),
                minMovesToForceMate + 1);
        }
    }

    public
    long numMoves(int depth, int maxDepth)
    {
        if (depth == maxDepth)
        {
            return 1;
        }

        long moves = 0;

        for (Move move : this.getAllMoves())
        {
            this.executeMove(move);

            moves += numMoves(depth + 1, maxDepth);

            this.reverseMove(move);
        }

        return moves;
    }

    private
    void restore(Piece piece)
    {
        if (piece != null)
        {
            this.setPieceAt(piece.getPos(), piece);
        }
    }

    private
    void toggleTurn()
    {
        if (this.turn.equals(Team.WHITE))
        {
            this.turn = Team.BLACK;
        }
        else if (this.turn.equals(Team.BLACK))
        {
            this.turn = Team.WHITE;
        }
    }

    public
    void reverseMove(Move move)
    {
        BoardState state  = this.states.pop();
        Piece      toMove = state.oldPiece;

        this.toggleTurn();
        this.setPieceAt(move.from, toMove);
        this.setPieceAt(move.to, null);

        if (move.joint != null)
        { // only castling
            toMove = this.getPieceAt(move.joint.to);

            this.setPieceAt(move.joint.from, toMove);
            this.setPieceAt(move.joint.to, null);
        }

        this.restore(state.taken);

        this.enTar               = state.enPas;
        this.canWhiteQueenCastle = state.canWhiteQueenCastle;
        this.canWhiteKingCastle  = state.canWhiteKingCastle;
        this.canBlackQueenCastle = state.canBlackQueenCastle;
        this.canBlackKingCastle  = state.canBlackKingCastle;
    }

    private
    void executeMove(Move move)
    {
        Piece    oldPiece                = this.getPieceAt(move.from);
        BoardPos lastEnTar               = this.enTar;
        boolean  lastCanWhiteQueenCastle = this.canWhiteQueenCastle;
        boolean  lastCanWhiteKingCastle  = this.canWhiteKingCastle;
        boolean  lastCanBlackQueenCastle = this.canBlackQueenCastle;
        boolean  lastCanBlackKingCastle  = this.canBlackKingCastle;
        Piece    taken                   = null;

        if (move.take != null)
        {
            taken = this.getPieceAt(move.take);
        }

        this.states.push(new BoardState(lastEnTar, oldPiece, taken, lastCanWhiteQueenCastle, lastCanWhiteKingCastle,
            lastCanBlackQueenCastle, lastCanBlackKingCastle));

        Piece toMove = this.getPieceAt(move.from);
        this.enTar = null;

        this.toggleTurn();

        if (toMove instanceof PiecePawn)
        { // en passant target square
            if (toMove.getPos().getRow() == 1 && toMove.getTeam().equals(Team.WHITE))
            {
                if (move.to.getRow() == 3)
                {
                    this.enTar = new BoardPos(2, toMove.getPos().getCol());
                }
            }
            else if (toMove.getPos().getRow() == 6 && toMove.getTeam().equals(Team.BLACK))
            {
                if (move.to.getRow() == 4)
                {
                    this.enTar = new BoardPos(5, toMove.getPos().getCol());
                }
            }
        }

        if (toMove instanceof PieceKing)
        {
            if (toMove.getTeam().equals(Team.WHITE))
            {
                this.canWhiteQueenCastle = false;
                this.canWhiteKingCastle  = false;
            }
            else if (toMove.getTeam().equals(Team.BLACK))
            {
                this.canBlackQueenCastle = false;
                this.canBlackKingCastle  = false;
            }
        }
        else if (toMove instanceof PieceRook)
        {
            if (toMove.getTeam().equals(Team.WHITE))
            {
                if (this.canWhiteQueenCastle)
                {
                    if (toMove.getPos().getCol() == 0 && toMove.getPos().getRow() == 0)
                    {
                        this.canWhiteQueenCastle = false;
                    }
                }

                if (this.canWhiteKingCastle)
                {
                    if (toMove.getPos().getCol() == 7 && toMove.getPos().getRow() == 0)
                    {
                        this.canWhiteKingCastle = false;
                    }
                }
            }
            else if (toMove.getTeam().equals(Team.BLACK))
            {
                if (this.canBlackQueenCastle)
                {
                    if (toMove.getPos().getCol() == 0 && toMove.getPos().getRow() == 7)
                    {
                        this.canBlackQueenCastle = false;
                    }
                }

                if (this.canBlackKingCastle)
                {
                    if (toMove.getPos().getCol() == 7 && toMove.getPos().getRow() == 7)
                    {
                        this.canBlackKingCastle = false;
                    }
                }
            }
        }

        switch (move.promotion)
        {
            case 1:
                toMove = new PieceQueen(toMove);

                break;
            case 2:
                toMove = new PieceRook(toMove);

                break;
            case 3:
                toMove = new PieceBishop(toMove);

                break;
            case 4:
                toMove = new PieceKnight(toMove);

                break;
        }

        if (move.take != null)
        {
            Piece piece = this.getPieceAt(move.take);

            if (piece instanceof PieceRook)
            {
                if (piece.getTeam().equals(Team.WHITE))
                {
                    if (this.canWhiteQueenCastle)
                    {
                        if (piece.getPos().getCol() == 0 && piece.getPos().getRow() == 0)
                        {
                            this.canWhiteQueenCastle = false;
                        }
                    }

                    if (this.canWhiteKingCastle)
                    {
                        if (piece.getPos().getCol() == 7 && piece.getPos().getRow() == 0)
                        {
                            this.canWhiteKingCastle = false;
                        }
                    }
                }
                else if (piece.getTeam().equals(Team.BLACK))
                {
                    if (this.canBlackQueenCastle)
                    {
                        if (piece.getPos().getCol() == 0 && piece.getPos().getRow() == 7)
                        {
                            this.canBlackQueenCastle = false;
                        }
                    }

                    if (this.canBlackKingCastle)
                    {
                        if (piece.getPos().getCol() == 7 && piece.getPos().getRow() == 7)
                        {
                            this.canBlackKingCastle = false;
                        }
                    }
                }
            }

            this.setPieceAt(move.take, null);
        }

        this.setPieceAt(move.from, null); // set original pos to null
        this.setPieceAt(move.to, toMove);

        if (move.joint != null)
        { // only castling right now
            toMove = this.getPieceAt(move.joint.from);

            this.setPieceAt(move.joint.from, null); // set original pos to null
            this.setPieceAt(move.joint.to, toMove);
        }
    }

    private
    boolean isInCheck(Team team)
    {
        Piece king = null;

        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece piece = this.getPieceAt(r, c);

                if (piece instanceof PieceKing && piece.getTeam().equals(team))
                {
                    king = piece;

                    break;
                }
            }
        }

        if (king != null)
        {
            return this.tileInAttack(king.getPos(), team);
        }

        return false;
    }

    @Override
    public
    String toString()
    {
        StringBuilder stringBuilder = new StringBuilder("\n");

        for (int r = 7; r >= 0; r--)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece piece = this.getPieceAt(r, c);
                char  label;

                if (piece != null)
                {
                    label = piece.getLabel();
                }
                else
                {
                    label = ' ';
                }

                stringBuilder.append(label);
            }

            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }

    public
    String toString(boolean reversed)
    {
        if (reversed)
        {
            return new StringBuilder(this.toString()).reverse().toString();
        }
        else
        {
            return this.toString();
        }
    }

    public static
    Board getBoardFromFEN(String fen)
    {
        return getBoardFromFEN(fen, false);
    }

    public static
    Board getBoardFromFEN(String fen, boolean mirrored)
    {
        Board    board;
        String[] args                = fen.split(" ");
        boolean  canWhiteQueenCastle = false;
        boolean  canWhiteKingCastle  = false;
        boolean  canBlackQueenCastle = false;
        boolean  canBlackKingCastle  = false;
        String   castle              = args[2];

        if (castle.contains("Q"))
        {
            canWhiteQueenCastle = true;
        }

        if (castle.contains("K"))
        {
            canWhiteKingCastle = true;
        }

        if (castle.contains("q"))
        {
            canBlackQueenCastle = true;
        }

        if (castle.contains("k"))
        {
            canBlackKingCastle = true;
        }

        String teamStr = args[1];
        Team   team;

        if ("b".equals(teamStr))
        {
            team = Team.BLACK;
        }
        else
        {
            team = Team.WHITE;
        }

        BoardPos enPas;
        String   enPasStr = args[3];

        if (enPasStr.equals("-"))
        {
            enPas = null;
        }
        else
        {
            enPas = BoardPos.getPosFromStr(enPasStr);
        }

        board = new Board(team, enPas, canWhiteQueenCastle, canWhiteKingCastle, canBlackQueenCastle,
            canBlackKingCastle);

        int r;
        int c;
        int rOffset;
        int cOffset;
        int startC;

        if (mirrored)
        {
            r       = 0;
            startC  = 7;
            c       = startC;
            rOffset = 1;
            cOffset = -1;
        }
        else
        {
            r       = 7;
            startC  = 0;
            c       = startC;
            rOffset = -1;
            cOffset = 1;
        }

        for (int i = 0; i < args[0].length(); i++)
        {
            char piece = args[0].charAt(i);

            switch (piece)
            {
                case '/':
                    r += rOffset;
                    c = startC;

                    break;
                case 'R':
                    board.setPieceAt(r, c, new PieceRook(board, Team.WHITE));
                    c += cOffset;

                    break;
                case 'r':
                    board.setPieceAt(r, c, new PieceRook(board, Team.BLACK));
                    c += cOffset;

                    break;
                case 'N':
                    board.setPieceAt(r, c, new PieceKnight(board, Team.WHITE));
                    c += cOffset;

                    break;
                case 'n':
                    board.setPieceAt(r, c, new PieceKnight(board, Team.BLACK));
                    c += cOffset;

                    break;
                case 'B':
                    board.setPieceAt(r, c, new PieceBishop(board, Team.WHITE));
                    c += cOffset;

                    break;
                case 'b':
                    board.setPieceAt(r, c, new PieceBishop(board, Team.BLACK));
                    c += cOffset;

                    break;
                case 'Q':
                    board.setPieceAt(r, c, new PieceQueen(board, Team.WHITE));
                    c += cOffset;

                    break;
                case 'q':
                    board.setPieceAt(r, c, new PieceQueen(board, Team.BLACK));
                    c += cOffset;

                    break;
                case 'K':
                    board.setPieceAt(r, c, new PieceKing(board, Team.WHITE));
                    c += cOffset;

                    break;
                case 'k':
                    board.setPieceAt(r, c, new PieceKing(board, Team.BLACK));
                    c += cOffset;

                    break;
                case 'P':
                    board.setPieceAt(r, c, new PiecePawn(board, Team.WHITE));
                    c += cOffset;

                    break;
                case 'p':
                    board.setPieceAt(r, c, new PiecePawn(board, Team.BLACK));
                    c += cOffset;

                    break;
                default:
                    int numSpaces = Integer.parseInt("" + piece);

                    for (int s = 0; s < numSpaces; s++)
                    {
                        c += cOffset;
                    }

                    break;
            }
        }

        return board;
    }

}
