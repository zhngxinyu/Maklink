import java.util.*;

public class MaklinkLinkGenerator {
    private static List<LinkLine> linkLines = new ArrayList<>();        //所有连接线合集

    private static Tools tools;     //引入工具类

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
        List<Point> mergeConvexPolygons = tools.mergeConvexPolygons(obstacles);

        for (Point vertex : mergeConvexPolygons) {
            // 获取距离已知顶点最近的地图边界点
            Point nearestBoundaryPoint = tools.getNearestBoundaryPoint(vertex, boundaryVertices);
            // 生成与地图上边界的连线
            LinkLine topLinkLine = new LinkLine(vertex, nearestBoundaryPoint);
            linkLines.add(topLinkLine);
        }
    }


    /**
     * 将Link线合集中经过障碍物的删除
     *
     * @param obstacles 障碍物集合
     * @param linkLines MakLink线集合
     * @return
     */
    public static void removeIntersectLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines) {
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
                    if (tools.areSegmentsIntersecting(p1, q1, p3, q3)) {
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
        List<Point> mergeConvexPolygons = tools.mergeConvexPolygons(obstacles);//获取最大凸多边形障碍物顶点集合
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
            Collections.sort(lines, (line1, line2) -> Double.compare(line1.getLength(), line2.getLength()));
        }

        // 按长度从小到大对每个点的终点链接线集合进行排序
        for (List<LinkLine> lines : endPointLinkLines) {
            Collections.sort(lines, (line1, line2) -> Double.compare(line1.getLength(), line2.getLength()));
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

    public static void main(String[] args) {
        List<Obstacle> obstacles = new ArrayList<>();   //障碍物合集
        // 多边形障碍物的顶点列表-->菱形1
        List<Point> obstaclePoints1 = new ArrayList<>();
        obstaclePoints1.add(new Point(40, 140));
        obstaclePoints1.add(new Point(60, 160));
        obstaclePoints1.add(new Point(100, 140));
        obstaclePoints1.add(new Point(60, 120));
//        obstaclePoints1.add(new Point(40, 40));
//        obstaclePoints1.add(new Point(60, 60));
//        obstaclePoints1.add(new Point(60, 60));
//        obstaclePoints1.add(new Point(60, 40));
        Obstacle obstacleOne = createObstacle(obstaclePoints1);
        obstacles.add(obstacleOne);     //加入合集

        // 多边形障碍物的顶点列表-->菱形2
        List<Point> obstaclePoints2 = new ArrayList<>();
        obstaclePoints2.add(new Point(50, 30));
        obstaclePoints2.add(new Point(30, 40));
        obstaclePoints2.add(new Point(80, 80));
        obstaclePoints2.add(new Point(100, 40));
//        obstaclePoints2.add(new Point(100, 30));
//        obstaclePoints2.add(new Point(80, 50));
//        obstaclePoints2.add(new Point(140, 80));
//        obstaclePoints2.add(new Point(160, 60));
        Obstacle obstacleTwo = createObstacle(obstaclePoints2);
        obstacles.add(obstacleTwo);     //加入合集

        // 多边形障碍物的顶点列表-->菱形3
        List<Point> obstaclePoints3 = new ArrayList<>();
        obstaclePoints3.add(new Point(120, 160));
        obstaclePoints3.add(new Point(140, 100));
        obstaclePoints3.add(new Point(180, 170));
        obstaclePoints3.add(new Point(165, 180));
//        obstaclePoints3.add(new Point(50, 80));
//        obstaclePoints3.add(new Point(40, 100));
//        obstaclePoints3.add(new Point(50, 120));
//        obstaclePoints3.add(new Point(60, 100));
        Obstacle obstacleThree = createObstacle(obstaclePoints3);
        obstacles.add(obstacleThree);     //加入合集

        // 多边形障碍物的顶点列表-->三角形
        List<Point> obstaclePoints4 = new ArrayList<>();
        obstaclePoints4.add(new Point(120, 40));
        obstaclePoints4.add(new Point(170, 40));
        obstaclePoints4.add(new Point(140, 80));
//        obstaclePoints4.add(new Point(110, 100));
//        obstaclePoints4.add(new Point(130, 140));
//        obstaclePoints4.add(new Point(160, 100));
        Obstacle obstacleFour = createObstacle(obstaclePoints4);
        obstacles.add(obstacleFour);     //加入合集

        generateLinkLines(obstacles, linkLines);     // 生成各障碍物顶点之间的link线
        generateMapLinkLines(obstacles, linkLines);   // 生成最大凸多边形障碍物顶点与地图边界之间的最短垂直连线
        removeIntersectLinkLines(obstacles, linkLines); // 将Link线合集中经过障碍物的删除
//        List<LinkLine> finalLinkLines = generateFinalLinkLines(obstacles, linkLines1);// 生成最终的MakLink线
        tools.printLinkLines(linkLines);
    }


}

