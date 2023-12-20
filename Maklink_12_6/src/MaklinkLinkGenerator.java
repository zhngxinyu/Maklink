import java.util.*;

public class MaklinkLinkGenerator {
    private static List<LinkLine> linkLines = new ArrayList<>();        //所有连接线合集

    public static Obstacle createObstacle(List<Point> vertices) {
        return new Obstacle(vertices);
    }

    /**
     * 生成各障碍物顶点之间的MakLink线
     *
     * @param obstacles 障碍物合集
     * @param linkLines 所有MakLink线合集
     */
    public static void generateLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
        Set<LinkLine> uniqueLinkLines = new HashSet<>();

        for (int i = 0; i < obstacles.size() - 1; i++) {
            Obstacle obstacle1 = obstacles.get(i);
            List<Point> vertices1 = obstacle1.getVertices();

            for (int j = i + 1; j < obstacles.size(); j++) {
                Obstacle obstacle2 = obstacles.get(j);
                List<Point> vertices2 = obstacle2.getVertices();

                for (Point vertex1 : vertices1) {
                    for (Point vertex2 : vertices2) {
                        LinkLine linkLine = new LinkLine(vertex1, vertex2);
                        uniqueLinkLines.add(linkLine);
                    }
                }
            }
        }

        linkLines.addAll(uniqueLinkLines);
    }

    /**
     * 生成最大凸多边形障碍物顶点与地图边界之间的最短垂直连线
     *
     * @param obstacles 障碍物合集
     * @param linkLines 所有MakLink线合集
     */
    public static void generateMapLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
        // 定义地图边界的顶点
        Point mapVertex1 = new Point(0, 0);
        Point mapVertex2 = new Point(200, 0);
        Point mapVertex3 = new Point(200, 200);
        Point mapVertex4 = new Point(0, 200);
        //创建地图边界顶点集合
        List<Point> boundaryVertices = new ArrayList<>();
        boundaryVertices.add(mapVertex1);
        boundaryVertices.add(mapVertex2);
        boundaryVertices.add(mapVertex3);
        boundaryVertices.add(mapVertex4);

        //获取最大凸多边形障碍物顶点集合
        List<Point> mergeConvexPolygons = mergeConvexPolygons(obstacles);

        for (Point vertex : mergeConvexPolygons) {
            // 获取距离已知顶点最近的地图边界点
            Point nearestBoundaryPoint = getNearestBoundaryPoint(vertex, boundaryVertices);
            // 生成与地图上边界的连线
            LinkLine topLinkLine = new LinkLine(vertex, nearestBoundaryPoint);
            linkLines.add(topLinkLine);
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
     * 生成不经过障碍物的Link线
     *
     * @param obstacles 障碍物集合
     * @param linkLines 未去除经过障碍物的MakLink线集合
     * @return
     */
    public static List<LinkLine> removeIntersectLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
        List<LinkLine> disjointLinkLines = new ArrayList<>();

        //遍历链接线
        for (LinkLine linkLine : linkLines) {
            Point p1 = linkLine.getStartPoint();//链接线起点
            Point q1 = linkLine.getEndPoint();//链接线终点
            boolean flag = true;//默认不经过障碍物
            //遍历障碍物检查链接线是否经过障碍物边缘
            for (Obstacle obstacle : obstacles) {
                //获取一个障碍物两两顶点组成的顶点集合
                List<List<Point>> vertexPairs = obstacle.getVertexPairs();
                //遍历障碍物两两顶点组成的顶点集合
                for (List<Point> list : vertexPairs) {
                    Point p2 = list.get(0);
                    Point q2 = list.get(1);
                    // 检查端点p2, q2是否在链接线(p1, q1)端点之间
                    if (isPointOnLineSegment(p1, q1, p2) || isPointOnLineSegment(p1, q1, q2)) {
                        flag = false;
                        break;
                    }
                }
                if (!flag) {//端点p2, q2在链接线(p1, q1)端点之间直接退出循环
                    break;
                }

                //获取障碍物边缘线段组成的顶点列表
                List<List<Point>> polygonEdges = obstacle.getPolygonEdges();
                //遍历障碍物边缘
                for (List<Point> list : polygonEdges) {
                    Point p3 = list.get(0);
                    Point q3 = list.get(1);
                    // 检查链接线(p1, q1)和障碍物边缘(p3, q3)是否相交
                    if (areSegmentsIntersecting(p1, q1, p3, q3)) {
                        flag = false;
                        break;
                    }
                }
                if (!flag) { //相交直接退出检查
                    break;
                }
            }

            if (flag) {
                //符合，加入disjointLinkLines集合
                disjointLinkLines.add(linkLine);
            }
        }

        return disjointLinkLines;
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
     * 生成最终的MakLink线
     *
     * @param obstacles  障碍物合集
     * @param linkLines1 不经过障碍物的Link线
     * @return
     */
    public static List<LinkLine> generateFinalLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines1) {

        //获取除最大凸多边形障碍物顶点以外的其它顶点集合
        List<Point> mergeConvexPolygons = mergeConvexPolygons(obstacles);//获取最大凸多边形障碍物顶点集合
        List<Point> otherVertices = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            List<Point> vertices = obstacle.getVertices();
            for (Point vertex : vertices) {
                if (!mergeConvexPolygons.contains(vertex)) {
                    otherVertices.add(vertex);
                }
            }
        }

        /**
         * 求除最大凸多边形障碍物顶点以外的其它顶点集合
         * List<Point> otherVertices 里每个点作为起点或者终点的链接线合集
         * 并将每个点对应的链接线分别按照长度从小到大排序
         */
        // 创建每个点作为起点的链接线集合
        List<List<LinkLine>> startPointLinkLines = new ArrayList<>();
        // 创建每个点作为终点的链接线集合
        List<List<LinkLine>> endPointLinkLines = new ArrayList<>();

        for (Point point : otherVertices) {
            List<LinkLine> startLines = new ArrayList<>();
            List<LinkLine> endLines = new ArrayList<>();
            for (int i = 0; i < linkLines1.size(); i++) {
                if (linkLines1.get(i).getStartPoint().getX() == point.getX() && linkLines1.get(i).getStartPoint().getY() == point.getY()) {
                    startLines.add(linkLines1.get(i));
                } else if (linkLines1.get(i).getEndPoint().getX() == point.getX() && linkLines1.get(i).getEndPoint().getY() == point.getY()) {
                    LinkLine linkLine = new LinkLine(linkLines1.get(i).getStartPoint(), linkLines1.get(i).getEndPoint());
                    endLines.add(linkLine);
                }
            }
            startPointLinkLines.add(startLines);
            endPointLinkLines.add(endLines);
        }

        // 按长度从小到大对每个点的起点链接线集合进行排序
        for (List<LinkLine> lines : startPointLinkLines) {
            Collections.sort(lines, (line1, line2) -> Double.compare(getLinkLineLength(line1), getLinkLineLength(line2)));
        }

        // 按长度从小到大对每个点的终点链接线集合进行排序
        for (List<LinkLine> lines : endPointLinkLines) {
            Collections.sort(lines, (line1, line2) -> Double.compare(getLinkLineLength(line1), getLinkLineLength(line2)));
        }

        List<List<List<LinkLine>>> pointLinkLines = new ArrayList<>();
        List<List<LinkLine>> pointLines = new ArrayList<>();
        pointLines.addAll(startPointLinkLines);
        pointLines.addAll(endPointLinkLines);
        pointLinkLines.add(pointLines);

        /**
         * 求除最大凸多边形障碍物顶点以外的其它顶点集合
         * List<Point> otherVertices 里每个点所在的障碍物的两条经过该点的两条边
         */

        // 输出每个点对应的链接线
        for (
                int i = 0; i < otherVertices.size(); i++) {
            Point point = otherVertices.get(i);
            List<LinkLine> startLines = startPointLinkLines.get(i);
            List<LinkLine> endLines = endPointLinkLines.get(i);

            System.out.println("Point: (" + point.getX() + ", " + point.getY() + ")");
            System.out.println("Start Lines:");
            for (LinkLine line : startLines) {
                System.out.println("(" + line.getStartPoint().getX() + ", " + line.getStartPoint().getY() + ") -> ("
                        + line.getEndPoint().getX() + ", " + line.getEndPoint().getY() + ")");
            }
            System.out.println("End Lines:");
            for (LinkLine line : endLines) {
                System.out.println("(" + line.getStartPoint().getX() + ", " + line.getStartPoint().getY() + ") -> ("
                        + line.getEndPoint().getX() + ", " + line.getEndPoint().getY() + ")");
            }
            System.out.println();
        }


        return null;

    }

    /**
     * 计算链接线的长度
     *
     * @param line 链接线
     * @return
     */
    private static double getLinkLineLength(LinkLine line) {
        double dx = line.getEndPoint().getX() - line.getStartPoint().getX();
        double dy = line.getEndPoint().getY() - line.getStartPoint().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }


    public static void printLinkLines(List<LinkLine> linkLines) {
        for (LinkLine linkLine : linkLines) {
//            System.out.println("Link Line: " + "("+linkLine.getStartPoint().getX()+","+ linkLine.getStartPoint().getY()+")" + " -> " + "("+linkLine.getEndPoint().getX()+","+ linkLine.getEndPoint().getY()+")");
            System.out.println(linkLine.getStartPoint().getX() + " " + linkLine.getStartPoint().getY() + "\n" + linkLine.getEndPoint().getX() + " " + linkLine.getEndPoint().getY());
        }
    }

    public static void main(String[] args) {
        List<Obstacle> obstacles = new ArrayList<>();   //障碍物合集

        // 多边形障碍物的顶点列表-->菱形1
        List<Point> obstaclePoints1 = new ArrayList<>();
        obstaclePoints1.add(new Point(40, 140));
        obstaclePoints1.add(new Point(60, 160));
        obstaclePoints1.add(new Point(100, 140));
        obstaclePoints1.add(new Point(60, 120));
        Obstacle obstacleOne = createObstacle(obstaclePoints1);
        obstacles.add(obstacleOne);     //加入合集

        // 多边形障碍物的顶点列表-->菱形2
        List<Point> obstaclePoints2 = new ArrayList<>();
        obstaclePoints2.add(new Point(50, 30));
        obstaclePoints2.add(new Point(30, 40));
        obstaclePoints2.add(new Point(80, 80));
        obstaclePoints2.add(new Point(100, 40));
        Obstacle obstacleTwo = createObstacle(obstaclePoints2);
        obstacles.add(obstacleTwo);     //加入合集

//        // 多边形障碍物的顶点列表-->菱形3
//        List<Point> obstaclePoints3 = new ArrayList<>();
//        obstaclePoints3.add(new Point(120, 160));
//        obstaclePoints3.add(new Point(140, 100));
//        obstaclePoints3.add(new Point(180, 170));
//        obstaclePoints3.add(new Point(165, 180));
//        Obstacle obstacleThree = createObstacle(obstaclePoints3);
//        obstacles.add(obstacleThree);     //加入合集
//
//        // 多边形障碍物的顶点列表-->三角形
//        List<Point> obstaclePoints4 = new ArrayList<>();
//        obstaclePoints4.add(new Point(120, 40));
//        obstaclePoints4.add(new Point(170, 40));
//        obstaclePoints4.add(new Point(140, 80));
//        Obstacle obstacleFour = createObstacle(obstaclePoints4);
//        obstacles.add(obstacleFour);     //加入合集

        generateLinkLines(obstacles, linkLines);     // 生成各障碍物顶点之间的link线
        generateMapLinkLines(obstacles, linkLines);   // 生成最大凸多边形障碍物顶点与地图边界之间的最短垂直连线
        List<LinkLine> linkLines1 = removeIntersectLinkLines(obstacles, linkLines); // 生成不经过障碍物的Link线
        List<LinkLine> finalLinkLines = generateFinalLinkLines(obstacles, linkLines1);// 生成最终的MakLink线
//        printLinkLines(finalLinkLines);
    }


}

