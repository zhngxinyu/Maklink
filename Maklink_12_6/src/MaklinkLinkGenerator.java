import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaklinkLinkGenerator {
    private static List<LinkLine> linkLines = new ArrayList<>();        //所有连接线合集

    public static Obstacle createObstacle(List<Point> vertices) {
        return new Obstacle(vertices);
    }

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

    public static void generateMapLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
        // 定义地图边界的顶点
        Point mapVertex1 = new Point(0, 0);
        Point mapVertex2 = new Point(200, 0);
        Point mapVertex3 = new Point(200, 200);
        Point mapVertex4 = new Point(0, 200);

        for (Obstacle obstacle : obstacles) {
            List<Point> vertices = obstacle.getVertices();

            for (Point vertex : vertices) {
                // 生成与地图上边界的连线
                LinkLine topLinkLine = new LinkLine(vertex, new Point(vertex.getX(), mapVertex1.getY()));
                linkLines.add(topLinkLine);

                // 生成与地图下边界的连线
                LinkLine bottomLinkLine = new LinkLine(vertex, new Point(vertex.getX(), mapVertex3.getY()));
                linkLines.add(bottomLinkLine);

                // 生成与地图左边界的连线
                LinkLine leftLinkLine = new LinkLine(vertex, new Point(mapVertex4.getX(), vertex.getY()));
                linkLines.add(leftLinkLine);

                // 生成与地图右边界的连线
                LinkLine rightLinkLine = new LinkLine(vertex, new Point(mapVertex2.getX(), vertex.getY()));
                linkLines.add(rightLinkLine);
            }
        }
    }

    public static void printLinkLines(List<LinkLine> linkLines) {
        for (LinkLine linkLine : linkLines) {
//            System.out.println("Link Line: " + "("+linkLine.getStartPoint().getX()+","+ linkLine.getStartPoint().getY()+")" + " -> " + "("+linkLine.getEndPoint().getX()+","+ linkLine.getEndPoint().getY()+")");
            System.out.println(linkLine.getStartPoint().getX() + " " + linkLine.getStartPoint().getY() + "\n" + linkLine.getEndPoint().getX() + " " + linkLine.getEndPoint().getY());
        }
    }

    public static List<LinkLine> removeDuplicateLinkLines(List<LinkLine> linkLines) {
        Set<Pair<Point, Point>> pointPairs = new HashSet<>();
        List<LinkLine> uniqueLinkLines = new ArrayList<>();

        for (LinkLine linkLine : linkLines) {
            Pair<Point, Point> pointPair = new Pair<>(linkLine.getStartPoint(), linkLine.getEndPoint());

            if (!pointPairs.contains(pointPair)) {
                pointPairs.add(pointPair);
                uniqueLinkLines.add(linkLine);
            }
        }

        return uniqueLinkLines;
    }
    //1
    public static List<LinkLine> removeIntersectLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
        List<LinkLine> disjointLinkLines = new ArrayList<>();

        //遍历链接线
        for (LinkLine linkLine : linkLines) {
            Point p1 = linkLine.getStartPoint();
            Point q1 = linkLine.getEndPoint();
            boolean flag = true;
            //遍历障碍物检查链接线是否经过障碍物边缘
            for (Obstacle obstacle : obstacles) {

                //获取一个障碍物两两顶点组成的顶点集合
                List<List<Point>> vertexPairs = obstacle.getVertexPairs();
                //遍历障碍物两两顶点组成的顶点集合
                for (List<Point> list : vertexPairs) {
                    Point p2 = list.get(0);
                    Point q2 = list.get(1);
                    // 检查(p2, q2)是否在链接线(p1, q1)端点之间
                    if (isPointOnLineSegment(p1, q1, p2) || isPointOnLineSegment(p1, q1, q2)) {
                        flag = false;
                        break;
                    }
                }
                //相交直接退出检查
                if (!flag) {
                    break;
                }

                //获取障碍物边缘线段两两组成的顶点列表
                List<List<Point>> polygonEdges = obstacle.getPolygonEdges();
                //遍历障碍物边缘
                for (List<Point> list : polygonEdges) {
                    Point p2 = list.get(0);
                    Point q2 = list.get(1);
                    // 检查链接线(p1, q1)和障碍物边缘(p2, q2)是否相交
                    if (areSegmentsIntersecting(p1, q1, p2, q2)) {
                        flag = false;
                        break;
                    }
                }
                //相交直接退出检查
                if (!flag) {
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
     * @param p1 链接线端点
     * @param p2 链接线端点
     * @param p3 障碍物端点
     * @param p4 障碍物端点
     * @return
     */
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
//            // 判断是否重叠
//            double minXp = Math.min(x1, x2);
//            double maxXp = Math.max(x1, x2);
//            double minYp = Math.min(y1, y2);
//            double maxYp = Math.max(y1, y2);
//
//            double minXq = Math.min(x3, x4);
//            double maxXq = Math.max(x3, x4);
//            double minYq = Math.min(y3, y4);
//            double maxYq = Math.max(y3, y4);

//            if (maxXp < minXq || minXp > maxXq || maxYp < minYq || minYp > maxYq) {
//                // 如果两条线段的边界没有相交，则为不重叠
//                return false;
//            }

            if (isPointOnLineSegment(p1, p2, p3) || isPointOnLineSegment(p1, p2, p4)) {
                //如果障碍物端点在MakLink线段的两个端点之间
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
     * 判断障碍物端点是否在MakLink线段的两个端点之间
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

        // 多边形障碍物的顶点列表-->菱形3
        List<Point> obstaclePoints3 = new ArrayList<>();
        obstaclePoints3.add(new Point(120, 160));
        obstaclePoints3.add(new Point(140, 100));
        obstaclePoints3.add(new Point(180, 170));
        obstaclePoints3.add(new Point(165, 180));
        Obstacle obstacleThree = createObstacle(obstaclePoints3);
        obstacles.add(obstacleThree);     //加入合集

        // 多边形障碍物的顶点列表-->三角形
        List<Point> obstaclePoints4 = new ArrayList<>();
        obstaclePoints4.add(new Point(120, 40));
        obstaclePoints4.add(new Point(170, 40));
        obstaclePoints4.add(new Point(140, 80));
        Obstacle obstacleFour = createObstacle(obstaclePoints4);
        obstacles.add(obstacleFour);     //加入合集

        generateLinkLines(obstacles, linkLines);     // 生成各障碍物顶点之间的link线
//        generateMapLinkLines(obstacles, linkLines);   // 生成合障碍物顶点与地图边界之间的最短连线
//        removeDuplicateLinkLines(linkLines);
        List<LinkLine> linkLines1 = removeIntersectLinkLines(obstacles, linkLines); // 生成不经过障碍物的MakLink线
        printLinkLines(linkLines1);
    }
}

