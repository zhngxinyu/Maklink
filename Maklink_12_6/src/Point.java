import java.util.ArrayList;
import java.util.List;

class Point {
    private List<LinkLine> linkLinesFromPoint = new ArrayList<>();
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void addLinkLine(LinkLine line) {
        linkLinesFromPoint.add(line);
    }

    public void removeLinkLine(LinkLine line) {
        linkLinesFromPoint.remove(line);
    }

    public List<LinkLine> getLinkLinesFromPoint() {
        return linkLinesFromPoint;
    }
}
