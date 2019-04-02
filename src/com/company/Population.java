package com.company;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.geom.Point2D;
import java.util.*;

import static java.lang.Math.sqrt;


public class Population extends peopleComparator {
    private static final int POPSIZE= 50;
    private static final int GENERATIONS= 500;
    private static final int EVOLUTION= 500;

    private ArrayList<Point2D> points;
    //heap contains the people in the population
    private List<people> heap;
    //graph contains the distances between all the points
    private double[][] graph;
    private int heapSize;
    private int size;

    ArrayList<Integer> basic = new ArrayList<>();
    int[] bestTour;
    int mutationCount = 0;

    XYSeries bestSeries = new XYSeries("Best Distances");
    XYSeries plotSeries = new XYSeries("Other Distances");
    int plotIteration = 0;
    long timeCount;

    // Constructor
    public Population(ArrayList<Point2D> cities, long time) {
        heap = new LinkedList<>();
        points = cities;
        size = cities.size();
        graph = new double[size][size];
        timeCount = time;
        makeGraph();

        heapSize = 0;
        for (int i = 0; i < size; i++) {
            basic.add(i);
        }

        // Creating an initial population with random tour arrangements
        for (int i = 0; i < POPSIZE; i++) {
            ArrayList<Integer> list = (ArrayList<Integer>) makeList().clone();
            double dist = getListDist(list);
            people tempPerson = new people(list, dist);
            addPerson(tempPerson);
        }
        bestTour = new int[size];

        solve();
    }

    private void solve() {
        int evol = 0;
        while (evol < EVOLUTION) {
            //every evolution goes on for a certain number of generations
            for (int i = 1; i <= GENERATIONS; i++) {
                crossover();
                mutationCount++;
                //track for the mutation factor through the generations
                if (mutationCount % 10 == 0) {
                    mutate();
                }
                //At regular intervals, then tour length and time are recorded for plotting the chart
                plotIteration++;
                if (plotIteration%2 == 0) {
                    bestSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, getBestDist());
                    plotSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, heap.get(POPSIZE-10).getDist());
                }
            }
            evol++;
        }
    }

    //Matrix of distances between all the points
    private void makeGraph(){
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
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
        return sqrt((x*x) + (y*y));
    }

    //Method to make random tour arrangements for the initial population
    private ArrayList<Integer> makeList() {
        ArrayList<Integer> temp = (ArrayList<Integer>) basic.clone();
        Collections.shuffle(temp);
        Collections.shuffle(temp);
        return temp;
    }

    //Method to get the tour distance of a particular tour arrangement
    private double getListDist(ArrayList<Integer> list) {
        double dist = graph[list.get(0)][list.get(size-1)];
        for (int i = 0; i < size -1; i++) {
            dist = dist + graph[list.get(i)][list.get(i+1)];
        }
        return dist;
    }

    //Method to add a new member into the population heap
    //the members of the population are arranged in the increasing order of their tour lengths
    //When a new member is put into the population, they are arranged based on their tour distance
    private void addPerson(people currentPerson) {
        if (heapSize == 0) {
            heap.add(currentPerson);
            heapSize++;
        } else {
            if (currentPerson.getDist() < heap.get(0).getDist()) {
                heap.add(0,currentPerson);
                heapSize++;
            } else if (currentPerson.getDist() > heap.get(heapSize-1).getDist()) {
                heap.add(heapSize,currentPerson);
                heapSize++;
            } else {
                people prev = heap.get(0);
                int i = 0;
                for(people temp: heap) {
                    if (i != 0) {
                        if (currentPerson.getDist() <= temp.getDist() && currentPerson.getDist() >= prev.getDist()) {
                            break;
                        }
                        prev = temp;
                    }
                    i++;
                }
                heap.add(i, currentPerson);
                heapSize++;
            }
        }
    }

    //Method to produce children from the fittest parents
    private void crossover(){
        Random rand = new Random();
        rand.setSeed(rand.nextInt(1000));

        //The top two parents are chosen for crossover - they are just the first two members in the population heap
        ArrayList<Integer> parent1 = (ArrayList<Integer>) heap.get(0).getList().clone();
        ArrayList<Integer> parent2 = (ArrayList<Integer>) heap.get(1).getList().clone();

        ArrayList<Integer> child1 = new ArrayList<>();
        ArrayList<Integer> child2 = new ArrayList<>();


        //Choosing the crossover points
        rand.setSeed(rand.nextInt(1000));
        //A random crossover location is chosen
        int crossover = rand.nextInt(size);

        //The first half of the parent arrangement is given directly to the children
        for (int i = 0; i <= crossover; i++) {
            child1.add(parent1.get(i));
            child2.add(parent2.get(i));
        }

        //The second half of the arrangement for the children comes from the other parent.
        //But to the avoid repetition of the cities, those cities which aren't already present in the child,
        //is copied over from the other parent.
        for(int i = 0; i < size; i++) {
            if (!child1.contains(parent2.get(i))) {
                child1.add(parent2.get(i));
            }

            if (!child2.contains(parent1.get(i))) {
                child2.add(parent1.get(i));
            }
        }

        //Making children objects and adding them to heap
        double child1Dist = getListDist(child1);
        people children1 = new people(child1, child1Dist);
        double child2Dist = getListDist(child2);
        people children2 = new people(child2, child2Dist);

        addPerson(children1);
        addPerson(children2);

        //Removing the last two people in the population heap - since they are the least fit in the entire population
        heap.remove(heapSize-1);
        heapSize--;
        heap.remove(heapSize-1);
        heapSize--;
    }

    //Method which randomly chooses two cities and swaps them in the arrangement
    private void mutate() {
        ArrayList<Integer> newList = (ArrayList<Integer>) heap.get(0).getList().clone();

        Random rand = new Random();
        rand.setSeed(rand.nextInt(1000));

        int a = rand.nextInt(size);
        int b = rand.nextInt(size);

        while (a == b) {
            a = rand.nextInt(size);
            b = rand.nextInt(size);
        }

        int dummy1 = newList.get(a);
        int dummy2 = newList.get(b);
        newList.set(a,dummy2);
        newList.set(b,dummy1);

        people newPerson = new people(newList, getListDist(newList));
        addPerson(newPerson);

        heap.remove(heapSize-1);
        heapSize--;
    }

    public int[] getBest() {
        ArrayList<Integer> bestList = (ArrayList<Integer>) heap.get(0).getList().clone();
        int i = 0;
        for(int index: bestList) {
            bestTour[i] = index;
            i++;
        }
        return bestTour;
    }

    public double getBestDist() {
        return heap.get(0).getDist();
    }

    public XYDataset getDataSet() {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        dataSet.addSeries(plotSeries);
        dataSet.addSeries(bestSeries);
        return dataSet;
    }
}

//Class to compare two objects of the people class
class peopleComparator implements Comparator<people> {
    @Override
    public int compare(people p1, people p2) {
        if (p1.getDist() < p2.getDist()) {
            return 1;
        } else if (p1.getDist() > p2.getDist()){
            return -1;
        }
        return 0;
    }
}

//Class which contains the information about the point and its relationship to its next best neighbour point
class people {
    private ArrayList<Integer> arrangement;
    private double dist;

    //Constructor: takes a point and sets it to this node
    people (ArrayList<Integer> list, double dist) {
        this.arrangement = list;
        this.dist = dist;
    }

    public double getDist() {
        return dist;
    }

    public ArrayList<Integer> getList() {
        return arrangement;
    }
}
