package chess;

import java.util.List;

public
class PieceRook extends Piece
{

    public
    PieceRook(Board board, Team team)
    {
        super(board, team);
    }

    public
    PieceRook(Piece piece)
    { //for promotions
        super(piece);
    }

    @Override
    public
    char getLabel()
    {
        if (this.getTeam().equals(Team.WHITE))
        {
            return 'R';
        }
        else
        {
            return 'r';
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

        return span;
    }

}
