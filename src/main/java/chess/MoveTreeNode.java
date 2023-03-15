package chess;

import java.util.List;

public
class MoveTreeNode
{

    private final boolean escaped;
    private final List<String> moves;
    private final List<Boolean> isForcedCheckmateTree;
    private final List<MoveTreeNode> nextNodes;

    public MoveTreeNode(boolean escaped, List<String> moves, List<Boolean> isForcedCheckmateTree,
                        List<MoveTreeNode> nextNodes)
    {
        this.escaped = escaped;
        this.moves = moves;
        this.isForcedCheckmateTree = isForcedCheckmateTree;
        this.nextNodes = nextNodes;
    }

}
