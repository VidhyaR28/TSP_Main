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
    private List<people> heap;
    private double[][] graph;
    private int heapSize;
    private int size;

    ArrayList<Integer> basic = new ArrayList<>();
    int[] bestTour;
    int mutationCount = 0;

    Random rd = new Random();

    XYSeries bestSeries = new XYSeries("Best Distances");
    XYSeries plotSeries = new XYSeries("Other Distances");
    int plotIteration = 0;
    long timeCount;

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

        for (int i = 0; i < POPSIZE; i++) {
            ArrayList<Integer> list = (ArrayList<Integer>) makeList().clone();
            // System.out.println("Person number "+ i + ": "+list.toString());
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
            System.out.println("At start: "+heap.get(0).getDist());
            for (int i = 1; i <= GENERATIONS; i++) {
                crossover();
                mutationCount++;
                if (mutationCount % 10 == 0) {
                    mutate();
                }
                plotIteration++;
                if (plotIteration%2 == 0) {
                    bestSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, getBestDist());
                    plotSeries.add((double) (System.currentTimeMillis()-timeCount)/1000, heap.get(POPSIZE-10).getDist());
                }
            }
            evol++;
            System.out.println("AT END        : "+heap.get(0).getDist());
            System.out.println("LIST: "+heap.get(0).getList().toString());
            System.out.println();
        }
    }

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

    private ArrayList<Integer> makeList() {
        ArrayList<Integer> temp = (ArrayList<Integer>) basic.clone();
        Collections.shuffle(temp);
        Collections.shuffle(temp);
        return temp;
    }

    private double getListDist(ArrayList<Integer> list) {
        double dist = graph[list.get(0)][list.get(size-1)];
        for (int i = 0; i < size -1; i++) {
            dist = dist + graph[list.get(i)][list.get(i+1)];
        }
        return dist;
    }

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

    private void crossover(){
        Random rand = new Random();
        rand.setSeed(rand.nextInt(1000));

        ArrayList<Integer> parent1 = (ArrayList<Integer>) heap.get(0).getList().clone();
        ArrayList<Integer> parent2 = (ArrayList<Integer>) heap.get(1).getList().clone();

        ArrayList<Integer> child1 = new ArrayList<>();
        ArrayList<Integer> child2 = new ArrayList<>();


        //Choosing the crossover points - METHOD 2
        rand.setSeed(rand.nextInt(1000));
        int crossover = rand.nextInt(size);

        for (int i = 0; i <= crossover; i++) {
            child1.add(parent1.get(i));
            child2.add(parent2.get(i));
        }

        for(int i = 0; i < size; i++) {
            if (!child1.contains(parent2.get(i))) {
                child1.add(parent2.get(i));
            }

            if (!child2.contains(parent1.get(i))) {
                child2.add(parent1.get(i));
            }
        }

        /*
        //Choosing the crossover points - METHOD 1
        int a = rand.nextInt(size);
        int b = rand.nextInt(size);

        while (a == b || Math.abs(a - b) < 3) {
            a = rand.nextInt(size);
            b = rand.nextInt(size);
        }

        if (b < a) {
            int dummy = a;
            a = b;
            b = dummy;
        }

        //Crossover process
        for (int i = a; i < b; i++) {
            child1.add(parent1.get(i));
            child2.add(parent2.get(i));
        }

        int count1 = 0;
        int count2 = 0;
        for(int i = 0; i < size; i++) {
            if (!child1.contains(parent2.get(i))) {
                if (count1 < a) {
                    child1.add(count1, parent2.get(i));
                    count1++;
                } else {
                    child1.add(parent2.get(i));
                }
            }

            if (!child2.contains(parent1.get(i))) {
                if (count2 < a) {
                    child2.add(count2, parent1.get(i));
                    count2++;
                } else {
                    child2.add(parent1.get(i));
                }
            }
        }
        */

        //Making children objects and adding them to heap
        double child1Dist = getListDist(child1);
        people children1 = new people(child1, child1Dist);
        double child2Dist = getListDist(child2);
        people children2 = new people(child2, child2Dist);

        System.out.println(children1.toString());
        System.out.println(children2.toString());
        System.out.println();

        addPerson(children1);
        addPerson(children2);

        //Removing the last two people
        heap.remove(heapSize-1);
        heapSize--;
        heap.remove(heapSize-1);
        heapSize--;
    }

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
    people(ArrayList<Integer> list, double dist) {
        this.arrangement = list;
        this.dist = dist;
    }

    public double getDist() {
        return dist;
    }

    public void setArrangement(ArrayList<Integer> list,  double dist) {
        this.arrangement = list;
        this.dist = dist;
    }

    public ArrayList<Integer> getList() {
        return arrangement;
    }
}
