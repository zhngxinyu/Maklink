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
        List<LinkLine> lines = new ArrayList<>();
        lines.addAll(uniqueLinkLines);
        List<LinkLine> removeIntersectLinkLines = tools.removeIntersectLinkLines(obstacles, lines);  //去除经过障碍物的链接线

//        linkLines.addAll(uniqueLinkLines);//初始链接线
        linkLines.addAll(removeIntersectLinkLines);//去除经过障碍物后的链接线
    }

    /**
     * 生成障碍物外围顶点与地图边界之间的最佳MakLink线
     *
     * @param obstacles 障碍物合集
     * @param linkLines 所有MakLink线合集
     * @param map       地图边界
     */
    public static void generateMapLinkLines(List<Obstacle> obstacles, List<LinkLine> linkLines, Map map) {

        //获取外围顶点集合
        List<Point> mergeConvexPolygons = tools.mergeConvexPolygons(obstacles);

        //每个顶点的链接线都根据它们的长度从最短到最长存储在集合lines中
        List<List<LinkLine>> lines = new ArrayList<>();
        // 生成每个顶点与地图边界之间的链接线
        for (Point vertex : mergeConvexPolygons) {
            List<LinkLine> vertexLines = new ArrayList<>();
            // 计算水平线和垂直线与边界相交的四个点
            Point horizontalIntersectionTop = new Point(vertex.getX(), map.getTopLeft().getY());//(x,200)
            Point horizontalIntersectionBottom = new Point(vertex.getX(), map.getBottomLeft().getY());//(x,0)
            Point verticalIntersectionLeft = new Point(map.getBottomLeft().getX(), vertex.getY());//(0,y)
            Point verticalIntersectionRight = new Point(map.getBottomRight().getX(), vertex.getY());//(200,y)

            // 生成与地图边界的连线
            LinkLine topLinkLine = new LinkLine(vertex, horizontalIntersectionTop);//和上边界的链接线
            LinkLine bottomLinkLine = new LinkLine(vertex, horizontalIntersectionBottom);//和下边界的链接线
            LinkLine leftLinkLine = new LinkLine(vertex, verticalIntersectionLeft);//和左边界的链接线
            LinkLine rightLinkLine = new LinkLine(vertex, verticalIntersectionRight);//和右边界的链接线
            vertexLines.add(topLinkLine);
            vertexLines.add(bottomLinkLine);
            vertexLines.add(leftLinkLine);
            vertexLines.add(rightLinkLine);
            List<LinkLine> removeIntersectLinkLines = tools.removeIntersectLinkLines(obstacles, vertexLines);//去除经过障碍物的链接线
            // 按照链接线长度从小到大排序
            Collections.sort(removeIntersectLinkLines, new Comparator<LinkLine>() {
                @Override
                public int compare(LinkLine line1, LinkLine line2) {
                    // 按照链接线长度进行比较
                    double length1 = line1.getLength();
                    double length2 = line2.getLength();
                    return Double.compare(length1, length2);
                }
            });

            lines.add(removeIntersectLinkLines);
        }

        //生成每个顶点所在的两条边
        List<EdgeLine> edgeLines = tools.generateEdgeLines(mergeConvexPolygons);

        //生成外围顶点与地图边界之间的最佳MakLink线
        List<LinkLine> finalResult = new ArrayList<>();
        for (int i = 0; i < mergeConvexPolygons.size(); i++) {
            int a;
            int b;
            if (i == 0) {
                a = mergeConvexPolygons.size() - 1;
                b = i;
            } else {
                a = i - 1;
                b = i;
            }
            EdgeLine edgeLine1 = edgeLines.get(a);//当前顶点所在的障碍物边1
            EdgeLine edgeLine2 = edgeLines.get(b);//当前顶点所在的障碍物边2
            List<LinkLine> result = new ArrayList<>();//重置候选MakLink线列表

            for (int j = 0; j < lines.get(i).size(); j++) {
                LinkLine linkLine = lines.get(i).get(j);//从集合lines中每个点所在的最短的链接线开始判断
                if (tools.areVertexAnglesLessThan180(linkLine, edgeLine1, edgeLine2)) {
                    //如果两个角度都小于或等于180度，则该线将被确定为最佳MakLink线，加入最终结果集
                    finalResult.add(linkLine);
                    //直接退出循环，忽略来自该顶点的其他线
                    break;
                } else {
                    //否则，将该线添加到属于当前顶点的候选MakLink线列表中。
                    result.add(linkLine);
                }

                if (result.size() == lines.get(i).size()) {
                    //如果当前顶点的所有链接线都不是最佳MakLink线，就把候选MakLink线加入最终结果集
                    finalResult.addAll(result);
                }
            }
        }

        linkLines.addAll(finalResult);
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

        //测试正方形
//        List<Point> obstaclePoints1 = new ArrayList<>();
//        obstaclePoints1.add(new Point(50, 50));
//        obstaclePoints1.add(new Point(50, 150));
//        obstaclePoints1.add(new Point(150, 150));
//        obstaclePoints1.add(new Point(150, 50));
//        Obstacle obstacleOne = createObstacle(obstaclePoints1);
//        obstacles.add(obstacleOne);     //加入合集


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


            //测试
//        // 多边形障碍物的顶点列表-->正方形1
//        List<Point> obstaclePoints1 = new ArrayList<>();
//        obstaclePoints1.add(new Point(40, 40));
//        obstaclePoints1.add(new Point(40, 60));
//        obstaclePoints1.add(new Point(60, 60));
//        obstaclePoints1.add(new Point(60, 40));
//        Obstacle obstacleOne = createObstacle(obstaclePoints1);
//        obstacles.add(obstacleOne);     //加入合集
//
//        //多边形障碍物的顶点列表-->菱形2
//        List<Point> obstaclePoints2 = new ArrayList<>();
//        obstaclePoints2.add(new Point(100, 30));
//        obstaclePoints2.add(new Point(80, 50));
//        obstaclePoints2.add(new Point(140, 80));
//        obstaclePoints2.add(new Point(160, 60));
//        Obstacle obstacleTwo = createObstacle(obstaclePoints2);
//        obstacles.add(obstacleTwo);     //加入合集
//
//        // 多边形障碍物的顶点列表-->菱形3
//        List<Point> obstaclePoints3 = new ArrayList<>();
//        obstaclePoints3.add(new Point(50, 80));
//        obstaclePoints3.add(new Point(40, 100));
//        obstaclePoints3.add(new Point(50, 120));
//        obstaclePoints3.add(new Point(60, 100));
//        Obstacle obstacleThree = createObstacle(obstaclePoints3);
//        obstacles.add(obstacleThree);     //加入合集
//
//        // 多边形障碍物的顶点列表-->三角形
//        List<Point> obstaclePoints4 = new ArrayList<>();
//        obstaclePoints4.add(new Point(110, 100));
//        obstaclePoints4.add(new Point(130, 140));
//        obstaclePoints4.add(new Point(160, 100));
//        Obstacle obstacleFour = createObstacle(obstaclePoints4);
//        obstacles.add(obstacleFour);     //加入合集


        //定义地图边界点
        Point mapVertex1 = new Point(0, 0);
        Point mapVertex2 = new Point(0, 200);
        Point mapVertex3 = new Point(200, 200);
        Point mapVertex4 = new Point(200, 0);
        Map map = new Map(mapVertex1, mapVertex2, mapVertex3, mapVertex4);

        generateLinkLines(obstacles, linkLines);     // 生成各障碍物顶点之间的link线
        generateMapLinkLines(obstacles, linkLines, map);   // 生成障碍物外围顶点与地图边界之间的最佳MakLink线
//        List<LinkLine> finalLinkLines = generateFinalLinkLines(obstacles, linkLines1);// 生成最终的MakLink线
//        tools.printLinkLines(linkLines);

        List<Point> midPoints = new ArrayList<>(); //所有MakLink线的中点集合
        Point start = new Point(20, 180);
        midPoints.add(start);
        for (LinkLine linkLine : linkLines) {
            Point midPoint = linkLine.getMidPoint();
            midPoints.add(midPoint);
        }
        Point end = new Point(160, 90);
        midPoints.add(end);
        //创建邻接矩阵
        int[][] matrix = tools.buildAdjacencyMatrix(midPoints, obstacles);
        // 输出邻接矩阵
        for (int i = 0; i < midPoints.size(); i++) {
            for (int j = 0; j < midPoints.size(); j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

    }


}

