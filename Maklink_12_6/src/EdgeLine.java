/**
 * 顶点对应的两条障碍物边缘线段
 */
public class EdgeLine {
    private Point startPoint;
    private Point endPoint;

    public EdgeLine(Point startPoint, Point endPoint) {
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
