package chess;

public
class BoardState
{

    public BoardPos enPas;
    public Piece    oldPiece;
    public Piece    taken;
    public boolean  canWhiteQueenCastle;
    public boolean  canWhiteKingCastle;
    public boolean  canBlackQueenCastle;
    public boolean  canBlackKingCastle;

    public
    BoardState(BoardPos enPas, Piece oldPiece, Piece taken, boolean canWhiteQueenCastle, boolean canWhiteKingCastle,
               boolean canBlackQueenCastle, boolean canBlackKingCastle)
    {
        this.enPas               = enPas;
        this.oldPiece            = oldPiece;
        this.taken               = taken;
        this.canWhiteQueenCastle = canWhiteQueenCastle;
        this.canWhiteKingCastle  = canWhiteKingCastle;
        this.canBlackQueenCastle = canBlackQueenCastle;
        this.canBlackKingCastle  = canBlackKingCastle;
    }

}
