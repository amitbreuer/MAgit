package body.commitNode;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.Layout;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;
import java.util.Map;

public class CommitTreeLayout implements Layout {
    private Map<String, Integer> sha1ToX;
    private Map<String, Integer> sha1ToY;
    private Integer xCoordinateToMark;

    public CommitTreeLayout(Map<String, Integer> sha1ToX, Map<String, Integer> sha1ToY,Integer xCoordinateToMark) {
        this.sha1ToX = sha1ToX;
        this.sha1ToY = sha1ToY;
        this.xCoordinateToMark = xCoordinateToMark;
    }

    @Override
    public void execute(Graph graph) {
        final List<ICell> cells = graph.getModel().getAllCells();
        int XCoordinate;
        int YCoordinate;

        for (ICell cell : cells) {
            CommitNode c = (CommitNode) cell;
            XCoordinate = sha1ToX.get(c.getSha1());
            YCoordinate = sha1ToY.get(c.getSha1());
            if (xCoordinateToMark != null && XCoordinate == xCoordinateToMark) {
                c.getCommitNodeController().getCommitCircle().setFill(Color.YELLOW);
            } else {
                c.getCommitNodeController().getCommitCircle().setFill(Color.PURPLE);
            }
            graph.getGraphic(c).relocate(XCoordinate, YCoordinate);
        }
    }
}
