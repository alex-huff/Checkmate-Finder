package chess;

import java.util.ArrayList;
import java.util.List;

public
class PiecePawn extends Piece
{

    public
    PiecePawn(Board board, Team team)
    {
        super(board, team);
    }

    @Override
    public
    char getLabel()
    {
        if (this.getTeam().equals(Team.WHITE))
        {
            return 'P';
        }
        else
        {
            return 'p';
        }
    }

    @Override
    public
    List<Move> getSpecialMoves()
    {
        List<Move> moves = new ArrayList<>();

        if (this.board.getEnTar() == null)
        {
            return moves;
        }

        //en passant

        int initialRow = this.getPos().getRow();
        int initialCol = this.getPos().getCol();
        int diff       = initialCol - this.board.getEnTar().getCol();

        if (diff == 1 || diff == -1)
        {
            if (this.getTeam().equals(Team.WHITE) && initialRow == 4 && this.board.getEnTar().getRow() == 5)
            {
                this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, 5, this.board.getEnTar().getCol(),
                    new BoardPos(4, this.board.getEnTar().getCol())));
            }
            else if (this.getTeam().equals(Team.BLACK) && initialRow == 3 && this.board.getEnTar().getRow() == 2)
            {
                this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, 2, this.board.getEnTar().getCol(),
                    new BoardPos(3, this.board.getEnTar().getCol())));
            }
        }

        return moves;
    }

    @Override
    public
    List<Move> getLegalSpan()
    {
        List<Move> moves      = new ArrayList<>();
        int        initialRow = this.getPos().getRow();
        int        initialCol = this.getPos().getCol();

        for (BoardPos pos : this.getSpan())
        {
            Piece piece = this.board.getPieceAt(pos);

            if (piece != null && !piece.getTeam().equals(this.getTeam()))
            {
                if (pos.getRow() == 0 || pos.getRow() == 7)
                {
                    Move promQueen  = new Move(initialRow, initialCol, pos.getRow(), pos.getCol(), pos);
                    Move promRook   = new Move(initialRow, initialCol, pos.getRow(), pos.getCol(), pos);
                    Move promBishop = new Move(initialRow, initialCol, pos.getRow(), pos.getCol(), pos);
                    Move promKnight = new Move(initialRow, initialCol, pos.getRow(), pos.getCol(), pos);

                    promQueen.promotion  = 1;
                    promRook.promotion   = 2;
                    promBishop.promotion = 3;
                    promKnight.promotion = 4;

                    this.board.addMoveIfNoCheck(moves, promQueen);
                    this.board.addMoveIfNoCheck(moves, promRook);
                    this.board.addMoveIfNoCheck(moves, promBishop);
                    this.board.addMoveIfNoCheck(moves, promKnight);
                }
                else
                {
                    this.board.addMoveIfNoCheck(moves,
                        new Move(initialRow, initialCol, pos.getRow(), pos.getCol(), pos));
                }
            }
        }

        //straight moves. added here because they are not part of the span as they cannot capture pieces

        if (this.getTeam().equals(Team.WHITE))
        {
            int r = initialRow + 1;
            int c = initialCol;

            Piece piece;

            if (this.board.inBounds(r, c))
            {
                piece = this.board.getPieceAt(r, c);

                if (piece == null)
                {
                    if (r == 7)
                    {
                        Move promQueen  = new Move(initialRow, initialCol, r, c);
                        Move promRook   = new Move(initialRow, initialCol, r, c);
                        Move promBishop = new Move(initialRow, initialCol, r, c);
                        Move promKnight = new Move(initialRow, initialCol, r, c);

                        promQueen.promotion  = 1;
                        promRook.promotion   = 2;
                        promBishop.promotion = 3;
                        promKnight.promotion = 4;

                        this.board.addMoveIfNoCheck(moves, promQueen);
                        this.board.addMoveIfNoCheck(moves, promRook);
                        this.board.addMoveIfNoCheck(moves, promBishop);
                        this.board.addMoveIfNoCheck(moves, promKnight);
                    }
                    else
                    {
                        this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, r, c));
                    }
                }
                else
                {
                    return moves;
                }
            }

            r = initialRow + 2;
            c = initialCol;

            if (initialRow == 1)
            { //can only move two spaces forward on first move
                if (this.board.inBounds(r, c))
                {
                    piece = this.board.getPieceAt(r, c);

                    if (piece == null)
                    {
                        this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, r, c));
                    }
                }
            }
        }
        else if (this.getTeam().equals(Team.BLACK))
        {
            int r = initialRow - 1;
            int c = initialCol;

            Piece piece;

            if (this.board.inBounds(r, c))
            {
                piece = this.board.getPieceAt(r, c);

                if (piece == null)
                {
                    if (r == 0)
                    {
                        Move promQueen  = new Move(initialRow, initialCol, r, c);
                        Move promRook   = new Move(initialRow, initialCol, r, c);
                        Move promBishop = new Move(initialRow, initialCol, r, c);
                        Move promKnight = new Move(initialRow, initialCol, r, c);

                        promQueen.promotion  = 1;
                        promRook.promotion   = 2;
                        promBishop.promotion = 3;
                        promKnight.promotion = 4;

                        this.board.addMoveIfNoCheck(moves, promQueen);
                        this.board.addMoveIfNoCheck(moves, promRook);
                        this.board.addMoveIfNoCheck(moves, promBishop);
                        this.board.addMoveIfNoCheck(moves, promKnight);
                    }
                    else
                    {
                        this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, r, c));
                    }
                }
                else
                {
                    return moves;
                }
            }

            r = initialRow - 2;
            c = initialCol;

            if (initialRow == 6)
            { //can only move two spaces forward on first move
                if (this.board.inBounds(r, c))
                {
                    piece = this.board.getPieceAt(r, c);

                    if (piece == null)
                    {
                        this.board.addMoveIfNoCheck(moves, new Move(initialRow, initialCol, r, c));
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public
    List<BoardPos> getSpan()
    {
        List<BoardPos> moves      = new ArrayList<>();
        int            initialRow = this.getPos().getRow();
        int            initialCol = this.getPos().getCol();

        if (this.getTeam().equals(Team.WHITE))
        {
            int   r = initialRow + 1; //up and left
            int   c = initialCol - 1;
            Piece piece;

            if (this.board.inBounds(r, c))
            {
                piece = this.board.getPieceAt(r, c);

                if (piece == null || piece.getTeam().equals(Team.BLACK))
                {
                    moves.add(new BoardPos(r, c));
                }
            }

            r = initialRow + 1; //up and right
            c = initialCol + 1;

            if (this.board.inBounds(r, c))
            {
                piece = this.board.getPieceAt(r, c);

                if (piece == null || piece.getTeam().equals(Team.BLACK))
                {
                    moves.add(new BoardPos(r, c));
                }
            }
        }
        else if (this.getTeam().equals(Team.BLACK))
        {
            int   r = initialRow - 1; //down and left
            int   c = initialCol - 1;
            Piece piece;

            if (this.board.inBounds(r, c))
            {
                piece = this.board.getPieceAt(r, c);

                if (piece == null || piece.getTeam().equals(Team.WHITE))
                {
                    moves.add(new BoardPos(r, c));
                }
            }

            r = initialRow - 1; //down and right
            c = initialCol + 1;

            if (this.board.inBounds(r, c))
            {
                piece = this.board.getPieceAt(r, c);

                if (piece == null || piece.getTeam().equals(Team.WHITE))
                {
                    moves.add(new BoardPos(r, c));
                }
            }
        }

        return moves;
    }

}
