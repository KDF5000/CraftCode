package com.routesearch.route;

import java.io.*;
import java.util.*;

/**
 * Created by dell on 2016/3/19.
 */
public class Path implements Comparable<Path>, Serializable{
    private int start; //起始点
    private int end;   //终点
    private int pathWeight; //路径权重
    private int keyPointsNum; //关键点个数
    private List<Integer> pathPoints = new LinkedList<Integer>(); // 途径的顶点
    private Set<Integer> pointSet = new HashSet<Integer>();  //途径的顶点集合
    private int[][] adjMatrix; //邻接矩阵
    private int[][] edgeMatrix; //边编号
    private Set<Integer> mustPassSet;//关键点

    private int key;  //当前路径的key, key = Count(keyPoint) - passedKeyPoint

    public Path(int start, int end, int[][]adjMatrix, int [][] edgeMatrix, Set<Integer> mustPassSet){
        this.start = start;
        this.end = end;
        this.pathWeight = 0;
        this.keyPointsNum = 0;
        this.adjMatrix = adjMatrix;
        this.edgeMatrix = edgeMatrix;
        this.mustPassSet = mustPassSet;
        this.addEdge(start, end);
    }

    /**
     * 添加一条边到已有路径上
     * @param s
     * @param t
     */

    public void addEdge(int s, int t){
        if(s != this.end){
            System.err.println("new edge must start with the end of the original path");
            return;
        }
        this.pathPoints.add(t);
        this.pointSet.add(t);
        this.pathWeight += this.adjMatrix[s][t];
        if(mustPassSet.contains(t)){
            this.keyPointsNum++;
        }
        this.key = this.mustPassSet.size() - this.keyPointsNum;
        this.end = t;
//        System.out.println("change End:"+this.end);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int compareTo(Path o) {
        if(this.key < o.key){
            return -1;
        }else if (this.key == o.key){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 打印路径
     */
    public void printPath(){
//       for(Iterator<Integer> iter =  this.pathPoints.iterator(); iter.hasNext();){
//           System.out.print(iter.next()+"|");
//       }
        int pointsNum = this.pathPoints.size();
        for(int i=0; i< pointsNum - 1; i++){
            System.out.print(this.edgeMatrix[this.pathPoints.get(i)][this.pathPoints.get(i+1)]);
            if(i < pointsNum -2){
                System.out.print("|");
            }
        }
        System.out.println();
    }

    public int getPathWeight() {
        return pathWeight;
    }

    public void setPathWeight(int pathWeight) {
        this.pathWeight = pathWeight;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Set<Integer> getPointSet() {
        return pointSet;
    }


    public Object deepClone() throws IOException, OptionalDataException,ClassNotFoundException{//将对象写到流里
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        ObjectOutputStream oo=new ObjectOutputStream(bo);
        oo.writeObject(this);//从流里读出来
        ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi=new ObjectInputStream(bi);
        return (oi.readObject());
    }

}
