import java.util.ArrayList;
import java.util.List;

public class Test {

    static boolean areSegmentsIntersecting(Point p1, Point p2, Point p3, Point p4) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        // 检查分母是否为0，表示线段平行或重叠
        if (denominator == 0) {
            // 判断是否重叠
            double minXp = Math.min(p1.getX(), p2.getX());
            double maxXp = Math.max(p1.getX(), p2.getX());
            double minYp = Math.min(p1.getY(), p2.getY());
            double maxYp = Math.max(p1.getY(), p2.getY());
            double minXq = Math.min(p3.getX(), p4.getX());
            double maxXq = Math.max(p3.getX(), p4.getX());
            double minYq = Math.min(p3.getY(), p4.getY());
            double maxYq = Math.max(p3.getY(), p4.getY());
            if (maxXp < minXq || minXp > maxXq || maxYp < minYq || minYp > maxYq) {
                // 如果两条线段的边界没有相交，则为不重叠
                return false;
            } else if (isPointOnLineSegment(p1, p2, p3)) {
                return true;
            } else if (isPointOnLineSegment(p1, p2, p4)) {
                return true;
            }
        }

        double intersectX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denominator;
        double intersectY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denominator;

        // 检查交点是否在两条线段上
        if (intersectX >= Math.min(x1, x2) && intersectX <= Math.max(x1, x2) &&
                intersectX >= Math.min(x3, x4) && intersectX <= Math.max(x3, x4) &&
                intersectY >= Math.min(y1, y2) && intersectY <= Math.max(y1, y2) &&
                intersectY >= Math.min(y3, y4) && intersectY <= Math.max(y3, y4)) {
            // 检查交点是否是所选线和障碍物边缘的点
            if ((intersectX != x1 || intersectY != y1) && (intersectX != x2 || intersectY != y2) &&
                    (intersectX != x3 || intersectY != y3) && (intersectX != x4 || intersectY != y4)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 判断障碍物两个端点是否在线段上
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    static boolean isPointOnLineSegment(Point p1, Point p2, Point p3) {
        // 计算向量
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double px = p3.getX();
        double py = p3.getY();

        double crossProduct = (px - x1) * (y2 - y1) - (py - y1) * (x2 - x1);

        if (Math.abs(crossProduct) > 0.000001) {
            // 如果点不在直线上
            return false;
        }

        double dotProduct = (px - x1) * (x2 - x1) + (py - y1) * (y2 - y1);

        if (dotProduct < 0 || dotProduct > (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) {
            // 如果点在线段的延长线上或不在线段的两个端点之间
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        Point point1 = new Point(30, 40);
        Point point2 = new Point(170, 40);
        Point point3 = new Point(120, 40);
        Point point4 = new Point(170, 40);

        if(areSegmentsIntersecting(point1, point2, point3, point4)){
            System.out.println(111);
        }
        System.out.println(222);
    }
}
