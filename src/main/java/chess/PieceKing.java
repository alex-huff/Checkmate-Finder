package chess;

import util.Offset;

import java.util.ArrayList;
import java.util.List;

public class PieceKing extends Piece {

    private static Offset[] offsets = { //1 space in each direction
        new Offset(-1, 1),
        new Offset(0, 1),
        new Offset(1, 1),
        new Offset(-1, 0),
        new Offset(1, 0),
        new Offset(-1, -1),
        new Offset(0, -1),
        new Offset(1, -1),
    };

    public PieceKing(Board board, Team team) {
        super(board, team);
    }

    @Override
    public char getLabel() {
        if (this.getTeam().equals(Team.WHITE)) {
            return 'K';
        } else {
            return 'k';
        }
    }

    @Override
    public List<Move> getSpecialMoves() { //castling
        BoardPos initialPos = this.getPos();
        int initialRow = initialPos.getRow();
        int initialCol = initialPos.getCol();

        if (this.board.canQueenSideCastle(this.getTeam()) || this.board.canKingSideCastle(this.getTeam())) { //can't castle if not first move for king
            if (!this.board.tileInAttack(initialRow, 4, this.getTeam())) { //can't start in check
                List<Move> moves = new ArrayList<>();

                if (this.board.canQueenSideCastle(this.getTeam())) { //left is rook and has not moved
                    if (
                        this.board.getPieceAt(initialRow, 1) == null
                            && this.board.getPieceAt(initialRow, 2) == null
                            && this.board.getPieceAt(initialRow, 3) == null
                    ) {
                        //if nothing in between king and rook
                        //king needs to not be in check, not be in check at 3, and not be in check at 2
                        if (!this.board.tileInAttack(initialRow, 3, this.getTeam())) {
                            //tileInAttack is adequate because both king and rook, the only pieces that are moving, cannot be blocking a piece by nature
                            Move move = new Move(initialRow, initialCol, initialRow, 2);
                            move.joint = new Move(initialRow, 0, initialRow, 3);

                            this.board.addMoveIfNoCheck(moves, move);
                        }
                    }
                }

                if (this.board.canKingSideCastle(this.getTeam())) { //right is rook and has not moved
                    if (
                        this.board.getPieceAt(initialRow, 6) == null &&
                            this.board.getPieceAt(initialRow, 5) == null
                    ) {
                        //if nothing in between king and rook
                        //king needs to not be in check, not be in check at 5, and not be in check at 6
                        if (!this.board.tileInAttack(initialRow, 5, this.getTeam())) {
                            //tileInAttack is adequate because both king and rook, the only pieces that are moving, cannot be blocking a piece by nature
                            Move move = new Move(initialRow, initialCol, initialRow, 6);
                            move.joint = new Move(initialRow, 7, initialRow, 5);

                            this.board.addMoveIfNoCheck(moves, move);
                        }
                    }
                }

                return moves;
            }
        }

        return null;
    }

    @Override
    public List<BoardPos> getSpan() {
        return this.getSpanFromOffsets(offsets);
    }

}
