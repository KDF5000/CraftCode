/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.util.*;
import java.util.zip.Inflater;

public final class Route
{
    private static final int INFINATE_VALUE = Integer.MAX_VALUE;    //无效值

    private static int startPoint;  // 起点
    private static int endPoint;    //终点
    private static int optRouteValue = INFINATE_VALUE;  //最优路径值

    private static Set<Integer> mustPassSet = new HashSet<Integer>();  //必经顶点集合

    private static Path optPath; // 最有路径

    /**
     * 你需要完成功能的入口
     *
     * @author KDF
     * @since 2016-3-19
     * @version V1
     * @param graphContent string
     * @param condition string
     */
    public static String searchRoute(String graphContent, String condition)
    {
        bulidSubSet(condition);  //提取
        int keyPointsNum = mustPassSet.size(); //关键点个数

        //构建邻接矩阵
        int vertexNum = findVertexNum(graphContent);
        List<int [][]> matrixList = buildAdjMatrix(graphContent, vertexNum);
        int[][] adjMatrix = matrixList.get(0);
        int[][] edgeMatrix = matrixList.get(1);


        optPath = new Path(startPoint, startPoint, adjMatrix, edgeMatrix, mustPassSet);
        optPath.setPathWeight(INFINATE_VALUE);

        Path s = new Path(startPoint, startPoint, adjMatrix,edgeMatrix, mustPassSet);
        PriorityQueue<Path> queue = new PriorityQueue<Path>();
        queue.add(s);

        while(queue.size()>0){
            Path node = queue.poll();
            int pathEnd = node.getEnd();

            //找到一条满足条件的路径
            if(pathEnd == endPoint && node.getKey() == 0){
                if(node.getPathWeight() < optPath.getPathWeight()){
                    optPath = node;
                    optPath.printPath();
                    break;
                }
                continue;
            }
//            System.out.println("pathEnd:" + pathEnd);
            for(int i=0;i<vertexNum;i++){
                //路径中已经包含此点
                if(i == pathEnd || node.getPointSet().contains(i)){
                    continue;
                }
                //有边
                if(adjMatrix[pathEnd][i] < INFINATE_VALUE && adjMatrix[pathEnd][i] > 0){
                    Path newPath = null;
                    try {
                        newPath = (Path) node.deepClone();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
//                    System.out.println(adjMatrix[pathEnd][i]+"addEdge:"+pathEnd+"->" + i);
                    newPath.addEdge(pathEnd, i);
//                    System.out.print("newPath:");
                    node.printPath();
                    queue.add(newPath);
//                    System.out.println(queue.size());
                }
            }

        }
        optPath.printPath();
        System.out.println(optPath.getPathWeight());
        return "hello world";
    }

    /**
     * 寻找图中顶点的个数
     * @param graphContent
     * @return
     */
    public static int findVertexNum(String graphContent) {
        int vertexNum = 0;
        String gcCopy = graphContent;
        String[] gcLines = gcCopy.split("\n");
        for(int i = 0; i < gcLines.length; i++) {
            String[] lineEles = gcLines[i].split(",");
            for(int j = 1; j < lineEles.length - 1; j++) {
                int v = Integer.parseInt(lineEles[j]);
                if(vertexNum < v) {
                    vertexNum = v;
                }
            }
        }
        return vertexNum + 1; //顶点编号是从0开始的
    }

    /**
     * 构建邻接矩阵以及顶点编号与边号对应矩阵
     * @param graphContent
     * @param vertexNum
     * @return
     */
    public static List<int[][]> buildAdjMatrix(String graphContent, int vertexNum) {
        List<int[][]> matrixList = new ArrayList<int[][]>();
        int[][] adjMatrix = new int[vertexNum][vertexNum];
        int[][] edgeMatrix = new int[vertexNum][vertexNum];
        for(int i = 0; i < vertexNum; i++) {
            for(int j = 0; j < vertexNum; j++) {
                if(i==j){
                    adjMatrix[i][j] = 0;
                }else{
                    adjMatrix[i][j] = INFINATE_VALUE;
                }
                edgeMatrix[i][j] = INFINATE_VALUE;
            }
        }

        String gcCopy = graphContent;
        String[] gcLines = gcCopy.split("\n");
        for(int i = 0; i < gcLines.length; i++) {
            String[] lineEles = gcLines[i].split(",");
            int id = Integer.parseInt(lineEles[0].trim());
            int u = Integer.parseInt(lineEles[1].trim());
            int v = Integer.parseInt(lineEles[2].trim());
            int w = Integer.parseInt(lineEles[3].trim());
            if(w < adjMatrix[u][v]) {
                adjMatrix[u][v] = w;  //如果u->v有多条边，取权重最小的边
                edgeMatrix[u][v] = id;
            }
        }
        matrixList.add(adjMatrix);
        matrixList.add(edgeMatrix);
        return matrixList;
    }

    static class Node{
        public int vertex; //节点编号
        public int weight; //权重
        public int id;  //临边编号

        public Node(int vertex, int weight, int id){
            this.vertex = vertex;
            this.weight = weight;
            this.id = id;
        }

    }

    /**
     * 构造邻接链表
     * @param graphContent
     * @return
     */
    public static HashMap<Integer, List<Node>> buildAdjList(String graphContent){
        HashMap<Integer, List<Node>> adjList = new HashMap<Integer,  List<Node>>();
        String gcCopy = graphContent;
        String[] gcLines = gcCopy.split("\n");
        for(int i = 0; i < gcLines.length; i++) {
            String[] lineEles = gcLines[i].split(",");
            int id = Integer.parseInt(lineEles[0].trim());
            int u = Integer.parseInt(lineEles[1].trim());
            int v = Integer.parseInt(lineEles[2].trim());
            int w = Integer.parseInt(lineEles[3].trim());
            List<Node> tmpList = adjList.get(u);
            if(tmpList!= null){
                tmpList.add(new Node(v, w, id));
            }
            adjList.put(u, tmpList);
        }

        return adjList;
    }

    /**
     * 提取起始点，终点以及毕经节点
     * @param condition
     */
    public static void bulidSubSet(String condition) {
        String[] cons = condition.split(",");
        startPoint = Integer.parseInt(cons[0]);
        endPoint = Integer.parseInt(cons[1]);
        String[] mpv = cons[2].split("\\|");

        for(int i = 0; i < mpv.length; i++) {
            mustPassSet.add(Integer.parseInt(mpv[i].trim()));
        }
    }

}