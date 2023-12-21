import java.util.*;

public class Tools {

    /**
     * 将Link线合集中经过障碍物的删除
     *
     * @param obstacles 障碍物集合
     * @param linkLines MakLink线集合
     * @return
     */
    public static List<LinkLine> removeIntersectLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
        Iterator<LinkLine> iterator = linkLines.iterator();

        //遍历linkLines列表中的每条链接线linkLine
        while (iterator.hasNext()) {
            LinkLine linkLine = iterator.next();
            //获取链接线的起点p1和终点q1。
            Point p1 = linkLine.getStartPoint();
            Point q1 = linkLine.getEndPoint();
            //设置一个布尔标志flag，用于指示链接线是否经过障碍物。
            boolean flag = false;//默认不经过障碍物

            //遍历obstacles列表中的每个障碍物obstacle
            for (Obstacle obstacle : obstacles) {
                //获取障碍物的两两顶点组成的顶点集合vertexPairs。
                List<List<Point>> vertexPairs = obstacle.getVertexPairs();

                for (List<Point> list : vertexPairs) {
                    //两个顶点p3和q3，表示障碍物的一条边缘线段。
                    Point p3 = list.get(0);
                    Point q3 = list.get(1);

                    //检查链接线(p1, q1)和障碍物边缘(p3, q3)是否相交，
                    if (areSegmentsIntersecting(p1, q1, p3, q3)) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    //如果标志flag为true，则表示链接线与障碍物边缘相交，直接退出循环。
                    break;
                }
            }

            if (flag) {
                //如果标志flag为true，则从linkLines列表中移除当前的链接线linkLine。
                iterator.remove();
            }
        }
        return linkLines;
    }

    public static void printLinkLines(List<LinkLine> linkLines) {
        for (LinkLine linkLine : linkLines) {
//            System.out.println("Link Line: " + "("+linkLine.getStartPoint().getX()+","+ linkLine.getStartPoint().getY()+")" + " -> " + "("+linkLine.getEndPoint().getX()+","+ linkLine.getEndPoint().getY()+")");
            System.out.println(linkLine.getStartPoint().getX() + " " + linkLine.getStartPoint().getY() + "\n" + linkLine.getEndPoint().getX() + " " + linkLine.getEndPoint().getY());
        }
    }

    /**
     * 获取最大凸多边形障碍物顶点集合
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
        //获取点p1和点p2的坐标值
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        //获取点p3的坐标值
        double px = p3.getX();
        double py = p3.getY();


        double crossProduct = (px - x1) * (y2 - y1) - (py - y1) * (x2 - x1);

        if (Math.abs(crossProduct) != 0) {
            // 如果叉积的绝对值不等于0，则表明点p3不在直线上，
            return false;
        }

        if ((px == x1 && py == y1) || (px == x2 && py == y2)) {
            // 如果障碍物端点在MakLink线段的端点处
            return false;
        }

        double dotProduct = (px - x1) * (x2 - x1) + (py - y1) * (y2 - y1);

        if (dotProduct < 0 || dotProduct > (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) {
            //如果点积小于0或大于线段长度的平方，则表明点p3在线段的延长线上
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

        //如果分母不为0，计算交点的坐标intersectX和intersectY
        double intersectX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denominator;
        double intersectY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denominator;
        Point intersect = new Point(intersectX, intersectY);

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
     * 获取障碍物顶点围成的边缘线段集合
     *
     * @param mergeConvexPolygons
     * @return
     */
    public static List<EdgeLine> generateEdgeLines(List<Point> mergeConvexPolygons) {
        List<EdgeLine> edgeLines = new ArrayList<>();

        int numVertices = mergeConvexPolygons.size();
        for (int i = 0; i < numVertices; i++) {
            Point currentPoint = mergeConvexPolygons.get(i);
            Point nextPoint = mergeConvexPolygons.get((i + 1) % numVertices);

            EdgeLine edgeLine = new EdgeLine(currentPoint, nextPoint);
            edgeLines.add(edgeLine);
        }

        return edgeLines;
    }

    /**
     * 检查由当前链接线连接产生的当前顶点的外角
     *
     * @param linkLine  链接线
     * @param edgeLine1 顶点所在的障碍物边缘
     * @param edgeLine2 顶点所在的障碍物边缘
     * @return 如果两个角度中的每一个都小于180度返回true
     */
    public static boolean areVertexAnglesLessThan180(LinkLine linkLine, EdgeLine edgeLine1, EdgeLine edgeLine2) {
        // 获取linkLine的向量表示
        double vectorX = linkLine.getEndPoint().getX() - linkLine.getStartPoint().getX();
        double vectorY = linkLine.getEndPoint().getY() - linkLine.getStartPoint().getY();
        // 获取edgeLine1的向量表示
        double vector1X = edgeLine1.getEndPoint().getX() - edgeLine1.getStartPoint().getX();
        double vector1Y = edgeLine1.getEndPoint().getY() - edgeLine1.getStartPoint().getY();

        // 获取edgeLine2的向量表示
        double vector2X = edgeLine2.getEndPoint().getX() - edgeLine2.getStartPoint().getX();
        double vector2Y = edgeLine2.getEndPoint().getY() - edgeLine2.getStartPoint().getY();

        // 计算linkLine和edgeLine1，edgeLine2之间的夹角（弧度）
        double angle = Math.atan2(vectorY, vectorX);
        double angle1 = Math.atan2(vector1Y, vector1X);
        double angle2 = Math.atan2(vector2Y, vector2X);

        // 将角度转换为正值
        angle = angle >= 0 ? angle : (2 * Math.PI + angle);
        angle1 = angle1 >= 0 ? angle1 : (2 * Math.PI + angle1);
        angle2 = angle2 >= 0 ? angle2 : (2 * Math.PI + angle2);

        // 计算两个外角的夹角（弧度）
        double angleDifference1 = Math.abs(angle - angle1);
        double angleDifference2 = Math.abs(angle - angle2);

        // 判断夹角是否小于180度
        return angleDifference1 < Math.PI && angleDifference2 < Math.PI;
    }

}