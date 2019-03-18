# TSP_Main
This project was developed by Vidhya Rajendran as a part of an Independent Study with Prof.Brian Johnson. 
This code can solve four optimization heuristics - Hill Climbing, Genetic Algorithm, Ant Colony Swarm, and Simulated Annealing.

The user is prompted to provide a set of points by clicking on the input panel. There is also the option of "test" points which is a
predefined set of input points. The user has the flexibility to choose a particular optimization heuristic to run from the option buttons.
There is also an option to find the best path using the Brute Force method which has a time complexity of O(n^2 ∗2^n). This could be used as a 
comparison between the time taken to find "the best" and "the optimal" path using Brute force and Heuristic methods, respectively.

Hill Climbing - this heuristic uses a greedy approach to find the search moves. This algorithm examines all the neighbouring nodes and 
then selects the node closest to the solution state, as the next node. The starting point for this approach is randomised. Drawbacks of 
this appraoch is that it finds the local optimum solution which doesnt necessarily coincide with the global optimum.

Genetic Algorithm - this is an adaptive heuristic search algorithm based on the evolutionary ideas of natural selection (Darwin's theory). 
The initial population is composed of randomly generated paths. They are tested for their fitness based on their path length. The two fittest 
individuals are chosen for reproduction. The subset of the path from the first parent is fused with a subset of the path from the second path 
produce an offspring. This offspring is put back into the population set and the least fit individual is culled from the list. Hence, the population
size is maintained. Over the evolutions, mutation occurs in the offspring moving the population away from the local optimum.

Ant Colony Swarm - this heuristic is based on the behaviour of the ants searching for food. 
Behaviour of the ants - At first, the ants wander randomly and when an ant finds a source of food, it walks back to the colony leaving
markers (pheromones) that show the path has food. When other ants come across the markers, they are likely to follow the path with a certain
probability. If they do, they then populate the path with their own markers. It gets stronger until there is a single path for the ants to follow.
In this algorithm, the ants are tuned to touch upon all the points and return to the base. Their distance travelled is computed and the ant that
the least distance gets to deposit more pheromones along its path. As more and more ants follow this trail, the pheromones build up until all
the ants follow a single path.

Simulated Annealing - this heuristic mimics the metallurgy proccess of heating and controlled cooling of a material to reduce their defects.
This heuristic helps skip the local optimum solution by accepting path arrangements which are worse than the current best path. The Boltzmann function 
of probability distribution is used as a determinant for the acceptance of "not-so-good" solutions. 

Output - Once the solution is computed, the graph of the solution convergence, best tour length and the time taken to compute that best tour is 
displayed on the panel. This helps in drawing a comparison between the time taken and the quality of the solution achieved from the different
heuristic algorithms. 
