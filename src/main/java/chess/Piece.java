package chess;

import util.Offset;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

    protected Board board;
    private Team team;
    private BoardPos pos;

    public Piece(Board board, Team team, BoardPos pos) {
        this.board = board;
        this.team = team;
        this.pos = pos;
    }

    public Piece(Board board, Team team) {
        this(board, team, new BoardPos(0, 0));
    }

    public Piece(Piece piece) {
        this(piece.board, piece.team, new BoardPos(piece.pos));
    }

    public List<Move> getLegalMoves() {
        List<Move> moves = this.getLegalSpan();
        List<Move> specialMoves = this.getSpecialMoves();

        if (specialMoves != null) moves.addAll(this.getSpecialMoves());

        return moves;
    }

    public abstract char getLabel();

    public abstract List<Move> getSpecialMoves();

    protected List<Move> getLegalSpan() {
        List<Move> moves = new ArrayList<>();
        BoardPos initialPos = this.getPos();
        int initialRow = initialPos.getRow();
        int initialCol = initialPos.getCol();

        for (BoardPos pos : this.getSpan()) {
            Piece piece = this.board.getPieceAt(pos);

            if (piece == null) {
                this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, pos.getRow(), pos.getCol()));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, pos.getRow(), pos.getCol(), pos));
                }
            }
        }

        return moves;
    }

    public Team getTeam() {
        return this.team;
    }

    public BoardPos getPos() {
        return this.pos;
    }

    public void updatePos(int row, int col) {
        this.pos.setRow(row);
        this.pos.setCol(col);
    }

    public abstract List<BoardPos> getSpan();

    protected List<BoardPos> getSpanFromOffsets(Offset[] offsets) {
        List<BoardPos> moves = new ArrayList<>();
        int initialRow = this.getPos().getRow();
        int initialCol = this.getPos().getCol();

        for (Offset offset : offsets) { //all spaces in offsets. used for king and knight currently
            int r = initialRow + offset.x;
            int c = initialCol + offset.y;

            if (this.board.inBounds(r, c)) {
                Piece piece = this.board.getPieceAt(r, c);

                if (piece == null) {
                    moves.add(new BoardPos(r, c));
                } else {
                    if (!piece.getTeam().equals(this.getTeam())) {
                        moves.add(new BoardPos(r, c));
                    }
                }
            }
        }

        return moves;
    }

    protected List<BoardPos> getDiagonalSpan() {
        List<BoardPos> moves = new ArrayList<>();
        BoardPos initialPos = this.getPos();

        int r = initialPos.getRow() + 1;
        int c = initialPos.getCol() + 1;

        while (r < 8 && c < 8) { // up and to right
            Piece piece = this.board.getPieceAt(r, c);

            if (piece == null) {
                moves.add(new BoardPos(r, c));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(r, c));
                }

                break;
            }

            r++;
            c++;
        }

        r = initialPos.getRow() - 1;
        c = initialPos.getCol() - 1;

        while (r >= 0 && c >= 0) { // down and to left
            Piece piece = this.board.getPieceAt(r, c);

            if (piece == null) {
                moves.add(new BoardPos(r, c));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(r, c));
                }

                break;
            }

            r--;
            c--;
        }

        r = initialPos.getRow() + 1;
        c = initialPos.getCol() - 1;

        while (r < 8 && c >= 0) { // up and to left
            Piece piece = this.board.getPieceAt(r, c);

            if (piece == null) {
                moves.add(new BoardPos(r, c));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(r, c));
                }

                break;
            }

            r++;
            c--;
        }

        r = initialPos.getRow() - 1;
        c = initialPos.getCol() + 1;

        while (r >= 0 && c < 8) { // down and to right
            Piece piece = this.board.getPieceAt(r, c);

            if (piece == null) {
                moves.add(new BoardPos(r, c));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(r, c));
                }

                break;
            }

            r--;
            c++;
        }

        return moves;
    }

    protected List<BoardPos> getHorizontalSpan() {
        List<BoardPos> moves = new ArrayList<>();
        BoardPos initialPos = this.getPos();
        int initialCol = initialPos.getCol();

        for (int r = initialPos.getRow() + 1; r < 8; r++) { //right hori
            Piece piece = this.board.getPieceAt(r, initialCol);

            if (piece == null) {
                moves.add(new BoardPos(r, initialCol));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(r, initialCol));
                }

                break;
            }
        }

        for (int r = initialPos.getRow() - 1; r >= 0; r--) { //left hori
            Piece piece = this.board.getPieceAt(r, initialCol);

            if (piece == null) {
                moves.add(new BoardPos(r, initialCol));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(r, initialCol));
                }

                break;
            }
        }

        return moves;
    }

    protected List<BoardPos> getVerticleSpan() {
        List<BoardPos> moves = new ArrayList<>();
        BoardPos initialPos = this.getPos();
        int initialRow = initialPos.getRow();

        for (int c = initialPos.getCol() + 1; c < 8; c++) { //up vert
            Piece piece = this.board.getPieceAt(initialRow, c);

            if (piece == null) {
                moves.add(new BoardPos(initialRow, c));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(initialRow, c));
                }

                break;
            }
        }

        for (int c = initialPos.getCol() - 1; c >= 0; c--) { //down vert
            Piece piece = this.board.getPieceAt(initialRow, c);

            if (piece == null) {
                moves.add(new BoardPos(initialRow, c));
            } else {
                if (!piece.getTeam().equals(this.getTeam())) {
                    moves.add(new BoardPos(initialRow, c));
                }

                break;
            }
        }

        return moves;
    }

}
