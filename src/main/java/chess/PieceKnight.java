package chess;

import util.Offset;

import java.util.List;

public
class PieceKnight extends Piece
{

    private static Offset[] offsets = {
        new Offset(-1, 2), new Offset(1, 2), new Offset(-2, 1), new Offset(2, 1), new Offset(-2, -1), new Offset(2, -1),
        new Offset(-1, -2), new Offset(1, -2),
        };

    public
    PieceKnight(Board board, Team team)
    {
        super(board, team);
    }

    public
    PieceKnight(Piece piece)
    { //for promotions
        super(piece);
    }

    @Override
    public
    char getLabel()
    {
        if (this.getTeam().equals(Team.WHITE))
        {
            return 'N';
        }
        else
        {
            return 'n';
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
        return this.getSpanFromOffsets(offsets);
    }

}
