package com.Projects;

import javax.swing.*;
import java.util.*;

/*
 * Ronan Conneely 18169899
 * Stephen MacSweeney 18173837
 * Keith Hennigan 18178715
 */
public class Main {

    private static int[] goalState;
    private static int nodeID = 1;

    public static void main(String[] args) {

        int[] startState = getInitialBoard();
        goalState = getGoalState();

        int manhattanDistance = calculateManhattan(createBoard(startState), goalState);
        int costFromStart = 0;//g
        //create a 2-D array
        int[][] board = createBoard(startState);

        List<Node> path = new ArrayList<>();
        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();
        List<Node> children;

        long start = System.currentTimeMillis();
        Node currentNode = new Node(board, (manhattanDistance + costFromStart), 0, nodeID, costFromStart, manhattanDistance);////////first node
        nodeID++;
        //add the initial node to open list
        open.add(currentNode);
        while (currentNode.getH() != 0) {
            //sort the open list in ascending order by f
            Collections.sort(open);
            //take the node with the lowest f value in open
            currentNode = open.get(0);
            //remove it from open
            open.remove(currentNode);
            //add it to closed
            closed.add(currentNode);

            if (currentNode.getH() == 0) {
                System.out.println("GOAL!!!!\n");
                break;
            } else {
                //add the possible moves to children
                children = possibleMoves(currentNode.getNode(), currentNode.getG(), currentNode.getNodeID());

                for (Node childNode : children) {
                    //check if the child state exists in closed and open
                    Node nodeClosed = hasSameNode(childNode.getNode(), closed);
                    Node nodeOpen = hasSameNode(childNode.getNode(), open);
                    //check if it's in closed
                    if (nodeClosed != null) {
                        continue;
                    } else if (nodeOpen != null) {
                        //if the state exists in open and the child has a lower g than the open node
                        if (nodeOpen.getG() > childNode.getG()) {
                            //swap them
                            open.remove(nodeOpen);
                            open.add(childNode);
                        }
                    }
                    //otherwise, add it to open
                    else {
                        open.add(childNode);
                    }

                }
            }
        }
        long end = System.currentTimeMillis();
        if (currentNode.getH() != 0) {
            System.out.println("Open list is empty");
        }

        //add the last node to the path
        path.add(currentNode);
        //set id to the node's parent's ID
        int id = currentNode.getParentID();
        //search the closed list for parents
        for (int i = closed.size() - 1; i > -1; i--) {
            Node node = closed.get(i);
            //if the current node is the last node's parent
            if (closed.get(i).getNodeID() == id) {
                //add it to the path
                path.add(node);
                //set the id to the current node's parent ID
                id = node.getParentID();
            }
        }

        Collections.sort(path, Node::compareToParents);
        //print out the path
        for (Node node : path) {
            System.out.println(printBoard(node.getNode()) + "\n------");
        }

        System.out.println("\nCompleted in " + currentNode.getG() + " moves.");
        System.out.println("Time solved = " + ((end - start) / 1000) + " seconds");


    }

    /**
     * This method checks a list for a corresponding state, by comparing 2D-arrays
     * @param currentNode is the current state
     * @param list the list to be checked
     * @return a Node object or null
     */
    public static Node hasSameNode(int[][] currentNode, List<Node> list) {
        for (Node node : list) {
            if (Arrays.deepEquals(node.getNode(), currentNode)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Class Node for creating a Node object
     */
    public static class Node implements Comparable<Node> {
        private int[][] node;
        private int f;
        private int parentID;
        private int nodeID;
        private int g;
        private int h;

        public Node(int[][] node, int f, int parent, int nodeID, int g, int h) {
            this.node = node;
            this.f = f;
            this.parentID = parent;
            this.nodeID = nodeID;
            this.g = g;
            this.h = h;
        }

        public int[][] getNode() {
            return node;
        }

        public int getF() {
            return f;
        }

        public int getParentID() {
            return parentID;
        }

        public int getNodeID() {
            return nodeID;
        }

        public int getG() {
            return g;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public String getNodeAsString() {
            return printBoard(node);
        }

        @Override
        public String toString() {
            return "--------------\n" +
                    printBoard(node) +
                    "\ng = " + g +
                    "\nh = " + h +
                    "\nf = " + f;
//                    "\nparentID = " + parentID +
//                    "\nnodeID = " + nodeID;
        }

        //compareTo for sorting the open list by F value
        @Override
        public int compareTo(Node node) {
            if (this.getF() > node.getF()) {
                return 1;
            } else if (this.getF() < node.getF()) {
                return -1;
            } else {
                return -1;
            }
        }

        //compareTo for sorting the final path
        public int compareToParents(Node node) {
            if (this.getParentID() > node.getParentID()) {
                return 1;
            } else if (this.getParentID() < node.getParentID()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * This method is used to create a copy of a 2D array, to avoid pass by reference issues
     *
     * @param inputArray is the array to be copied
     * @return the new array
     */
    public static int[][] createCopy(int[][] inputArray) {
        int[][] copiedArray = new int[inputArray.length][inputArray.length];
        for (int row = 0; row < inputArray.length; row++) {

            for (int column = 0; column < inputArray.length; column++) {

                copiedArray[row][column] = inputArray[row][column];
            }
        }

        return copiedArray;
    }


    /**
     * The method calculates the manhattan distance heuristic. The method calculates the distance of
     * numbers in the current state, and how far they are from reaching their destination of the goal state.
     * @param currentState the current node state
     * @param goalState the goal state
     * @return the distance
     */
    public static int calculateManhattan(int[][] currentState, int[] goalState){
        //convert the current state to 1D array
        int[] state = toOneD(currentState);
        int distance =0;

        //x coordinates for each element
        int[] arrayX = new int[]{0,1,2,0,1,2,0,1,2};
        //y coordinates
        int[] arrayY = new int[]{0,0,0,1,1,1,2,2,2};

        int xPosition, yPosition,coordinates;

        for(int i = 0; i < state.length; i++){
            //get the x position of the current element
            xPosition = Math.abs(arrayX[i] - arrayX[getIndexOf(goalState, state[i])]);
            //get the y position of the current element
            yPosition = Math.abs(arrayY[i] - arrayY[getIndexOf(goalState, state[i])]);
            //add together for xy
            coordinates = xPosition+yPosition;
            //add the coordinates to the total distance
            distance+=coordinates;
        }
        return distance;
    }

    /**
     * This method prints out the possible moves
     * @param board is the current state of the board
     */
    public static List<Node> possibleMoves(int[][] board, int g, int parentID){

        int[][] boardCopy;
        //this will represent the number that can move
        int numberToMove;
        List<Node> children = new ArrayList<>();
        //increment g as we are going to a new level
        g += 1;
        int h;

        //right horizontal heuristic

        //first we need to copy the array with the createCopy() method to avoid changing the array
        //passed as a parameter
        boardCopy = createCopy(board);
        //assign numberToMove the number returned from the function verticalHeuristic. The method returns 0
        //if the move is not possible. If it returns 0 or greater the condition of the if statement will
        //be met and will print out the move
        //This is the down vertical heuristic
        numberToMove = horizontalHeuristic(boardCopy, "right");
        if(numberToMove >= 0){
            //calculate our distance
            h = calculateManhattan(boardCopy,goalState);   //f = outOfPlace + g
            //add a new Node to children, pass its new state, f, the parent's ID, it's new ID, the current g, and the distance
            children.add(new Node(boardCopy,(h+g), parentID, nodeID, g, h));
            //increment the node ID to keep it unique
            nodeID++;

        }

        //left horizontal heuristic
        boardCopy = createCopy(board);
        numberToMove = horizontalHeuristic(boardCopy, "left");
        if(numberToMove >= 0){

            h = calculateManhattan(boardCopy,goalState); //f = outOfPlace + g
            children.add(new Node(boardCopy,(h+g), parentID, nodeID, g, h));
            nodeID++;

        }

        //up vertical heuristic
        boardCopy = createCopy(board);
        numberToMove = verticalHeuristic(boardCopy, "up");
        if(numberToMove >= 0){

            h = calculateManhattan(boardCopy,goalState);  //f = outOfPlace + g
            children.add(new Node(boardCopy,(h+g), parentID, nodeID, g, h));
            nodeID++;

        }

        boardCopy = createCopy(board);
        numberToMove = verticalHeuristic(boardCopy, "down");
        if(numberToMove >= 0){

            h = calculateManhattan(boardCopy,goalState);  //f = outOfPlace + g
            children.add(new Node(boardCopy,(h+g), parentID, nodeID, g, h));
            nodeID++;

        }

        return children;
    }

    /**
     * This method assesses the current state of the board and checks if the 0 can be swapped with
     * the number below or above it.
     * @param boardCopy is the current state of the board
     * @return numberToMove, the number that can move into the empty space
     */
    public static int verticalHeuristic(int[][] boardCopy, String direction){
        int emptySpace = 0, numberToMove = 0, position = 0;

        //first we check if it's an up or down. Assign -1 if it's up, or 1 if its down, This will decide
        //the row later in the for loop
        if(direction.equalsIgnoreCase("up")){
            position =  -1;
        }else if(direction.equalsIgnoreCase("down")){
            position =  1;
        }

        //wrapped in a try catch encase the we go out of bounds. If we go out of bounds that means
        //there is no number that can move into the blank space from below, and -1 is returned
        //Our aim here is to stay in the same column, but change row. So if position == 1, we swap
        //the tile below the blank space. If position == 1 we swap above
        try {
            for (int row = 0; row < boardCopy.length; row++) {
                for (int column = 0; column < boardCopy.length; column++) {
                    //if the current position is == 0
                    if (boardCopy[row][column] == 0) {
                        //copy the 0 to emptySpace
                        emptySpace = boardCopy[row][column];
                        //assign the number to be moved to numberToMove which will be returned later
                        numberToMove = boardCopy[row + position][column];
                        //swap 0 and the number that can move into it's place with the next two lines
                        boardCopy[row][column] = boardCopy[row + position][column];
                        boardCopy[row + position][column] = emptySpace;

                        return numberToMove;
                    }
                }
            }
        }catch(Exception anError){
            //return -1 if we go out of bounds
            return -1;
        }
        return -1;
    }

    /**
     * This method assesses the current state of the board and checks if the 0 can be swapped with
     * the number to the left or right.
     * @param boardCopy is the current state of the board
     * @return numberToMove, the number that can move into the empty space
     */
    public static int horizontalHeuristic(int[][] boardCopy, String direction){
        int emptySpace = 0, numberToMove = 0, position = 0;

        //first we check if it's an left or right. Assign -1 if it's left, or 1 if it's right. This will decide
        //the column later in the for loop
        if(direction.equalsIgnoreCase("left")){
            position =  -1;
        }else if(direction.equalsIgnoreCase("right")){
            position =  1;
        }

        //wrapped in a try catch encase the we go out of bounds. If we go out of bounds that means
        //there is no number that can move into the blank space from below, and -1 is returned
        //Our aim here is to stay in the same row, but change column. So if position == 1, we swap
        //the tile to the right of the empty space. If position == 1 we swap to the left

        try {
            for (int row = 0; row < boardCopy.length; row++) {
                for (int column = 0; column < boardCopy.length; column++) {
                    //if the current position is == 0
                    if (boardCopy[row][column] == 0) {
                        //copy the 0 to emptySpace
                        emptySpace = boardCopy[row][column];
                        //assign the number to be moved to numberToMove which will be returned later
                        numberToMove = boardCopy[row][column + position];
                        //swap 0 and the number that can move into it's place with the next two lines
                        boardCopy[row][column] = boardCopy[row][column + position];
                        boardCopy[row][column + position] = emptySpace;

                        return numberToMove;
                    }
                }
            }
        }catch(Exception anError){
            //return -1 if we go out of bounds
            return -1;
        }
        return -1;
    }


    /**
     * Method creates a 3x3 board by creating a 2D array
     * @param startState is the start state
     * @return a 2D array
     */
    public static int[][] createBoard(int[] startState){
        //get the square root of(startState.length) and create a board of that size
        int size = (int)Math.sqrt(startState.length);
        int[][] board = new int[size][size];

        //using a nested for loop we can access the rows and columns of the board
        //int index will be used to access the startState array and assign the numbers to the 2D array
        for(int row = 0, index = 0; row < board.length; row++){
            for (int column = 0; column < board.length; column++, index++){
                board[row][column] = startState[index];
            }
        }

        return board;
    }

    /**
     * This method prints a 2D array to the command line to create a visual representation of the board
     * @param board
     */
    public static String printBoard(int[][] board){
        String output = "";

        for(int row = 0, index = 0; row < board.length; row++, index++){
            for (int column = 0; column < board.length; column++){
                output += board[row][column] + " ";
            }
            if(row < board.length-1) {
                output += "\n";
            }
        }

        return output;
    }

    /**
     * Method gets the index of an element in an array
     */
    public static int getIndexOf(int[] state, int position){

        int index = 0;

        //search for our position
        for(int i = 0; i < state.length; i++){
            //if the current element is equal to position
            if(state[i] == position){
                index = i;
            }
        }

        return index;
    }

    /**
     * method turns 2D-array passed as a parameter to a 1D-array
     */
    public static int[] toOneD(int[][] array) {
        int[] oneArray = new int[array.length*array.length];

        for (int i = 0, index = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++, index++) {
                oneArray[index] = array[i][j];
            }
        }
        return oneArray;
    }

    /**
     * This method prompts the user for an input state using JOptionPane. It then checks using the method isValid()
     * if the input follows the required formatting. If true, it will parse the String to an int array using the method
     * parseToIntArray(). After this, isUnique() will be called to check if each number is unique.
     * This method will loop until input criteria is met
     * @return
     */
    public static int[] getInitialBoard(){
        String startState;
        int[] initialBoard = new int[9];
        boolean flag = false;

        //first ask the user for a start state. Then validate the input
        do {
            try {
                startState = JOptionPane.showInputDialog(null, "Input the start state in the format \"1 2 3 4 5 6 7 8 0\"" +
                        " where each digit is unique and separated by a space.");

                //first check if the string matches the correct format
                if (isValid(startState)) {
                    //convert the string to an array
                    initialBoard = parseToIntArray(startState);
                    //check if each number is unique
                    if (isUnique(initialBoard)) {
                        //if it's unique, change flag to true and the loop will finish
                        flag = true;
                    } else {
                        JOptionPane.showConfirmDialog(null, "Input state does not contain all unique numbers");
                    }
                    //check if there is a nine in the array
                    if (!isInRange(initialBoard)) {
                        //if it's unique, change flag to true and the loop will finish
                        JOptionPane.showConfirmDialog(null, "Input state cannot contain the number 9");
                        flag = false;
                    }
                }
                //otherwise, input does not correspond to format 1 2 3 4 5 .....
                else {
                    JOptionPane.showConfirmDialog(null, "Input state does not correspond to the correct format of \"1 2 3 4 5 6 7 8 0\"");
                }
            }catch(NullPointerException error){
                JOptionPane.showConfirmDialog(null, error);
            }


        }while(!flag);

        return initialBoard;
    }


    /**
     * does the exact same as getInitialBoard()
     * @return
     */
    public static int[] getGoalState(){
        boolean flag = false;
        String endState;
        int[] endStateArray = new int[9];

        //now ask the user for an end state. Then validate the input
        do {
            try {
                endState = JOptionPane.showInputDialog(null, "Input the Goal state in the format \"1 2 3 4 5 6 7 8 0\"" +
                        " where each digit is unique and separated by a space.");

                if (isValid(endState)) {
                    endStateArray = parseToIntArray(endState);

                    if (isUnique(endStateArray)) {
                        flag = true;
                    } else {
                        JOptionPane.showConfirmDialog(null, "Input state does not contain all unique numbers");
                    }
                    //check if there is a 9
                    if (!isInRange(endStateArray)) {
                        JOptionPane.showConfirmDialog(null, "Input state cannot contain the number 9");
                        flag = false;
                    }
                } else {
                    JOptionPane.showConfirmDialog(null, "Input state does not correspond to the correct format of \"1 2 3 4 5 6 7 8 0\"");
                }
            } catch(NullPointerException error){
                JOptionPane.showConfirmDialog(null, error);
            }

        }while(!flag);

        return endStateArray;
    }

    /**
     * checks if each number in the array is unique by using a for loop to check each element in the array
     * @param inputArray is the board state
     * @return true or false
     */
    public static boolean isUnique(int[] inputArray){
        //checkList will keep track of the numbers that have been checked
        List checkList = new ArrayList<Integer>();
        int digit;

        checkList.add(inputArray[0]);
        for(int i = 1; i < inputArray.length; i++){
            //digit is assigned the current element of the array
            digit = inputArray[i];
            //check the index of digit in the checkList. If the index is greater than -1, it exists already and
            //the input array is not filled with unique numbers. We also don't want 9, so if that exists
            //return false
            if(checkList.indexOf(digit) > -1 || digit == 9){
                return false;
            }//otherwise add the current element to the checkList
            else{
                checkList.add(digit);
            }
        }
        return true;
    }

    /**
     * method checks if the input matches the Regular Expression
     * @param state is the input state
     * @return true or false
     */
    public static boolean isValid(String state){
        String pattern = "^\\d\\s\\d\\s\\d\\s\\d\\s\\d\\s\\d\\s\\d\\s\\d\\s\\d$";

        if(state.matches(pattern)){
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * Method parses each digit from a String to a new int array using a for loop and Integer.parseInt();
     * @param state
     * @return
     */
    public static int[] parseToIntArray(String state){
        int [] stateArray = new int[9];
        String[] stringArray;

        //splits string into string array and gets rid of the spaces
        stringArray = state.split(" ");
        for(int i = 0; i < stateArray.length; i++){
            //parse int from String array to int array
            stateArray[i] = Integer.parseInt(stringArray[i]);
        }

        return stateArray;
    }

    /**
     * The method just makes sure that the digit 9 is not present in the array
     */
    public static boolean isInRange(int[] board){
        for(int i = 0; i < board.length; i++){
            if(board[i] == 9){
                return false;
            }
        }
        return true;
    }
}

