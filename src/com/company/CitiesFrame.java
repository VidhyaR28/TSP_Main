package com.company;

//JFreeChart imports
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

class CitiesFrame extends JFrame {
    //Default dimensions of the frame
    private static final int DefaultWidth = 1950;
    private static final int DefaultHeight = 1050;
    //The scope of cities needs to be accessible across all the sub panels
    private ArrayList<Point2D> cities;
    private int[] bestTour;

    //A test condition to check whether a JChart is already displayed on the output panel
    //if this test condition is true, the current JChart is removed to display the new JChart
    boolean test2 = false;

    //Sub panels in the frame - defined here so that these panels could be accessed in other panel objects
    private CitiesPanel panel1;
    private OutputPanel panel3;

    //JFreeChart data structure to hold the plot pairs
    XYDataset data;


    public CitiesFrame() {
        setTitle("TSP");
        setSize(DefaultWidth, DefaultHeight);

        panel1 = new CitiesPanel();
        OptimizationPanel panel2 = new OptimizationPanel();
        panel3 = new OutputPanel();

        //The three panels are arranged side by side in the frame
        JSplitPane splitPane1 = new JSplitPane(SwingConstants.HORIZONTAL, panel2, panel3);
        JSplitPane splitPane = new JSplitPane(SwingConstants.VERTICAL, splitPane1, panel1);
        splitPane1.setOneTouchExpandable(false);
        splitPane1.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(600);
        this.add(splitPane);
    }


    //First panel that will take the input points from the user
    class CitiesPanel extends JPanel {
        // circles list contains all the profiles of the clicked spots
        private ArrayList<Ellipse2D> circles;
        private Ellipse2D currentCircle;
        private static final int radius = 5;
        // The mouse clicks are converted into (x,y) points
        private ArrayList<Point2D> points;
        //Test condition to trigger the tour display of the best path
        private boolean test;
        int[] index;
        int size = 0;
        Graphics2D g2;


        //Constructor
        private CitiesPanel() {
            circles = new ArrayList<Ellipse2D>();
            currentCircle = null;
            points = new ArrayList<Point2D>();
            test = false;

            //Creating and linking new buttons to panel
            JButton test1 = new JButton("TEST 1");
            add(test1);

            JButton test2 = new JButton("TEST 2");
            add(test2);

            JButton clear = new JButton("CLEAR");
            add(clear);


            //Creating and linking button action
            test1Action test1Act = new test1Action();
            test1.addActionListener(test1Act);

            test2Action test2Act = new test2Action();
            test2.addActionListener(test2Act);

            clearAction clearAct = new clearAction();
            clear.addActionListener(clearAct);


            //Creating mouseListener on the panel for the point collection
            addMouseListener(new MouseHandler());
        }

        //Method to draw the circles
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g2 = (Graphics2D) g;

            g2.setBackground(Color.white);
            g2.setPaint(Color.black);

            if (test) {
                setBackground(Color.lightGray);
            }

            //draw all circles
            for (Ellipse2D c : circles) {
                g2.fill(c);
            }

            //Happens only after the best distance is calculated
            if (test) {
                //REDEFINE IT
                for (int i = 0; i < size - 1; i++) {
                    g2.draw(new Line2D.Double(points.get(index[i]), points.get(index[i + 1])));
                }
                g2.setPaint(Color.red);
                g2.draw(new Line2D.Double(points.get(index[0]), points.get(index[size - 1])));
            }
        }

        //This method repaints the panel but also triggers the test condition that displays the best tour
        private void redraw(int[] indices) {
            this.index = indices;
            this.size = points.size();
            test = true;
            repaint();
        }

        //Over-loading of the redraw method - This method vipes the panel clean
        public void redraw() {
            test = false;
            points.clear();
            circles.clear();
            repaint();
        }

        //Method to add circles to the array
        private void add(Point2D centre) {
            double x = centre.getX();
            double y = centre.getY();

            points.add(centre);

            currentCircle = new Ellipse2D.Double();
            currentCircle.setFrameFromCenter(x, y, x + radius, y + radius);
            circles.add(currentCircle);
            repaint();
        }

        //Method that generates a set of predefined points for comparison across different heuristic methods
        private void test1() {
            add(new Point2D.Double(455,668));
            add(new Point2D.Double(1184,326));
            add(new Point2D.Double(1013,633));
            add(new Point2D.Double(779,187));
            add(new Point2D.Double(905,419));
            add(new Point2D.Double(510,347));
            add(new Point2D.Double(758,579));
            add(new Point2D.Double(655,786));
            add(new Point2D.Double(287,410));
            add(new Point2D.Double(484,94));
            add(new Point2D.Double(992,223));
            add(new Point2D.Double(1170,175));
            add(new Point2D.Double(1235,515));
            add(new Point2D.Double(923,809));
            add(new Point2D.Double(1159,795));
            add(new Point2D.Double(154,809));
            add(new Point2D.Double(399,519));
            add(new Point2D.Double(239,172));
            add(new Point2D.Double(703,352));
            add(new Point2D.Double(839,107));
        }

        //Method that generates a set of predefined points for comparison across different heuristic methods
        private void test2() {
            add(new Point2D.Double(736,140));
            add(new Point2D.Double(885,176));
            add(new Point2D.Double(1026,263));
            add(new Point2D.Double(1120,426));
            add(new Point2D.Double(1131,585));
            add(new Point2D.Double(1087,754));
            add(new Point2D.Double(955,848));
            add(new Point2D.Double(767,897));
            add(new Point2D.Double(574,871));
            add(new Point2D.Double(454,793));
            add(new Point2D.Double(367,650));
            add(new Point2D.Double(350,451));
            add(new Point2D.Double(438,268));
            add(new Point2D.Double(581,180));
        }

        private ArrayList<Point2D> getPoints() {
            return points;
        }

        //Inner class for the Action listener
        public class test1Action implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                test1();
            }
        }

        public class test2Action implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                test2();
            }
        }

        public class clearAction implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                redraw();
                cities.clear();
                panel3.clear();
            }
        }

        //Inner Class for the MouseHandler
        private class MouseHandler extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                add(e.getPoint());
            }
        }
    }


    //Second panel that lets the user choose the heuristic method
    public class OptimizationPanel extends JPanel {
        //GridBag to organize the buttons
        GridBagConstraints c;

        private OptimizationPanel() {
            this.setLayout(new GridBagLayout());

            add(new JLabel("Choose the Optimization method", JLabel.CENTER));
            c = new GridBagConstraints();

            c.insets = new Insets(3, 3, 3, 3);
            c.ipadx = 10;
            c.ipady = 15;
            c.gridx = 0;

            makeButton("BRUTE FORCE", 1);
            makeButton("HILL CLIMBING", 2);
            makeButton("SIMULATED ANNEALING", 3);
            makeButton("ANT COLONY SWARM", 4);
            makeButton("GENETIC EVOLUTION", 5);
        }

        private void makeButton(String name, int a) {
            JButton button = new JButton(name);
            c.gridy = a;
            add(button, c);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cities = panel1.getPoints();
                    panel3.clear();
                    getOption(button.getText());
                }
            });
        }

        private void getOption(String name) {
            //These if-else if condition runs based on the option chosen
            if (name.equals("ANT COLONY SWARM")) {
                //Timer on
                long startTime = System.currentTimeMillis();
                //Optimization is triggered and the startTime is sent as a parameter to collect data for the graph
                antColony optimization = new antColony(cities, startTime);
                bestTour = optimization.getBest();
                double tourDist = optimization.getBestDist();
                data = optimization.getDataSet();
                //Timer off
                long endTime = System.currentTimeMillis();
                //The best tour is displayed on the panel1 - the input panel that collected the points
                panel1.redraw(bestTour);
                test2 = true;
                //The (x,y) data collected during the optimization is sent to the output panel to build the chart
                panel3.display(endTime-startTime, tourDist, name);

            } else if (name.equals("SIMULATED ANNEALING")) {
                long startTime = System.currentTimeMillis();
                simulatedAnnealing optimization = new simulatedAnnealing(cities, startTime);
                bestTour = optimization.getBest();
                double tourDist = optimization.getBestDist();
                data = optimization.getDataSet();
                long endTime = System.currentTimeMillis();
                panel1.redraw(bestTour);
                test2 = true;
                panel3.display(endTime-startTime, tourDist, name);

            } else if (name.equals("HILL CLIMBING")) {
                long startTime = System.currentTimeMillis();
                hillClimbing optimization = new hillClimbing(cities, startTime);
                bestTour = optimization.getBest();
                double tourDist = optimization.getBestDist();
                data = optimization.getDataSet();
                long endTime = System.currentTimeMillis();
                panel1.redraw(bestTour);
                test2 = true;
                panel3.display(endTime-startTime, tourDist, name);

            } else if (name.equals("BRUTE FORCE")) {
                long startTime = System.currentTimeMillis();
                bruteForce optimization = new bruteForce(cities, startTime);
                bestTour = optimization.getBest();
                double tourDist = optimization.getBestDist();
                data = optimization.getDataSet();
                long endTime = System.currentTimeMillis();
                panel1.redraw(bestTour);
                test2 = true;
                panel3.display(endTime-startTime, tourDist, name);

            } else if (name.equals("GENETIC EVOLUTION")) {
                long startTime = System.currentTimeMillis();
                Population optimization = new Population(cities, startTime);
                bestTour = optimization.getBest();
                double tourDist = optimization.getBestDist();
                data = optimization.getDataSet();
                long endTime = System.currentTimeMillis();
                panel1.redraw(bestTour);
                test2 = true;
                panel3.display(endTime-startTime, tourDist, name);

            }
        }
    }



    //Third panel that outputs the best distance, best time and the graph for comparison
    public class OutputPanel extends JPanel {
        double time;
        double dist;
        boolean test = false;
        String name;
        JPanel chartPanel;
        Graphics g2;

        private OutputPanel() {
            JLabel begin = new JLabel("TSP Results");
            add(begin);
        }

        private void display(long time, double dist, String name) {
            System.out.println("Time taken: " + (double)time/1000 +" seconds");
            System.out.println("Best Tour: " + dist);
            this.time = (double)time/1000;
            this.dist = dist;
            test = true;
            this.name = name;
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g2 = (Graphics2D) g;

            g2.drawString("Time taken: " + time +" seconds", 10, 40);
            g2.drawString("Best Tour: " + dist, 10, 60);

            if (test) {
                JFreeChart chart = ChartFactory.createXYLineChart("CONVERGENCE OF " + name, "Time elapsed", "Distance",
                        data, PlotOrientation.VERTICAL ,true , true , false);

                XYPlot plot = chart.getXYPlot();
                NumberAxis range = (NumberAxis) plot.getRangeAxis();
                range.setRange(dist-200, dist+3000);

                chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new java.awt.Dimension( 590 , 400 ));

                GridBagConstraints c;
                this.setLayout(new GridBagLayout());
                c = new GridBagConstraints();

                c.insets = new Insets(3, 3, 3, 3);
                c.ipadx = 600;
                c.ipady = 450;
                c.gridx = 0;
                c.gridy = 150;

                add(chartPanel, c);
                this.revalidate();
                test = false;
            }
        }

        private void clear() {
            this.time = 0.0;
            this.dist = 0.0;
            if (test2) {
                test2 = false;
                this.remove(chartPanel);
                displayNew(this.getGraphics());
            }
        }

        private void displayNew(Graphics g) {
            super.paintComponent(g);
            g2 = (Graphics2D) g;

            g2.drawString("Time taken: " + time +" seconds", 10, 40);
            g2.drawString("Best Tour: " + dist, 10, 60);
        }
    }

}