import java.util.*;
/**
 * MakLink线
 */
public class LinkLine {
    private Point startPoint;
    private Point endPoint;

    private List<Double> vList;
    public LinkLine(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    /**
     * @return 返回MakLink线长度(double)
     */
    public double getLength() {
        double xDiff = endPoint.getX() - startPoint.getX();
        double yDiff = endPoint.getY() - startPoint.getY();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /**
     * @return 返回MakLink线中点(Point)
     */
    public Point getMidPoint() {
        double midX = (startPoint.getX() + endPoint.getX()) / 2.0;
        double midY = (startPoint.getY() + endPoint.getY()) / 2.0;
        return new Point(midX, midY);
    }

}