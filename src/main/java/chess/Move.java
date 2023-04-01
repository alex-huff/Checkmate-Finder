package chess;

public
class Move
{

    public final BoardPos from;
    public final BoardPos to;
    public final BoardPos take;
    public       Move     joint; // for castling
    public       int      promotion = 0;

    public
    Move(int fromRow, int fromCol, int toRow, int toCol, BoardPos take)
    {
        this.from = new BoardPos(fromRow, fromCol);
        this.to   = new BoardPos(toRow, toCol);
        this.take = take;
    }

    public
    Move(BoardPos start, BoardPos finish, BoardPos take)
    {
        this(start.getRow(), start.getCol(), finish.getRow(), finish.getCol(), take);
    }

    public
    Move(BoardPos start, BoardPos finish)
    {
        this(start.getRow(), start.getCol(), finish.getRow(), finish.getCol(), null);
    }

    public
    Move(int fromRow, int fromCol, int toRow, int toCol)
    {
        this(fromRow, fromCol, toRow, toCol, null);
    }

    public
    String singleStr()
    {
        return "(" + from.toString() + "-" + to.toString() + ")";
    }

    @Override
    public
    String toString()
    {
        String str = "(" + from.toString() + "-" + to.toString() + ")";

        if (this.joint != null)
        {
            str += "&&";
            // str += this.joint.toString();
            // if this and this.joint refer to each other as their joint move this
            // will reach max recursion depth
            str += this.joint.singleStr();
        }
        else if (this.promotion != 0)
        {
            str += "&&";
            str += "promote" + this.promotion;
        }

        return str;
    }

    @Override
    public
    boolean equals(Object other)
    {
        if (!(other instanceof Move otherMove)) return false;
        if (!this.from.equals(otherMove.from)) return false;
        if (!this.to.equals(otherMove.to)) return false;
        if (this.take == null && otherMove.take != null) return false;
        if (this.take != null && !this.take.equals(otherMove.take)) return false;
        if (this.joint == null && otherMove.joint != null) return false;
        if (this.joint != null && !this.joint.equals(otherMove.joint)) return false;
        return true;
    }

}
