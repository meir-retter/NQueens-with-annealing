/**
 * Created by Meir on 4/13/2015.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Collections;


class NQueensRevised {
    // size of board, will be set in the main() method at the end of the program
    static int n;

    // basic counter function because
    // I can't find any java counterpart to python's .count() method
    static int counter(ArrayList<Integer> itemList, int itemToCheck) {
        int count = 0;
        for (int i : itemList) {
            if (i == itemToCheck) {
                count++;
            }
        }
        return count; // returns the number of times itemToCheck appears in itemList
    }

    static char[][] createBoard(ArrayList<Integer> arr) {
        // '#' represents a queen; I used it because it's easier to see than most chars
		/*

	     Takes a single ArrayList of length n, where
	     every element in the ArrayList is an int in [0,n).
	     Returns a 2D array of n rows, each of length n.
	     In this array, all elements are ' ' except
	     those at positions specified by the ArrayList. For example:

	     createBoard([0,2,1,3,5,4,6,7])

	     would return

	     {{'#', ' ', ' ', ' ', ' ', ' ', ' ', ' '},    --> Queen at index 0
	      {' ', ' ', '#', ' ', ' ', ' ', ' ', ' '},    --> Queen at index 2
	      {' ', '#', ' ', ' ', ' ', ' ', ' ', ' '},    etc.
	      {' ', ' ', ' ', '#', ' ', ' ', ' ', ' '},
	      {' ', ' ', ' ', ' ', ' ', '#', ' ', ' '},
	      {' ', ' ', ' ', ' ', '#', ' ', ' ', ' '},
	      {' ', ' ', ' ', ' ', ' ', ' ', '#', ' '},
	      {' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'}}
	    */


        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            board[i][arr.get(i)] = '#';
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != '#') {
                    board[i][j] = ' ';
                }
            }
        }
        return board;
    }

    static int scoreArr(ArrayList<Integer> arr) {
        // tests how good of a solution arr is
        // lower is better
        // there is only one queen in every row and column
        // so only possible problems are queens in the same diagonal
        int s = 0;
        ArrayList<Integer> diags1 = new ArrayList<Integer>();
        ArrayList<Integer> diags2 = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            // need data on both positively and negatively sloping diagonals
            diags1.add(arr.get(i) - i);
            diags2.add(arr.get(i) - (n-i));
            // duplicate integers in either diag represent a diagonal conflict
        }
        // test for duplicates
        // use a set to ensure that each diagonal gets tested only once
        HashSet<Integer> set1 = new HashSet<Integer>();
        HashSet<Integer> set2 = new HashSet<Integer>();
        for (int i : diags1) {
            set1.add(i);
        }
        for (int i : diags2) {
            set2.add(i);
        }
        for (int i : set1) {
            s += (counter(diags1, i) - 1);
        }
        for (int i : set2) {
            s += (counter(diags2, i) - 1);
        }
        return s;
    }

    static void annealing() {
        // uses simulated annealing to find a perfect solution
        // to use brute force would have taken O(n!) time
        int checks = 0;
        int swaps = 0;
        Random randomGenerator = new Random();
        ArrayList<Integer> sol = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            sol.add(i);
        }
        Collections.shuffle(sol);
        // sol now represents a Chess board with a queen in each row and column
        // the initial temperature and coolingRate are parameters that can be altered
        // probably not optimal right now
        double T = 1; // initial temperature
        double coolingRate = .001;
        while (scoreArr(sol) != 0) {

            T *= (1-coolingRate); //cool the system

            // create a copy of sol
            ArrayList<Integer> newsol = new ArrayList<Integer>();
            newsol.addAll(sol);

            // swap two numbers in newsol
            int first = randomGenerator.nextInt(n);
            int second = randomGenerator.nextInt(n);
            int f1 = newsol.get(first);
            int f2 = newsol.get(second);
            newsol.set(first, f2);
            newsol.set(second, f1);
            checks += 1;
            double r = Math.random();
            int currentScore = scoreArr(sol);
            int newScore = scoreArr(newsol);
            // if newScore is better or equal to currentScore, accept newsol
            // otherwise, decide whether to accept newsol based on
            // a function of T and how much worse newsol is
            // this acceptance function becomes more strict as T decays,
            // eventually becoming a greedy hill climber
            if ((newScore <= currentScore) ||
                    ((newScore > currentScore) &&
                            (r < Math.pow(Math.E, (-(newScore-currentScore)/T))))) {
                sol = newsol;
                swaps += 1;
                System.out.println("conflicts: " + newScore + ", " + "T = " + T + ", " + "checks: " + checks + ", " + "swaps: " + swaps);

            }
        }
        if (n < 20) {
            printBoard(createBoard(sol));
        } else if (n >= 20 && n <= 100) {
            printSmallBoard(createBoard(sol));
        }
        System.out.println(sol);
    }

    static void printBoard(char[][] board) {
        for (int i = 0; i <= (n*4); i++) {
            if (i == n*4) {
                System.out.println('+');
            } else if (i % 4 == 0 && (i != n*4)) {
                System.out.print('+');
            } else {
                System.out.print('-');
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j != n-1) {
                    System.out.print("| " + board[i][j] + " ");
                } else {
                    System.out.println("| " + board[i][j] + " |");
                }
            }
            for (int j = 0; j <= (n*4); j++) {
                if (j == n*4) {
                    System.out.println('+');
                } else if  (j % 4 == 0 && (j != n*4)) {
                    System.out.print('+');
                } else {
                    System.out.print('-');
                }
            }
        }
    }

    static void printSmallBoard(char[][] board) {
        // less aesthetically appealing, but more compact
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j != n-1) {
                    System.out.print((board[i][j] == '#' ? '#' : (char)20)+ " ");

                } else {
                    System.out.println((board[i][j] == '#' ? '#' : (char)20));
                }
            }
        }
    }


    public static void main(String[] args) {
        n = 20;
        annealing();
    }
}