package chess;

import java.util.List;

public
record MoveTreeNode(boolean escaped, List<String> moves, List<Boolean> isForcedCheckmateTree,
                    List<MoveTreeNode> nextNodes)
{

}
