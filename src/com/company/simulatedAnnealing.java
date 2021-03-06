package com.company;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;
import java.util.Random;
import java.awt.geom.*;

import static java.lang.Math.sqrt;

public class simulatedAnnealing {
    ArrayList<Integer> basicIndices = new ArrayList<Integer>();
    ArrayList<Integer> alteredIndices = new ArrayList<Integer>();
    Queue<Integer> tabu = new LinkedList<>();
    ArrayList<Point2D> cities;
    double[][] dist;
    int size;

    Random rand1 = new Random();

    int[] bestTour;
    double bestDist;
    ArrayList<Integer> bestIndices = new ArrayList<Integer>();
    private static final double coolingRate = 0.9995;
    private double t;
    private long itr;

    XYSeries bestSeries = new XYSeries("Best Distances");
    XYSeries plotSeries = new XYSeries("Other Distances");
    int plotIteration = 0;
    long timeCount;

    //Constructor
    public simulatedAnnealing(ArrayList<Point2D> points, long time){
        cities = points;
        size = points.size();
        dist = new double[size][size];
        itr = size*4000;
        t = 5000;
        timeCount = time;
        for( int i = 0; i < size; i++){
            basicIndices.add(i);
        }
        //Calculates all the distances and stores it in a matrix
        calcMatrix();
        Collections.shuffle(basicIndices);
        bestDist = calcDist(basicIndices);
        bestIndices = (ArrayList<Integer>) basicIndices.clone();
        alteredIndices = (ArrayList<Integer>) basicIndices.clone();
        annealing();
    }

    //Calculates all the distances and stores it in a matrix
    private void calcMatrix(){
        for (int i = 0; i < size-1; i++) {
            for ( int j = i+1; j < size-1; j++) {
                dist[i][j] = distance(cities.get(i), cities.get(j));
                dist[j][i] = dist[i][j];
            }
        }
    }

    private double distance(Point2D a, Point2D b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();

        return (sqrt((x*x)+(y*y)));
    }

    // For an arrangement of cities, the distance is calculated from searching in the matrix
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

    private void swap() {
        Random rand = new Random();
        rand.setSeed(rand.nextInt(100));
        int a = 0;
        int b = 0;
        //Two cities are chosen at random and if they are the same or have been chosen in the last few iterations,
        //the process is repeated until these conditions are negated.
        while (a == b) {
            a = rand.nextInt(size);
            b = rand.nextInt(size);
            if (tabu.contains(a) || tabu.contains(b)) {
                a = b;
            }
        }

        //tabu keeps a track of the last few chosen cities so that it doesn't kep repeating
        tabu.add(a);
        tabu.add(b);
        //after a certain number of iterations, the last ones in the queue are let go
        if(tabu.size() > (cities.size()*0.5)) {
            while(tabu.size()>(cities.size()*0.5)) {
                tabu.remove();
            }
        }
        basicIndices = (ArrayList<Integer>) alteredIndices.clone();

        //the swap is saved in the alteredIndices, so that it could be reverted back when necessary
        int x = alteredIndices.get(a);
        int y = alteredIndices.get(b);
        alteredIndices.set(a, y);
        alteredIndices.set(b, x);

        // Take D from the annealing as parameter in swap and just pass it to swapDist;
        // Alter swap to take and return double D;
        //swapDist(D,a,b) - Method to use the distance previously calc and just swap out the points and their dist
    }

    private double swapDist (double D, int a, int b) {
        int aPrev = basicIndices.get(a-1);
        int aCurrent = basicIndices.get(a);
        int aNext = basicIndices.get(a+1);
        int bPrev = basicIndices.get(b-1);
        int bCurrent = basicIndices.get(b);
        int bNext = basicIndices.get(b+1);

        D = D - dist[aCurrent][aPrev] - dist[aCurrent][aNext] - dist[bCurrent][bPrev] - dist[bCurrent][bNext];
        D = D + dist[bCurrent][aPrev] + dist[bCurrent][aNext] + dist[aCurrent][bPrev] + dist[aCurrent][bNext];
        return D;
    }

    //reverting the swap - just copying over basicIndices into alteredIndices
    private void revertSwap() {
        alteredIndices = (ArrayList<Integer>) basicIndices.clone();
    }


    private void annealing(){
        //the number of revertswap called is tracked
        int revertCount = 0;
        int count = 1;
        double temp;
        while (count <= (size/2)) {
            Collections.shuffle(alteredIndices);

            //initial temp
            temp = t / Math.exp(count);

            for (int i = 0; i < itr; i++) {
                //the stopping condition based on temperature - a premature halt
                if (temp > 1 * Math.exp(-(count))) {
                    //a swap is initiated
                    swap();
                    double D = calcDist(alteredIndices);
                    //if the swap's distance is better than the current, the arrangement is retained,
                    //else based on a probability, if the distance does not fall within a randomly selected value,
                    //the arrangement is reverted
                    if (D <= bestDist) {
                        System.out.println("BEST");
                        bestDist = D;
                        revertCount = 0;
                        bestIndices = (ArrayList<Integer>) alteredIndices.clone();
                    } else if (Math.exp((bestDist - D) / temp) < Math.random()) {
                        revertCount++;
                        revertSwap();
                        //if too many reverts happen, the arrangement is set to the best and then the swaps are run on them
                        if (revertCount > size * 5) {
                            alteredIndices = (ArrayList<Integer>) bestIndices.clone();
                        }
                    }
                    //At regular intervals, then tour length and time are recorded for plotting the chart
                    plotIteration++;
                    if (plotIteration%2 == 0) {
                        bestSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, bestDist);
                        plotSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, D);
                    }
                    //temperature reduction
                    temp *= (coolingRate);
                } else {
                    break;
                }
            }
            System.out.println("End temp :" +temp);
            count++;
        }
    }

    public int[] getBest() {
        bestTour = new int[bestIndices.size()];
        int i = 0;
        for(int index: bestIndices) {
            bestTour[i] = index;
            i++;
        }
        return bestTour;
    }

    public double getBestDist() {
        return bestDist;
    }

    //Method to combine to the plot series to be sent back to the panel
    public XYDataset getDataSet() {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        dataSet.addSeries(plotSeries);
        dataSet.addSeries(bestSeries);
        return dataSet;
    }

}