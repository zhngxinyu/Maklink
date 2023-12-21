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
    public Point getMidPoint() {
        double midX = (startPoint.getX() + endPoint.getX()) / 2.0;
        double midY = (startPoint.getY() + endPoint.getY()) / 2.0;
        return new Point(midX, midY);
    }

}