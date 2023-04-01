package chess;

public
class BoardPos
{

    private              int    row;
    private              int    col;
    private static final String letters = "abcdefgh";

    public
    BoardPos(BoardPos pos)
    {
        this.row = pos.getRow();
        this.col = pos.getCol();
    }

    public
    BoardPos(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public
    int getRow()
    {
        return this.row;
    }

    public
    int getCol()
    {
        return this.col;
    }

    public
    void setRow(int row)
    {
        this.row = row;
    }

    public
    void setCol(int col)
    {
        this.col = col;
    }

    @Override
    public
    String toString()
    {
        return letters.charAt(this.col) + Integer.toString(this.row + 1);
    }

    public
    char getFile()
    {
        return letters.charAt(this.col);
    }

    public static
    BoardPos getPosFromStr(String str)
    {
        return new BoardPos(Integer.parseInt("" + str.charAt(1)) - 1, letters.indexOf(str.charAt(0)));
    }

    @Override
    public
    boolean equals(Object other)
    {
        if (!(other instanceof BoardPos otherBoardPos)) return false;
        return ((this.row == otherBoardPos.row) && (this.col == otherBoardPos.col));
    }

}
