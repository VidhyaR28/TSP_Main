// This project was developed by Vidhya Rajendran,
// a student of Department of Architecture, University of Washington, Seattle,
// as a part of an Independent study with Prof.Brian Johnson

// This code can take in points from the users and compute for the shortest distance using
// four optimization heuristics - Hill Climbing, Genetic Algorithm, Ant Colony Swarm, and Simulated Annealing.
// There is also an option to run the points on Brute Force algorithm which checks for every possible combination.
// And hence, runs on O(n!). Anything above 10 points takes a lot of computing time on the Brute force method.

package com.company;

import javax.swing.*;

public class RunAlgorithm {
    public static void main(String[] args) {
        //Creating a frame
        CitiesFrame frame = new CitiesFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}