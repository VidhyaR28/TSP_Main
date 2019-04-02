package com.company;


import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;
import java.util.Random;
import java.awt.geom.*;

class antColony {
    double[][] graph;
    //Original amount of trail
    private double c = 1.0;
    //Trail preference
    private double alpha = 1;
    //Greedy preference
    private double beta = 5;
    //Trail evaporation coefficient
    private double evaporation = 0.5;
    //New trail deposit coefficient;
    private double Q = 500;
    //Probability of pure random selection of the next town
    private double pr = 0.01;
    //Maximum iterations
    private int maxIteration = 1000;
    //To keep track of the updation city for every ant
    private int count = 0;
    //To keep the track of the probability of the next city
    private double probs[];

    Random rand = new Random();

    int noCities;
    ArrayList<Point2D> points;
    double[][] trails;

    double antFactor = 0.8;
    int noAnts;
    Ant[] ants;

    double bestTourLength;
    int[] bestTour;

    XYSeries bestSeries = new XYSeries("Best Distances");
    XYSeries plotSeries = new XYSeries("Other Distances");
    int plotIteration = 0;
    long timeCount;

    //Constructor
    public antColony(ArrayList<Point2D> cities, long time) {
        points = cities;
        noCities = cities.size();
        graph = new double[noCities][noCities];
        trails = new double[noCities][noCities];
        noAnts = (int) (noCities * antFactor);
        ants = new Ant[noAnts];
        probs = new double[noCities];
        timeCount = time;
        for (int i = 0; i < noAnts; i++) {
            ants[i] = new Ant(noCities);
        }

        bestTour = new int[noCities];
        bestTourLength = 0;

        makeGraph();
        antColonyOpt();
    }

    //Make Graph of all the distances from the points
    private void makeGraph(){
        for (int i = 0; i < noCities; i++) {
            for (int j = i+1; j < noCities; j++) {
                graph[i][j] = calcDist(points.get(i), points.get(j));
                graph[j][i] = graph[i][j];
            }
            graph[i][i] = 0.0;
        }
    }

    //Method to calculate distance from one point to another
    private double calcDist(Point2D a, Point2D b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        return Math.sqrt(x*x + y*y);
    }

    //Method to control the solving of the AntColony
    private void antColonyOpt() {
        int i = 0;

        while (i < 10) {
            solve();
            i++;
        }
    }

    public int[] getBest() {
        return bestTour;
    }

    public double getBestDist() {
        return bestTourLength;
    }

    private void solve(){
        //Set the trails to 1 before the ants are set loose
        for (int i = 0; i < noCities; i++) {
            for (int j = i+1; j < noCities; j++) {
                trails[i][j] = c;
                trails[j][i] = trails[i][j];
            }
            trails[i][i] = c;
        }

        int iteration = 0;

        while (iteration <= maxIteration) {
            setupAnts();
            moveAnts();
            updateTrails();
            updateBest();
            plotIteration++;
            //At regular intervals, then tour length and time are recorded for plotting the chart
            if (plotIteration%20 == 0) {
                bestSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, bestTourLength);
                plotSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, ants[rand.nextInt(noAnts)].trailLength);
            }
            iteration++;
        }
    }

    //Method to combine to the plot series to be sent back to the panel
    public XYDataset getDataSet() {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        dataSet.addSeries(plotSeries);
        dataSet.addSeries(bestSeries);
        return dataSet;
    }

    //setting up ants - clear the tour arrangements in the ant objects
    private void setupAnts() {
        count = 0;
        for (Ant a: ants) {
            a.clear();
            //The first city that an ant visits is chosen at random
            a.toVisit(rand.nextInt(noCities));
        }
        count++;
    }

    //moving ants - the subsequent cities are chosen based on the pheromone deposited by ants during the previous rounds
    private void moveAnts() {
        while (count < noCities) {
            for (Ant a : ants) {
                a.toVisit(findCity(a));
            }
            count++;
        }
    }

    private int findCity(Ant a) {
        // sometimes just randomly select a city
        if (rand.nextDouble() < pr) {
            int t = rand.nextInt(noCities - count); // random town
            int j = -1;
            for (int i = 0; i < noCities; i++) {
                if (!a.visited(i)) {
                    j++;
                }
                if (j == t) {
                    return i;
                }
            }
        }
        // calculate probabilities of visiting every other city from the current city
        probTo(a);
        // randomly select a probability value
        double r = rand.nextDouble();
        // keep adding the probabilities of the cities to be go next and
        // the iteration when this probability crosses the random probability, that city is chosen for the next visit
        double tot = 0;
        for (int i = 0; i < noCities; i++) {
            tot += probs[i];
            if (tot >= r)
                return i;
        }

        // the code never reaches here as the sum of all the probabilities add up to 1
        throw new RuntimeException("Not supposed to get here.");
    }

    //Method to calculate the probability of visiting the next city
    private void probTo(Ant a) {
        //current city
        int i = a.trail[count-1];
        double pheromone = 0.0;
        //for all the cities not visited, the previous pheromone deposits there are summed to be used as a denominator
        for (int l = 0; l < noCities; l++) {
            if (!a.visited(l)){
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);
            }
        }

        for (int j = 0; j < noCities; j++) {
            //If the city has been visited, the probability is set to 0. If not, then the pheromone deposited
            // in that city is used as a numerator.
            if (a.visited(j)) {
                probs[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
                probs[j] = numerator / pheromone;
            }
        }
    }

    //Method to calculate the pheromone deposition based on the distances that the ant travelled
    //Shorter the distance, larger is this deposition
    private void updateTrails() {
        //evaporation
        for (int i = 0; i < noCities; i++) {
            for (int j =0; j <noCities; j++) {
                trails[i][j] *= evaporation;
            }
        }

        //contribution of each ant to the trail
        for(Ant a: ants) {
            double contribution = Q / a.getTrailLength();
            for(int i = 0; i < noCities-1; i++) {
                trails[a.trail[i]][a.trail[i+1]] += contribution;
            }
            trails[a.trail[noCities - 1]][a.trail[0]] += contribution;
        }
    }

    //update best - for every iteration, the best tour distance and the arrangement is stored
    private void updateBest() {
        if (bestTourLength == 0) {
            bestTourLength = ants[0].getTrailLength();
            System.arraycopy(ants[0].getTour(), 0, bestTour, 0, noCities);
        } else {
            for (Ant a: ants) {
                double test = a.getTrailLength();
                if (test < bestTourLength) {
                    System.arraycopy(a.getTour(), 0, bestTour, 0, noCities);
                    bestTourLength = test;
                }
            }
        }
    }

    //Inner Class for the Ant behaviour
    class Ant {
        //Tour arrangement
        int[] trail;
        //Tour length
        double trailLength = 0.0;
        //To keep track of whether a city has been visited or not
        boolean[] visited;

        //Constructor
        private Ant(int n) {
            trail = new int[n];
            visited = new boolean[n];
        }

        private boolean visited(int city){
            return visited[city];
        }

        private void clear() {
            for (int i = 0; i < noCities; i++) {
                visited[i] = false;
            }
            trailLength = 0.0;
        }

        private void toVisit(int city) {
            trail[count] = city;
            visited[city] = true;
            updateTrailLength();
        }

        private void updateTrailLength() {
            if (count != 0) {
                trailLength = trailLength + graph[trail[count-1]][trail[count]];
            }
        }

        private double getTrailLength() {
            trailLength = trailLength + graph[trail[0]][trail[noCities-1]];
            return trailLength;
        }

        private int[] getTour(){
            return trail;
        }
    }


}
