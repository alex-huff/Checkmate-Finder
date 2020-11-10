package chess;

import java.util.List;

public class PieceBishop extends Piece {

    public PieceBishop(Board board, Team team) {
        super(board, team);
    }

    public PieceBishop(Piece piece) { //for promotions
        super(piece);
    }

    @Override
    public char getLabel() {
        if (this.getTeam().equals(Team.WHITE)) {
            return 'B';
        } else {
            return 'b';
        }
    }

    @Override
    public List<Move> getSpecialMoves() {
        return null;
    }

    @Override
    public List<BoardPos> getSpan() {
        return this.getDiagonalSpan();
    }
}
