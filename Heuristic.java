package searchclient;

import java.util.Comparator;
import java.util.ArrayList;

public abstract class Heuristic implements Comparator<Node> {
    public ArrayList<Entity> goalsOnly = new ArrayList<Entity>();
    public Heuristic(Node initialState) {
        // Here's a chance to pre-process the static parts of the level.

        for (int i = 0; i<initialState.goals.length; i++){
            for (int j =0; j<initialState.goals[0].length; j++){
                if (initialState.goals[i][j] != '\u0000') goalsOnly.add(new Entity(i,j,initialState.goals[i][j]));
            }
        }
    }

    public int h(Node n) {
       int res = 0;
       int prevBoxI = 0;
       int prevBoxJ = 0;
       boolean trig = false;
       int distBetwBoxes = 0;
         for (int i=0; i<n.boxes.length; i++){
            for (int j=0; j<n.boxes[0].length; j++){
                if (n.boxes[i][j] != '\u0000'){		
		    if (trig == false) trig = true;
		    else {
		    	distBetwBoxes = distBetwBoxes + manhDist(prevBoxI, prevBoxJ, i, j);
		    }					
		    prevBoxI = i;
		    prevBoxJ = j;									
                    char matchingGoal = Character.toLowerCase(n.boxes[i][j]);
                    short distToClosestGoal = 9999;
                    for (int k=0; k<goalsOnly.size(); k++){
                        if (matchingGoal == goalsOnly.get(k).val){
                            short distToCurGoal = manhDist(i,j,goalsOnly.get(k).i, goalsOnly.get(k).j);
                            if (distToCurGoal < distToClosestGoal) distToClosestGoal = distToCurGoal;
                        }
                    }
					//System.err.println("DEBUG: do we get here?: " + distToClosestGoal);		
                     res = res + ((int)distToClosestGoal);//*((int)distToClosestGoal);								 
                }
            }
        } 
	return 5*res + distBetwBoxes;
    }

    public int manhDist(int i1, int j1, int i2, int j2){ // between cells of a map, not between state nodes
        int diffI = Math.abs(i1-i2);
        int diffJ = Math.abs(j1-j2);
        return diffI+diffJ;
    }

    public abstract int f(Node n);

    @Override
    public int compare(Node n1, Node n2) {
        return this.f(n1) - this.f(n2);
    }

    class Entity {

        public int i;
        public int j;
        public char val;

        public Entity(int i, int j, char val) {
            this.i = i;
            this.j = j;
            this.val = val;
        }
    }

    public static class AStar extends Heuristic {
        public AStar(Node initialState) {
            super(initialState);
        }

        @Override
        public int f(Node n) {
            return n.g() + this.h(n);
        }

        @Override
        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(Node initialState, int W) {
            super(initialState);
            this.W = W;
        }

        @Override
        public int f(Node n) {
            return n.g() + this.W * this.h(n);
        }

        @Override
        public String toString() {
            return String.format("WA*(%d) evaluation", this.W);
        }
    }

    public static class Greedy extends Heuristic {
        public Greedy(Node initialState) {
            super(initialState);
        }

        @Override
        public int f(Node n) {
            return this.h(n);
        }

        @Override
        public String toString() {
            return "Greedy evaluation";
        }
    }
}
