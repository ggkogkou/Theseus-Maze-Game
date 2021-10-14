import java.util.*;

public class HeuristicPlayer extends Player{
    /////////////////////////////////////////////
    ///////// VARIABLES
    ////////////////////////////////////////////

    public int[] count = new int[4];
    protected ArrayList<Integer[]> path; // ArrayList to keep track of the movements
    protected List<Integer> followCertainPath;
    protected TreeMap<Integer, Integer> allDistances; // This map will hold all the distances at every direction
    protected HashMap<Supply, Integer> suppliesSawBefore; // this map holds the supplies that theseus has seen
    // and deletes them once he collects them
    protected int opponentTileId;
    private boolean theseusSearchingSupplySawBeforeMode; // this variable is true when theseus tries to find again
    // a supply that he saw before but didn't collect then

    //////////////////////////////////////////
    ///////// CONSTRUCTORS
    /////////////////////////////////////////

    public HeuristicPlayer(Board board, int playerId, String name, int score, int x, int y, int opponentTileId){
        // First call the constructor of the derived class
        super(playerId, name, board, score, x, y);
        // Initialize an ArrayList with initial capacity of 100
        path = new ArrayList<>(100);
        // Initialize the rest of the variables
        allDistances = new TreeMap<>();
        suppliesSawBefore = new HashMap<>();
        this.opponentTileId = opponentTileId;
        followCertainPath = new ArrayList<>();
    }

    ///////////////////////////////////////////////////////
    ////////////// OTHER METHODS
    //////////////////////////////////////////////////////

    // The method below finds all the possible moves and then evaluates them, selects the best move
    // to be made and updates the path ArrayList field through updatePath method
    // Also mention that it overrides the diceRoll from the super class Player
    @Override
    public int getNextMove(int currentPos, int currentOpponentPos){
        // Update opponent id field
        opponentTileId = currentOpponentPos;

        // Get the results from tilesNearSupplyOrOpponent in a TreeMap collection.
        // Now the field is updated so the evaluate method will use it correctly.
        // As tilesNearSupplyOrOpponent is called at this point at every round, the method
        // that updates the field suppliesTheseusSaw is also updated. So the map is always updated
        // in every round.
        allDistances.clear();
        allDistances = tilesNearSupplyOrOpponent();

        // Check if the List of the specific path that Theseus follows is empty and if it is
        // change the theseusSearchingSupplySawBeforeMode field
        if(suppliesSawBefore.isEmpty() || followCertainPath.isEmpty()) theseusSearchingSupplySawBeforeMode = false;
        System.out.println(theseusSearchingSupplySawBeforeMode);

        System.out.println("Supplies in HashSet are : "+suppliesSawBefore.toString());

        // Declare a HashMap to store the moves with their evaluation in a key-value form
        TreeMap<Integer, Double> possibleMovesEvaluation = new TreeMap<>();
        // Declare an ArrayList to store the valid moves that can be played
        ArrayList<Integer> possibleMoves = findValidMoves(currentPos);

        // Now we must evaluate them one by one, so we must iterate through the ArrayList.
        // The enhanced for-loop is easier way to do the job.
        List<Double> tempList = new ArrayList<>(); // Will be used to sort the evaluation values
        double evaluationResult;
        for (Integer possibleMove : possibleMoves){
            evaluationResult = evaluate(possibleMove);
            // Add the result to the ArrayList
            tempList.add(evaluationResult);
            // Now put in the HashMap in key-value form the move with its evaluation value
            possibleMovesEvaluation.put(possibleMove, evaluationResult);
        }

        // Now we have to choose the move with the highest evaluation.
        // Note that there is a chance of two moves to have the same evaluation or all moves have
        // evaluation = 0. These cases will be treated properly.

        // We need to iterate the Arraylist to find the highest evaluation value
        Collections.sort(tempList);
        for(Double d : tempList) System.out.printf("%f ", d);
        System.out.println();
        // Now the list is sorted in ascending order

        // Firstly we must consider checking if all the values of the evaluations are zero
        // meaning that all moves are equivalent.
        boolean allEvaluationValuesEqualZero = false; // if all the values in the list are zero it becomes true
        int counter = 0;
        for(Double x : tempList){
            if(x == 0) counter++;
        }
        if(counter == tempList.size()) allEvaluationValuesEqualZero = true;

        // Now we check the various cases
        // Firstly the case that there is a maximum value.
        if(!allEvaluationValuesEqualZero){
            count[0]++;
            System.out.println("CASE 1 =============================");
            // If there are more than one highest values equal and !=0 then we choose randomly
            // which one we use
            // Now remove the elements of the tempList that has the minimum evaluation value
            // To remove elements we have to begin looping from the upper indexes as when an object
            // is removed, the ArrayList indexes change
            for(int i=tempList.size()-2; i>=0; i--){
                if(tempList.get(i) < tempList.get(tempList.size()-1)) tempList.remove(i);
            }
            System.out.println("tempList has values" + tempList.toString());
            Random rand = new Random();
            int index = rand.nextInt(tempList.size());
            double highestEvaluationValue = tempList.get(index);
            System.out.println("Highest evaluation value is: " + highestEvaluationValue);

            // Now we check in the HashMap which move corresponds to the evaluation value chosen
            // by iterating the HashMap
            int selectedMove = -1; // this variable will store the next move
            for(Map.Entry<Integer, Double> entry : possibleMovesEvaluation.entrySet()){
                // If there are two or more equal highest values, the choice is random
                if(entry.getValue() == highestEvaluationValue)
                    selectedMove = entry.getKey();
            }

            // Check if Theseus was already looking for a supply and if so store the extra move that he probably
            // will make differently than the followingCertainPath. If the move he will play doesn't correspond to the
            // followingCertainPath next move, this "extra" move will be stored in the List, so it can be
            // corrected later. Else, if the two moves are the same, we just remove it from the followingCertainPath.
            if(theseusSearchingSupplySawBeforeMode && selectedMove != followCertainPath.get(0)){
                int extraMove = 0;
                if(selectedMove == 1) extraMove = 5;
                else if(selectedMove == 3) extraMove = 7;
                else if(selectedMove == 5) extraMove = 1;
                else if(selectedMove == 7) extraMove = 3;
                else System.exit(1);
                followCertainPath.add(0, extraMove);
            }
            else if(theseusSearchingSupplySawBeforeMode) followCertainPath.remove(0);

            // Finally, we need to update the path and return the move.
            updatePath(selectedMove);
            return selectedMove;
        }
        else if(theseusSearchingSupplySawBeforeMode){
            System.out.println("CASE 2 =============================");
            count[1]++;
            // If at some point the next else if run, then in this else if we just make sure
            // that Theseus follows the path.
            int selectedMove = followCertainPath.get(0); // select the move
            followCertainPath.remove(0); // remove it
            updatePath(selectedMove); // update path
            return selectedMove; // return
        }
        else if(!suppliesSawBefore.isEmpty()){
            count[2]++;
            System.out.println("CASE 3 =============================");
            // Switch to true the boolean type field
            theseusSearchingSupplySawBeforeMode = true;
            // If every direction is empty of supplies and opponents try to remember where theseus
            // saw a supply but wasn't able to collect it at that time.
            Set<Map.Entry<Supply, Integer>> entrySet = suppliesSawBefore.entrySet();
            List<Integer> aList = new ArrayList<>();
            // Find the latest round that Theseus saw a supply
            for(Map.Entry<Supply, Integer> entry : entrySet) aList.add(entry.getValue());
            Collections.sort(aList);

            // Now the list with the rounds is in ascending order. Just peak the last value.
            int lastRoundTheseusSawASupply = aList.get(aList.size()-1);

            // Find in which key does this value belong to
            Supply key = new Supply();
            for(Map.Entry<Supply, Integer> entry : entrySet){
                if(entry.getValue() == lastRoundTheseusSawASupply){
                    key = entry.getKey();
                }
            }

            // Now go to search for this supply by following the reverse moves until we reach it.
            // Declare a list ot hold the path that Theseus will follow.
            // The path index is equal to the rounds that have been played-1. Because we have Game.round
            // starting from 0, we can use them as equals.
            for(int i=path.size()-1; i>=suppliesSawBefore.get(key); i--){
                if(path.get(i)[0] == 1) followCertainPath.add(5);
                else if(path.get(i)[0] == 3) followCertainPath.add(7);
                else if(path.get(i)[0] == 5) followCertainPath.add(1);
                else if(path.get(i)[0] == 7) followCertainPath.add(3);
                else System.exit(1);
            }

            // Now this list has the order that theseus need to follow so that he reaches the supply
            int selectedMove = followCertainPath.get(0); // select the move
            followCertainPath.remove(0); // remove it
            updatePath(selectedMove); // update path
            return selectedMove; // return
        }
        else{
            count[3]++;
            System.out.println("CASE 4 =============================");
            // Instead of letting Theseus move randomly, we choose to just make hom move to a direction
            // different than this he came from.
            Random rand = new Random();
            int previousMove;
            int selectedMove;
            if(path.size() == 0){
                selectedMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
                updatePath(selectedMove);
                return selectedMove;
            }

            previousMove = path.get(path.size()-1)[0];
            int sameAsCame = -1;
            if(previousMove == 1) sameAsCame = 5;
            else if(previousMove == 3) sameAsCame = 7;
            else if(previousMove == 5) sameAsCame = 1;
            else if(previousMove == 7) sameAsCame = 3;

            // Treat the case of tile id 0 and only one way out
            if(currentPos == 0 && possibleMoves.size() == 1){
                selectedMove = possibleMoves.get(0);
                updatePath(selectedMove);
                return selectedMove;
            }

            do{
                selectedMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
            } while(selectedMove == sameAsCame);

            updatePath(selectedMove);
            return selectedMove;
        }
    }

    // The next method evaluates a possible move through a formula and returns a double.
    // The parameter currentPosition given isn't used because the id og the tile is easily calculated
    // by the x, y coordinates of the player, given in the derived class Player.
    public double evaluate(int possibleMove){
        HashMap<Integer, Double> pointsGained = new HashMap<>(5); // hold the points that will be gained at each direction

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

    // The next method just prints out some statistics
    public void statistics(){
        String str1 = "-----------------------";
        String str2 = "-----------------";
        String stats = " GAME STATISTICS ";

        // Declare the variables that will be used to keep track of the moves Theseus played
        int timesWentUp = 0;
        int timesWentRight = 0;
        int timesWentDown = 0;
        int timesWentLeft = 0;
        int round;
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

    // The following method updates the path field with the appropriate data
    // [selected move, supply collectd or not, distance from supply, distance from opponent,
    // theseusSearchingSupplySawBefore 1 or 0]
    public void updatePath(int selectedMove){
        Integer[] moreInfo = new Integer[5];

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
        moreInfo[2] = allDistances.get(selectedMove);

        // The same but for opponent
        moreInfo[3] = allDistances.get(selectedMove-1); // cause the orientations for the opponent have different encoding

        // Update the last position of the array
        moreInfo[4] = theseusSearchingSupplySawBeforeMode ? 1 : 0;

        // Lastly just update the path List
        path.add(moreInfo);
    }

    // This method takes the ArrayList with the tiles that are visible as a parameter
    // and then searches for supplies. It returns the distance of the supplies in each way.
    // If there are more than one supplies in a certain direction then the encoding of the keys in the map
    // changes a bit. For example if in the up direction there are two supplies then the one closer will be
    // stored with key=1 and the other with key=11. So, as many supplies exist in a direction, that many digits of the
    // direction the key has.
    protected TreeMap<Integer, Integer> tilesNearSupplyOrOpponent(){
        // Declare the TreeMap to be returned
        TreeMap<Integer, Integer> sortedMap = new TreeMap<>();

        // Firstly store the results of the functions that find the visible tiles i new Lists
        ArrayList<Tile> upList = tilesVisibleUpwards(x* board.getN()+y);
        ArrayList<Tile> rightList = tilesVisibleRightwards(x* board.getN()+y);
        ArrayList<Tile> downList = tilesVisibleDownwards(x* board.getN()+y);
        ArrayList<Tile> leftList = tilesVisibleLeftwards(x* board.getN()+y);

        // First check if the Lists are empty, meaning that there is a wall in the tile
        if(upList.isEmpty()) sortedMap.put(1, 0);
        if(rightList.isEmpty()) sortedMap.put(3, 0);
        if(downList.isEmpty()) sortedMap.put(5, 0);
        if(leftList.isEmpty()) sortedMap.put(7, 0);

        // Declare variables to hold the coordinates of the opponent
        int xOpponent = -1, yOpponent = -1; // coordinates of opponent

        // Iterate through the ArrayLists and find the supplies all directions.
        // The supplies that are found we store them in the map
        ArrayList<Supply> suppliesFound = new ArrayList<>();

        // Lastly in each case search for opponent. This will be stored in tha map with the key: 0.
        // An opponent can be found only in one direction. For storage reasons, for the opponent
        // we have the following encoding. { 0: up, 2: right, 4: down, 6: left }

        // First for the up direction
        for(Tile tile : upList){
            // Use the method tileHasSupply() that is added in the Board class
            // to know if the specific tile has a supply
            if(board.tileHasSupply(tile.getTile_id())) suppliesFound.add(board.tileWithSupply(tile.getTile_id()));
            // Check about opponent
            if(tile.getTile_id() == opponentTileId){
                xOpponent = tile.getX();
                yOpponent = tile.getY();
            }
        }
        // If there is no supply found distance is considered 0
        // As the for-loop above is iterating ArrayList and the the tiles that
        // are closer to the current tile are first in the ArrayList. So the ArrayList of the supplies
        // has stored the supplies closer in the tile in its first position. So in the first position there is the closest supply
        if(suppliesFound.isEmpty()) sortedMap.put(1, 0);
        else if(suppliesFound.size() == 1) sortedMap.put(1, suppliesFound.get(0).getX() - x);
        else if(suppliesFound.size() == 2){
            sortedMap.put(1, suppliesFound.get(0).getX() - x);
            sortedMap.put(11, suppliesFound.get(1).getX() - x);
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(1, suppliesFound.get(0).getX() - x);
            sortedMap.put(11, suppliesFound.get(1).getX() - x);
            sortedMap.put(111, suppliesFound.get(2).getX() - x);
        }
        this.updateSuppliesSawBefore(suppliesFound); // Update the map suppliesTheseusSaw by calling the method
        suppliesFound.clear();
        if(xOpponent == -1 || yOpponent == -1) sortedMap.put(0, 0);
        else sortedMap.put(0, xOpponent - x);

        // The same for the other directions
        // Right
        xOpponent = -1;
        yOpponent = -1;
        for(Tile tile : rightList){
            // Use the method tileHasSupply() that is added in the Board class
            // to know if the specific tile has a supply
            if(board.tileHasSupply(tile.getTile_id())) suppliesFound.add(board.tileWithSupply(tile.getTile_id()));
            // Check about opponent
            if(tile.getTile_id() == opponentTileId){
                xOpponent = tile.getX();
                yOpponent = tile.getY();
            }
        }
        if(suppliesFound.isEmpty()) sortedMap.put(3, 0);
        else if(suppliesFound.size() == 1) sortedMap.put(3, suppliesFound.get(0).getY() - y);
        else if(suppliesFound.size() == 2){
            sortedMap.put(3, suppliesFound.get(0).getY() - y);
            sortedMap.put(33, suppliesFound.get(1).getY() - y);
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(3, suppliesFound.get(0).getY() - y);
            sortedMap.put(33, suppliesFound.get(1).getY() - y);
            sortedMap.put(333, suppliesFound.get(2).getY() - y);
        }
        this.updateSuppliesSawBefore(suppliesFound); // Update the map suppliesTheseusSaw  by calling the method
        suppliesFound.clear();
        if(xOpponent == -1 || yOpponent == -1) sortedMap.put(2, 0);
        else sortedMap.put(2, yOpponent - y);

        // Down
        xOpponent = -1;
        yOpponent = -1;
        for(Tile tile : downList){
            // Use the method tileHasSupply() that is added in the Board class
            // to know if the specific tile has a supply
            if(board.tileHasSupply(tile.getTile_id())) suppliesFound.add(board.tileWithSupply(tile.getTile_id()));
            // Check about opponent
            if(tile.getTile_id() == opponentTileId){
                xOpponent = tile.getX();
                yOpponent = tile.getY();
            }
        }
        if(suppliesFound.isEmpty()) sortedMap.put(5, 0);
        else if(suppliesFound.size() == 1) sortedMap.put(5, x - suppliesFound.get(0).getX());
        else if(suppliesFound.size() == 2){
            sortedMap.put(5, x - suppliesFound.get(0).getX());
            sortedMap.put(55, x - suppliesFound.get(1).getX());
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(5, x - suppliesFound.get(0).getX());
            sortedMap.put(55, x - suppliesFound.get(1).getX());
            sortedMap.put(555, x - suppliesFound.get(2).getX());
        }
        this.updateSuppliesSawBefore(suppliesFound); // Update the map suppliesTheseusSaw  by calling the method
        suppliesFound.clear();
        if(xOpponent == -1 || yOpponent == -1) sortedMap.put(4, 0);
        else sortedMap.put(4, x - xOpponent);

        // Left
        xOpponent = -1;
        yOpponent = -1;
        for(Tile tile : leftList){
            // Use the method tileHasSupply() that is added in the Board class
            // to know if the specific tile has a supply
            if(board.tileHasSupply(tile.getTile_id())) suppliesFound.add(board.tileWithSupply(tile.getTile_id()));
            // Check about opponent too
            if(tile.getTile_id() == opponentTileId){
                xOpponent = tile.getX();
                yOpponent = tile.getY();
            }
        }
        if(suppliesFound.isEmpty()) sortedMap.put(7, 0);
        else if(suppliesFound.size() == 1) sortedMap.put(7, y - suppliesFound.get(0).getY());
        else if(suppliesFound.size() == 2){
            sortedMap.put(7, y - suppliesFound.get(0).getY());
            sortedMap.put(77, y - suppliesFound.get(1).getY());
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(7, y - suppliesFound.get(0).getY());
            sortedMap.put(77, y - suppliesFound.get(1).getY());
            sortedMap.put(777, y - suppliesFound.get(2).getY());
        }
        this.updateSuppliesSawBefore(suppliesFound); // Update the map suppliesTheseusSaw  by calling the method
        suppliesFound.clear();
        if(xOpponent == -1 || yOpponent == -1) sortedMap.put(6, 0);
        else sortedMap.put(6, y - yOpponent);

        System.out.println(sortedMap.toString());

        // Only left to return the treemap
        return sortedMap;
    }

    // The next method updates the map that holds the supplies that theseus have seen at some point
    // of the game but not collected yet
    protected void updateSuppliesSawBefore(ArrayList<Supply> suppliesFound){
        for(Supply sup : suppliesFound){
            if(!suppliesSawBefore.containsKey(sup)) suppliesSawBefore.put(sup, Game.currentRound);
        }
        // Find if a supply has been collected so that we remove it from the map
        for(Supply sup : board.getSupplies()){
            if(!sup.isActivated()) suppliesSawBefore.remove(sup);
        }
    }
}
