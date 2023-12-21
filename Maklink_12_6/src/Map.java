import java.util.ArrayList;
import java.util.List;

public class Map {

    // 定义地图边界的顶点
    private Point bottomLeft;

    private Point bottomRight;

    private Point topLeft;

    private Point topRight;


    public Map(Point bottomLeft, Point topLeft, Point topRight, Point bottomRight) {
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getTopRight() {
        return topRight;
    }
}
