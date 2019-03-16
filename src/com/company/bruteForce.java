package com.company;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;
import java.awt.geom.*;
import static java.lang.Math.sqrt;

public class bruteForce {
    ArrayList<Integer> basicIndices = new ArrayList<Integer>();
    ArrayList<Point2D> cities;
    double[][] dist;
    int size;
    double bestDist;

    int[] bestTour;

    XYSeries bestSeries = new XYSeries("Best Distances");
    XYSeries plotSeries = new XYSeries("Other Distances");
    int plotIteration = 0;
    long timeCount;

    //Constructor
    public bruteForce(ArrayList<Point2D> points, long time) {
        cities = points;
        size = points.size();
        dist = new double[size][size];
        bestTour = new int[size];
        timeCount = time;

        calcMatrix();
        explore(0);
    }

    public int[] getBest() {
        return bestTour;
    }

    public double getBestDist() {
        return bestDist;
    }

    private void calcMatrix() {
        for (int i = 0; i < size-1; i++) {
            for ( int j = i+1; j < size-1; j++) {
                dist[i][j] = distance(cities.get(i), cities.get(j));
                dist[j][i] = dist[i][j];
            }
            dist[i][i] = 0.0;
        }
    }

    private double distance(Point2D a, Point2D b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();

        return (sqrt((x*x)+(y*y)));
    }

    private double calcDist (ArrayList<Integer> index) {
        int a = index.get(0);
        int b = index.get(size-1);
        double totSum  = dist[a][b];

        for (int i = 0; i < size-1; i++) {
            a = index.get(i);
            b = index.get(i+1);
            totSum  = totSum + dist[a][b];
        }
        return totSum;
    }

    public XYDataset getDataSet() {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        dataSet.addSeries(plotSeries);
        dataSet.addSeries(bestSeries);
        return dataSet;
    }

    //To find out whether a point is already under consideration
    private boolean isSafe(int i){
        return (!basicIndices.contains(i));
    }

    //The recursive function that sets the arrangement of points until all the points are chosen
    private void explore(int col){
        //Base condition: When all the points are set to a particular arrangement, the totalDistance is calculated
        if (col >= size){
            double D = calcDist(basicIndices);
            if (bestDist == 0) {
                bestDist = D;
                int i = 0;
                for (int index: basicIndices) {
                    bestTour[i] = index;
                    i++;
                }
                System.out.println(" first initial: " + bestDist);
            } else if ( D < bestDist ) {
                int i = 0;
                for (int index: basicIndices) {
                    bestTour[i] = index;
                    i++;
                }
                bestDist = D;
                System.out.println(bestDist);
                // redraw(cities,bestIndices);
            }
            plotIteration++;
            if (plotIteration%20 == 0) {
                bestSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, bestDist);
                plotSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, D);
            }
        } else {
            for (int i=0 ; i < size; i++){
                if(isSafe(i)){
                    basicIndices.add(i); //Set the point
                    explore(col+1); //Recursion to set the points after the one set in the line above
                    //At this point, using recursion, a complete arrangement is tested for best distance.
                    //The next lines removes the set point, so that the other arrangements could be tested.
                    basicIndices.remove(basicIndices.size() - 1);
                }
            }
        }
    }

}