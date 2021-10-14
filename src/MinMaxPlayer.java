import java.util.*;

public class MinMaxPlayer extends Player{

    private final int MAX_DEPTH = 4; // the maximum depth that we want to give
    private ArrayList<Integer[]> path = new ArrayList<>();
    private TreeMap<Integer, Integer> allDistances = new TreeMap<>();

    // Just the constructor
    public MinMaxPlayer(Board board, int playerId, String name, int score, int x, int y){
        super(playerId, name, board, score, x, y);
    }

    // Just return the move, update the path variable and declare the root node of the tree.
    @Override
    public int getNextMove(int currentPosition, int opponentCurrentPosition){
        // Create root node
        Node root = new Node();
        createMySubtree(currentPosition, opponentCurrentPosition, root, 1);
        // printNodes(root);
        // The tree is now finished
        int selectedMove = chooseMinMaxMove(root);
        System.out.println("Move selected = "+selectedMove);
        updatePath(selectedMove);
        return selectedMove;
    }

    // Use minimax to find the best move and then return it to the getNextMove().
    // In this method we will call minimax for all the children of the root node and we will
    // keep the one with the maximum score and its' corresponding move.
    public int chooseMinMaxMove(Node root){
        double bestScore = Double.NEGATIVE_INFINITY; // at the end it will hold the maximum score of the children nodes
        int bestMove = -1;

        // Let's find the evaluation that root will have after minimax algorithm is done
        // double findRootEvaluation = minimax(root, 0, true);
        //System.out.println("The root's evaluation is after minimax recursions equal to "+findRootEvaluation);

        // Now loop through the children and each time compare its' score with the currently higher one, that is
        // stored in the bestScore variable. Whenever we find a new higher, we chenge the bestMove value to the vaue of the
        // move-branch that the bestScore corresponds to.
        for(int i=0; i<root.childrenNodes.size(); i++){
            double score = minimax(root.childrenNodes.get(i), 1 ,false);
            if(score > bestScore){
                bestScore = score;
                bestMove = root.childrenNodes.get(i).getMove();
            }
        }
        if(bestMove > 0) return bestMove; // return the bestMove
        else{
            System.out.println("Move "+bestMove+" selected. Error.");
            System.exit(1);
            return 0;
        }
    }

    // Minimax algorithm implementation. The method is recursive.
    public double minimax(Node node, int depth, boolean maximizingPlayer){
        if(depth == 2 || node.childrenNodes.isEmpty()) return node.getNodeEvaluation(); // terminal condition, return the node
        // evaluation of the last nodes with the maximum depth. Obviously there aren't needed both of the if statement checks.

        // If the player who wants to select the maximum move plays, then the following happens.
        if(maximizingPlayer){
            double eva; // will get the minimax recursive result each time
            double maxEva = Double.NEGATIVE_INFINITY; // at the end it will hold the maximum score
            for(int i=0; i<node.childrenNodes.size(); i++){
                eva = minimax(node.childrenNodes.get(i), depth+1, false); // recursive call
                maxEva = Math.max(eva, maxEva); // maxEva is the maximum number of maxEva and eva. This way every time that
                // minimax returns a higher score we update the bestEva variable.
            }
            return maxEva; // we return the maximum
        }
        else{ // if the minimizing player plays
            double eva;
            double minEva = Double.POSITIVE_INFINITY;
            for(int i=0; i<node.childrenNodes.size(); i++){
                eva = minimax(node.childrenNodes.get(i), depth+1, true);
                minEva = Math.min(eva, minEva);
            }
            return minEva;
        }
    }

    // This method creates the branches that correspond to the players' possible moves and creates the tree
    public void createMySubtree(int currentPosition, int opponentCurrentPosition, Node root, int depth){
        if(depth > MAX_DEPTH) return; // terminal condition
        ArrayList<Integer> validMoves = findValidMoves(currentPosition); // get the possible moves
        //System.out.println("Player's moves list is  "+validMoves.toString());

        for (int move : validMoves) {
            // Evaluate the move
            double evaluationResult;
            // Check whether the payer that is currently playing is Theseus or Minotaur so that we call the right evaluate method
            if(name.equals("Theseus")) evaluationResult = evaluateTheseus(currentPosition, move, opponentCurrentPosition);
            else evaluationResult = evaluateMinotaur(currentPosition, move, opponentCurrentPosition);
            // Declare the new node
            Node child = new Node();
            child.setNodeEvaluation(evaluationResult);
            child.setNodeDepth(depth);
            child.setMove(move);
            // Declare a new variable to change the position, because the new position is just for simulation. We do not
            // want to mess with the currentPosition because it will change the data of the game and the following for loops will
            // take a wrong position as the current.
            int passPosition = currentPosition;
            if (move == 1) passPosition += board.getN();
            else if (move == 3) passPosition++;
            else if (move == 5) passPosition -= board.getN();
            else passPosition--;
            // Call the opponents' method to create his branches and nodes.
            createOpponentSubtree(passPosition, opponentCurrentPosition, child, depth + 1, evaluationResult);
            // Finally, after the child is completed we just connect to its' parent node.
            root.connectChild(child);
        }
    }

    // The method below creates the branches and nodes that are considered as opponent turn
    public void createOpponentSubtree(int currentPosition, int opponentCurrentPosition, Node root, int depth, double parentEval){
        if(depth > MAX_DEPTH) return; // terminal condition, if the max depth has been constructed stop the tree
        ArrayList<Integer> validMoves = findValidMoves(opponentCurrentPosition); // get the valid moves dynamic array
        //System.out.println("Opponents moves list is  "+validMoves.toString());

        for (int move : validMoves) {
            double evaluationResult;
            // Now check if who is the player that is currently playing (the one who maximizes) so that we know which evaluate method to call
            if(name.equals("Theseus")) evaluationResult = evaluateMinotaur(opponentCurrentPosition, move, currentPosition);
            else evaluationResult = evaluateTheseus(opponentCurrentPosition, move, currentPosition);
            // Declare the child
            Node child = new Node();
            child.setNodeEvaluation(evaluationResult);
            child.setNodeDepth(depth);
            child.setMove(move);
            // Make a new variable of the next move to be simulated because if we change currentPosition, we destroy the following for loops
            int passPosition = opponentCurrentPosition;
            if (move == 1) passPosition += board.getN();
            else if (move == 3) passPosition++;
            else if (move == 5) passPosition -= board.getN();
            else passPosition--;
            // Call the other method to create the opponents branches and nodes
            createMySubtree(currentPosition, passPosition, child, depth+1);
            // Finally, after completing the child's subtree, we connect it to the parent node
            root.connectChild(child);
        }
    }

    // Evaluate methods for theseus and minotaur separately
    public double evaluateTheseus(int currentPos, int possibleMove, int opponentCurrentPos){
        allDistances = tilesNearSupplyOrOpponent(currentPos, opponentCurrentPos);
        HashMap<Integer, Double> pointsGained = new HashMap<>(); // hold the points that will be gained at each direction

        for(Map.Entry<Integer, Integer> entry : allDistances.entrySet()){
            //////////
            if(entry.getKey() == 1 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 1 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 1 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 1 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
                //////////
            else if(entry.getKey() == 3 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 3 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 3 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 3 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
                //////////
            else if(entry.getKey() == 5 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 5 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 5 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 5 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
                //////////
            else if(entry.getKey() == 7 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 7 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 7 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 7 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
        }

        //System.out.println(pointsGained.toString());
        // Now find the points lost by the opponent existence
        double pointsOpponentUp = 0;
        double pointsOpponentRight = 0;
        double pointsOpponentDown = 0;
        double pointsOpponentLeft = 0;

        if(allDistances.get(0) == 1) pointsOpponentUp = -1.0;
        else if(allDistances.get(0) == 2) pointsOpponentUp = -0.6;
        else if(allDistances.get(0) == 3) pointsOpponentUp = -0.3;
            //////
        else if(allDistances.get(2) == 1) pointsOpponentRight = -1.0;
        else if(allDistances.get(2) == 2) pointsOpponentRight = -0.5;
        else if(allDistances.get(2) == 3) pointsOpponentRight = -0.3;
            /////
        else if(allDistances.get(4) == 1) pointsOpponentDown = -1.0;
        else if(allDistances.get(4) == 2) pointsOpponentDown = -0.5;
        else if(allDistances.get(4) == 3) pointsOpponentDown = -0.3;
            /////
        else if(allDistances.get(6) == 1) pointsOpponentLeft = -1.0;
        else if(allDistances.get(6) == 2) pointsOpponentLeft = -0.5;
        else if(allDistances.get(6) == 3) pointsOpponentLeft = -0.3;

        // Now based on the possibleMove, fetch the data and use it in the formula
        // and return the appropriate values
        if(possibleMove == 1)
            return pointsGained.get(possibleMove)*0.46 + pointsOpponentUp*0.56;
            /////
        else if(possibleMove == 3)
            return pointsGained.get(possibleMove)*0.46 + pointsOpponentRight*0.56;
            /////
        else if(possibleMove == 5)
            return pointsGained.get(possibleMove)*0.46 + pointsOpponentDown*0.56;
            /////
        else if(possibleMove == 7)
            return pointsGained.get(possibleMove)*0.46 + pointsOpponentLeft*0.56;
        else{
            System.exit(1);
            return 0;
        }
    }

    public double evaluateMinotaur(int currentPos, int possibleMove, int opponentCurrentPos) {
        allDistances = tilesNearSupplyOrOpponent(currentPos, opponentCurrentPos);

        // Declare a HashMap that holds the points that minotaur will gain if he moves in a certain direction.
        HashMap<Integer, Double> pointsGained = new HashMap<>();

        // Iterate through allDistances so that we make the appropriate additions to the HashMap
        for(Map.Entry<Integer, Integer> entry : allDistances.entrySet()){
            // First check for the up direction, then for right, down, left
            //////////
            if(entry.getKey() == 1 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 1 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 1 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 1 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
                //////////
            else if(entry.getKey() == 3 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 3 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 3 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 3 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
                //////////
            else if(entry.getKey() == 5 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 5 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 5 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 5 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
                //////////
            else if(entry.getKey() == 7 && entry.getValue() == 0) pointsGained.put(entry.getKey(), 0.0);
            else if(entry.getKey() == 7 && entry.getValue() == 3) pointsGained.put(entry.getKey(), 0.3);
            else if(entry.getKey() == 7 && entry.getValue() == 2) pointsGained.put(entry.getKey(), 0.5);
            else if(entry.getKey() == 7 && entry.getValue() == 1) pointsGained.put(entry.getKey(), 1.0);
        }

        //System.out.println(pointsGained.toString());
        // Now check for Theseus existence. If Theseus is visible for minotaur, then he immediately
        // goes to that direction. Else he moves in Theseus standards.
        boolean theseusIsVisibleUp = false;
        boolean theseusIsVisibleRight = false;
        boolean theseusIsVisibleDown = false;
        boolean theseusIsVisibleLeft = false;

        if(allDistances.get(0) > 0) theseusIsVisibleUp = true;
        else if(allDistances.get(2) > 0) theseusIsVisibleRight = true;
        else if(allDistances.get(4) > 0) theseusIsVisibleDown = true;
        else if(allDistances.get(6) > 0) theseusIsVisibleLeft = true;

        if(theseusIsVisibleUp && possibleMove == 1) return 10000; // 10000 is pretty big value that means that this move must be selected in getNextMove
        else if(theseusIsVisibleRight && possibleMove == 3) return 10000;
        else if(theseusIsVisibleDown && possibleMove == 5) return 10000;
        else if(theseusIsVisibleLeft && possibleMove == 7) return 10000;
        else if(possibleMove == 1) return pointsGained.get(possibleMove);
        else if(possibleMove == 3) return pointsGained.get(possibleMove);
        else if(possibleMove == 5) return pointsGained.get(possibleMove);
        else if(possibleMove == 7) return pointsGained.get(possibleMove);
        else{
            System.exit(1);
            return -100;
        }
    }

    // Update the path field
    public void updatePath(int selectedMove){
        Integer[] moreInfo = new Integer[4];

        // If the selected move is illegal, terminate the program
        if(selectedMove < 0){
            System.out.println("\n\n\n\n\n\n\nSomething went wrong in updatePath. Program terminated :(   "+ selectedMove );
            System.exit(1);
        }

        // Store the move
        moreInfo[0] = selectedMove;

        // Now check if a supply was collected in the current tile
        // We remind that round starts from zero so it's equal to the path size-1
        moreInfo[1] = 0;
        for(Supply sup : board.getSupplies()){
            if(sup.getCollectedInRound() == path.size() - 1) {
                moreInfo[1] = 1;
                break;
            }
        }

        // Store the distance from a supply at a certain direction
        moreInfo[2] = distancesMap.get(selectedMove);

        // The same but for opponent
        moreInfo[3] = distancesMap.get(selectedMove-1); // cause the orientations for the opponent have different encoding

        // Lastly just update the path List
        path.add(moreInfo);
    }

    // Prints some statistics
    public void statistics(){
        String str1 = "-----------------------";
        String str2 = "-----------------";
        String stats = " GAME STATISTICS ";

        // Declare the variables that will be used to keep track of the moves Theseus played
        int timesWentUp = 0;
        int timesWentRight = 0;
        int timesWentDown = 0;
        int timesWentLeft = 0;
        int round = 0;
        int suppliesCollected = 0;

        // Firstly print the statistics for each round
        for(Integer[] search : path){
            System.out.println();
            // The index of the path is the same with the round, as we have round start from zero. So we need to increase
            // it by 1.
            round = path.indexOf(search) + 1;
            System.out.println("------- ROUND " + round + " STATISTICS -----------------------");
            for(Supply sup : board.getSupplies()){
                // The index of the path's elements is equal to the round
                if(sup.getCollectedInRound() == path.indexOf(search)) suppliesCollected++;
            }
            System.out.println("*Theseus has collected " + suppliesCollected + " at total!");
            // Print the distance from the supply in this round, if it exists
            if(search[2] > 0) System.out.println("*Distance from the nearest supply: " + search[2]);
            else System.out.println("*No supplies found near");
            // Same but for opponent
            if(search[3] > 0) System.out.println("*Distance from the opponent: " + search[3]);
            else System.out.println("*No opponent found near");
            // Now increase the variables that will be printed in the end
            if(search[0] == 1) timesWentUp++;
            else if(search[0] == 3) timesWentRight++;
            else if(search[0] == 5) timesWentDown++;
            else if(search[0] == 7) timesWentLeft++;
        }

        // Now print the final stats
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println(str1 + str2 + str1);
        System.out.println(str1 + stats + str1);
        System.out.println(str1 + str2 + str1);
        System.out.println();
        System.out.println("Times went up: " + timesWentUp);
        System.out.println("Times went right: " + timesWentRight);
        System.out.println("Times went down: " + timesWentDown);
        System.out.println("Times went left: " + timesWentLeft);
        System.out.println();
    }
}
