import java.util.ArrayList;
import java.util.List;

public class Tools {
    public static void printLinkLines(List<LinkLine> linkLines) {
        for (LinkLine linkLine : linkLines) {
//            System.out.println("Link Line: " + "("+linkLine.getStartPoint().getX()+","+ linkLine.getStartPoint().getY()+")" + " -> " + "("+linkLine.getEndPoint().getX()+","+ linkLine.getEndPoint().getY()+")");
            System.out.println(linkLine.getStartPoint().getX() + " " + linkLine.getStartPoint().getY() + "\n" + linkLine.getEndPoint().getX() + " " + linkLine.getEndPoint().getY());
        }
    }

    /**
     * 要求该集合的顶点组成一个凸多边形并且包围所有的障碍物
     *
     * @param obstacles 障碍物合集
     * @return 障碍物的顶点集合
     */
    public static List<Point> mergeConvexPolygons(List<Obstacle> obstacles) {
        List<Point> mergedPoints = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            mergedPoints.addAll(obstacle.getVertices());
        }
        //凸包算法来计算给定点集的凸多边形
        List<Point> convexHull = computeConvexHull(mergedPoints);
        return convexHull;
    }

    public static List<Point> computeConvexHull(List<Point> points) {
        int n = points.size();
        if (n < 3) {
            return new ArrayList<>(points);
        }

        // 找到最左边的点
        int leftmost = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).getX() < points.get(leftmost).getX()) {
                leftmost = i;
            }
        }

        List<Point> hull = new ArrayList<>();
        int p = leftmost;
        int q;
        do {
            hull.add(points.get(p));
            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                if (isCounterClockwise(points.get(p), points.get(i), points.get(q))) {
                    q = i;
                }
            }
            p = q;
        } while (p != leftmost);

        return hull;
    }

    public static boolean isCounterClockwise(Point p0, Point p1, Point p2) {
        double crossProduct = crossProduct(p0, p1, p2);
        return crossProduct > 0;
    }

    public static double crossProduct(Point p0, Point p1, Point p2) {
        double x1 = p1.getX() - p0.getX();
        double y1 = p1.getY() - p0.getY();
        double x2 = p2.getX() - p0.getX();
        double y2 = p2.getY() - p0.getY();
        return x1 * y2 - x2 * y1;
    }

    /**
     * 计算已知顶点到地图边界的水平和垂直线与边界相交的四个点，并返回其中距离最短的点
     *
     * @param vertex           地图中已知顶点
     * @param boundaryVertices 围成地图的顶点集合
     * @return 到已知顶点垂直距离最短的点
     */
    public static Point getNearestBoundaryPoint(Point vertex, List<Point> boundaryVertices) {
        if (boundaryVertices.isEmpty()) {
            return null;
        }

        Point nearestBoundaryPoint = null;
        double minDistance = Double.MAX_VALUE;

        // 计算水平线和垂直线与边界相交的四个点
        Point horizontalIntersectionTop = new Point(vertex.getX(), 200);
        Point horizontalIntersectionBottom = new Point(vertex.getX(), 0);
        Point verticalIntersectionLeft = new Point(0, vertex.getY());
        Point verticalIntersectionRight = new Point(200, vertex.getY());

        // 计算四个相交点到已知顶点的距离，并找到最小距离的点
        for (Point boundaryVertex : boundaryVertices) {
            double distance = Math.sqrt(Math.pow(boundaryVertex.getX() - vertex.getX(), 2) +
                    Math.pow(boundaryVertex.getY() - vertex.getY(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestBoundaryPoint = boundaryVertex;
            }
        }

        // 比较水平线和垂直线与边界相交的点的距离，并更新最小距离的点
        double horizontalTopDistance = Math.sqrt(Math.pow(horizontalIntersectionTop.getX() - vertex.getX(), 2) +
                Math.pow(horizontalIntersectionTop.getY() - vertex.getY(), 2));
        double horizontalBottomDistance = Math.sqrt(Math.pow(horizontalIntersectionBottom.getX() - vertex.getX(), 2) +
                Math.pow(horizontalIntersectionBottom.getY() - vertex.getY(), 2));
        double verticalLeftDistance = Math.sqrt(Math.pow(verticalIntersectionLeft.getX() - vertex.getX(), 2) +
                Math.pow(verticalIntersectionLeft.getY() - vertex.getY(), 2));
        double verticalRightDistance = Math.sqrt(Math.pow(verticalIntersectionRight.getX() - vertex.getX(), 2) +
                Math.pow(verticalIntersectionRight.getY() - vertex.getY(), 2));

        if (horizontalTopDistance < minDistance) {
            minDistance = horizontalTopDistance;
            nearestBoundaryPoint = horizontalIntersectionTop;
        }

        if (horizontalBottomDistance < minDistance) {
            minDistance = horizontalBottomDistance;
            nearestBoundaryPoint = horizontalIntersectionBottom;
        }

        if (verticalLeftDistance < minDistance) {
            minDistance = verticalLeftDistance;
            nearestBoundaryPoint = verticalIntersectionLeft;
        }

        if (verticalRightDistance < minDistance) {
            nearestBoundaryPoint = verticalIntersectionRight;
        }

        return nearestBoundaryPoint;
    }
    /**
     * 判断点p3是否在线段(p1,p2)的两个端点之间
     *
     * @param p1 MakLink线段起点
     * @param p2 MakLink线段终点
     * @param p3 障碍物端点
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

        if ((px == x1 && py == y1) || (px == x2 && py == y2)) {
            // 如果障碍物端点在MakLink线段的端点处
            return false;
        }

        double dotProduct = (px - x1) * (x2 - x1) + (py - y1) * (y2 - y1);

        if (dotProduct < 0 || dotProduct > (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) {
            // 如果点在线段的延长线上或不在线段的两个端点之间
            return false;
        }

        return true;
    }

    /**
     * 检查线段(p1, p2)和线段(p3, p4)是否相交
     *
     * @param p1 链接线端点
     * @param p2 链接线端点
     * @param p3 障碍物端点
     * @param p4 障碍物端点
     * @return
     */
    static boolean areSegmentsIntersecting(Point p1, Point p2, Point p3, Point p4) {
        //获取MakLink线端点坐标值
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        //获取障碍物边缘端点坐标值
        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        // 检查分母是否为0
        if (denominator == 0) {//表示线段平行或重叠即MakLink线和障碍物边缘斜率相等
            if (isPointOnLineSegment(p1, p2, p3) || isPointOnLineSegment(p1, p2, p4)) {
                //如果障碍物边缘线段的一个端点在MakLink线段的两个端点之间
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
}
