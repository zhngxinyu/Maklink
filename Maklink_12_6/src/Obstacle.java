import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private List<Point> vertices; // 障碍物的顶点坐标列表

    public Obstacle(List<Point> vertices) {
        this.vertices = vertices;
    }

    // 获取障碍物的顶点坐标列表
    public List<Point> getVertices() {
        return vertices;
    }


    //获取障碍物边缘线段两两组成的顶点列表
    public List<List<Point>> getPolygonEdges() {
        List<List<Point>> edges = new ArrayList<>();

        // 遍历多边形的顶点
        for (int i = 0; i < vertices.size(); i++) {
            Point currentPoint = vertices.get(i);
            Point nextPoint = vertices.get((i + 1) % vertices.size());

            List<Point> edge = new ArrayList<>();
            edge.add(currentPoint);
            edge.add(nextPoint);
            edges.add(edge);
        }

        return edges;
    }

    //获取一个障碍物两两顶点组成的顶点集合
    public List<List<Point>> getVertexPairs() {
        List<List<Point>> vertexPairs = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            Point vertex1 = vertices.get(i);

            for (int j = i + 1; j < vertices.size(); j++) {
                Point vertex2 = vertices.get(j);

                List<Point> pair = new ArrayList<>();
                pair.add(vertex1);
                pair.add(vertex2);
                vertexPairs.add(pair);
            }
        }

        return vertexPairs;
    }

}