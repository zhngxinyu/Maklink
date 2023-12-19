public class LinkLine {
    private Point startPoint;
    private Point endPoint;

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

    public double getLength() {
        double xDiff = endPoint.getX() - startPoint.getX();
        double yDiff = endPoint.getY() - startPoint.getY();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public boolean intersects(LinkLine otherLine) {
        return false;
    }
}