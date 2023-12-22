/**
 * 连接起始点和MakLink中点的路径线段
 */
public class PathLine {
    private Point startPoint;
    private Point endPoint;

    public PathLine(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }
}
