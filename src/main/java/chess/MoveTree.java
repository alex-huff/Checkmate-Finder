package chess;

public
class MoveTree
{

    public MoveTreeNode root;
    public boolean isWhitesTurn;

    public MoveTree(MoveTreeNode root, boolean isWhitesTurn)
    {
        this.root = root;
        this.isWhitesTurn = isWhitesTurn;
    }

}
