package chess;

import java.util.List;

public
class MoveTreeNode
{

    public final boolean            escaped;
    public       List<Move>         moves;
    public       List<String>       moveStrings;
    public final List<Boolean>      isForcedCheckmateTree;
    public final List<MoveTreeNode> nextNodes;

    public
    MoveTreeNode(boolean escaped, List<Move> moves, List<String> moveStrings, List<Boolean> isForcedCheckmateTree,
                 List<MoveTreeNode> nextNodes)
    {
        this.escaped               = escaped;
        this.moves                 = moves;
        this.moveStrings           = moveStrings;
        this.isForcedCheckmateTree = isForcedCheckmateTree;
        this.nextNodes             = nextNodes;
    }

    public
    MoveTreeNode(boolean escaped, List<Move> moves, List<Boolean> isForcedCheckmateTree, List<MoveTreeNode> nextNodes)
    {
        this(escaped, moves, null, isForcedCheckmateTree, nextNodes);
    }

}
