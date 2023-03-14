package chess;

import java.util.List;

public
class PieceQueen extends Piece
{

    public
    PieceQueen(Board board, Team team)
    {
        super(board, team);
    }

    public
    PieceQueen(Piece piece)
    { //for promotions
        super(piece);
    }

    @Override
    public
    char getLabel()
    {
        if (this.getTeam().equals(Team.WHITE))
        {
            return 'Q';
        }
        else
        {
            return 'q';
        }
    }

    @Override
    public
    List<Move> getSpecialMoves()
    {
        return null;
    }

    @Override
    public
    List<BoardPos> getSpan()
    {
        List<BoardPos> span = this.getHorizontalSpan();

        span.addAll(this.getVerticleSpan());
        span.addAll(this.getDiagonalSpan());

        return span;
    }

}
