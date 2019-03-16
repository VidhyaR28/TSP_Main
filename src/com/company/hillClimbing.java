package com.company;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;
import java.util.List;
import java.util.Random;
import java.awt.geom.*;
import static java.lang.Math.sqrt;

public class hillClimbing {
    ArrayList<Point2D> cities;
    //List (priorityQueue) is made for every point and stored in array indexed against its original input order
    PriorityQueue<node>[] base;
    int size;
    double bestDist;
    //List to contain the arrangements of the best path and the current path during the different iteration of start-points
    List<Integer> current;
    List<Integer> bestList;

    int[] bestTour;

    XYSeries bestSeries = new XYSeries("Best Distances");
    XYSeries plotSeries = new XYSeries("Other Distances");
    long timeCount;

    //Constructor
    public hillClimbing(ArrayList<Point2D> points, long time){
        cities = points;
        size = points.size();
        base = makeArrayOf(size);
        bestDist = 0;
        current = new ArrayList<>();
        bestList = new ArrayList<>();
        timeCount = time;
        //The priorityQueue of each point is computed in this method
        calcMatrix();
        //This method builds the path from the computed lists of priorityQueues
        hillClimbing();
    }

    //Method to make an array of priorityQueues
    private PriorityQueue<node>[] makeArrayOf(int arraySize) {
        return (PriorityQueue<node>[]) (new PriorityQueue[arraySize]);
    }

    //Method to calculate distance between every point to every other points
    //and using the distance to create a priorityQueue list for every point - Graph creation
    private void calcMatrix(){
        for (int i = 0; i < size; i++) {
            //A new comparator is created because the elements of the priorityQueue to be compared are objects of node class
            PriorityQueue<node> temp = new PriorityQueue<>(size-1, new nodeComparator());
            Point2D currentPoint = cities.get(i);
            for ( int j = 0; j < size; j++) {
                if (i == j) {
                    continue;
                } else {
                    node addition = new node(cities.get(j), j);
                    addition.nodeDist(currentPoint);
                    temp.add(addition);
                }
            }
            base[i] = temp;
        }
    }

    //Recursive function to retrieve to "next best" for each node
    private double calcDist(int index, List<Integer> visited) {
        //Base condition when all the nodes are arranged, the distance between the last to the first node is calculated
        if (visited.size() >= size) {
            current = visited.subList(0,size);
            return calcPointDist(cities.get(index), cities.get(visited.get(0)));
        } else {
            //For every other node, an iterator is set on the node's priorityQueue to retrieve the first possible node
            //which has not yet been added to the arrangement list - visited
            Iterator<node> itr = base[index].iterator();
            node next = itr.next();
            while (visited.contains(next.index)) {
                next = itr.next();
            }
            visited.add(next.index);
            double Dist = next.dist;
            Dist = Dist + calcDist(next.index, visited);
            return Dist;
        }
    }

    private double calcPointDist(Point2D p1, Point2D p2) {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        return sqrt((x*x)+(y*y));
    }

    // Randomly selected the start point of the arrangement and looks for the best arrangement
    private void hillClimbing() {
        int i = 0;
        List<Integer> indicesDone = new ArrayList<>();
        Random rand = new Random();
        int index = rand.nextInt(size);

        while (i <= size*0.8) {
            i++;
            while (indicesDone.contains(index)) {
                index = rand.nextInt(size);
            }
            indicesDone.add(index);
            List<Integer> toBegin = new ArrayList<>();
            toBegin.add(index);
            double dist = calcDist(index, toBegin);
            // bestList = current; // new test addition

            if (i == 1) {
                bestDist = dist;
                bestList = current.subList(0,size);
            } else if (dist < bestDist) {
                bestDist = dist;
                bestList = current.subList(0,size);
            }

            bestSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, bestDist);
            plotSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, dist);
        }
    }

    public int[] getBest() {
        bestTour = new int[bestList.size()];
        int i = 0;
        for(int index: bestList) {
            bestTour[i] = index;
            i++;
        }
        return bestTour;
    }

    public double getBestDist() {
        return bestDist;
    }

    public XYDataset getDataSet() {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        dataSet.addSeries(plotSeries);
        dataSet.addSeries(bestSeries);
        return dataSet;
    }
}


//Class to compare two objects of the node class
class nodeComparator implements Comparator<node> {
    @Override
    public int compare(node n1, node n2) {
        if (n1.dist < n2.dist) {
            return -1;
        } else if (n1.dist > n2.dist){
            return 1;
        }
        return 0;
    }
}
//Class which contains the information about the point and its relationship to its next best neighbour point
class node {
    private Point2D point;
    int index;
    double dist;

    //Constructor: takes a point and sets it to this node
    node(Point2D point, int index) {
        this.point = point;
        this.index = index;
    }

    public void nodeDist(Point2D newPoint) {
        double x = point.getX() - newPoint.getX();
        double y = point.getY() - newPoint.getY();
        dist = sqrt((x*x)+(y*y));
    }
}