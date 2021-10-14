import java.util.*;

public class HeuristicMinotaur extends HeuristicPlayer {

    // The suppliesSawBefore here is the map with the supplies that minotaur has found
    private HashMap<Supply, Integer> suppliesCurrentlyVisible; // map with the supplies currently visible.
    // needs to be updated at every round in updateSuppliesSawBefore
    private int roundsWithoutFindAnySupplies;

    public HeuristicMinotaur(Board board, int playerId, String name, int score, int x, int y, int opponentTileId){
        // Initialize the HeuristicPlayer first
        super(board, playerId, name, score, x, y, opponentTileId);
        suppliesCurrentlyVisible = new HashMap<>();
    }

    // In this method we try to find the best minotaur next move between {1, 3, 5, 7} and return it
    // so he can move to that direction.
    @Override
    public int getNextMove(int currentPos, int currentOpponentPos){
        opponentTileId = currentOpponentPos;
        // Firstly, just as we did for Theseus in the derived class, we need to find all the
        // distances from the supplies or the opponent if they exist around of course.
        // If something doesn't exist in a direction it takes the value zero.
        allDistances.clear();
        allDistances = tilesNearSupplyOrOpponent();

        // Find the rounds that minotaur has not seen any supplies
        if(!suppliesSawBefore.isEmpty()) roundsWithoutFindAnySupplies = 0; // make it zero when a supply is visible
        if(suppliesSawBefore.size() > 0 && suppliesCurrentlyVisible.isEmpty()) roundsWithoutFindAnySupplies++;

        // Now that we have these distances stored in a TreeMap we can proceed on finding the evaluation values.
        // We know that minotaur wants to spend more time around the supplies, as he knows that he has better chances
        // to kill him in that way. The only difference is that minotaur doesn't care to collect the supplies, but he only
        // cares to know where they are. Also, we need to mention that in this program we assume that minotaur knows the
        // maze that he lives in, but doesn't know where the supplies are.

        // Our first job here is to evaluate all the possible moves one by one, using the evaluate() method.
        // Get the possible moves from the grandparent Player method findValidMoves()
        ArrayList<Integer> possibleMoves = findValidMoves(currentPos);
        // Now evaluate and store the evaluation result as a key in a TreeMap with values corresponding to the moves encoding
        TreeMap<Double, Integer> possibleMovesEvaluation = new TreeMap<>();
        for (Integer possibleMove : possibleMoves){
            possibleMovesEvaluation.put(evaluate(possibleMove), possibleMove);
        }

        // Now the evaluation values are stored in the sorted map in ascending order. Warning: in the tree map the keys
        // are always unique. This means that if two directions have the same evaluation result, the one that stays stored
        // in the map is the last one of them in the order that they were examined in the previous enhanced for loop.
        // So, if we have two or more directions with the same evaluation result, minotaur chooses to go to the direction
        // of the key-evaluation that is stored in the map because as we mentioned before, minotaur doesn't really care
        // to which direction he will go if he finds supplies in two or more different directions.

        // As we did with Theseus in the HeuristicPlayer class, we need to determine if all the evaluation values are zero
        // or there is a maximum one to tell us in which direction Theseus will go. This is easy to check. Just find out if
        // the map has only one key, meaning that it replaced all the previous keys that where
        boolean allEvaluationsAreZero = false;
        if(possibleMovesEvaluation.size() == 1 && possibleMovesEvaluation.containsKey(0.0)) allEvaluationsAreZero = true;
        System.out.println("Minotaur TreeMap is: " + possibleMovesEvaluation.toString());

        // Now consider all the possible cases like in HeuristicPlayer
        if(!allEvaluationsAreZero){
            Random rand = new Random();
            int selectedMove;
            int previousMove;
            double higherEvaluation;

            // If minotaur sees supply in the first round:
            if(path.size() == 0){
                higherEvaluation = possibleMovesEvaluation.lastKey();
                selectedMove = possibleMovesEvaluation.get(higherEvaluation);
                updatePath(selectedMove);
                return selectedMove;
            }
            // If minotaur goes in the tile with id 0 and has only one way to move:
            if(currentPos == 0 && possibleMoves.size() == 1){
                selectedMove = possibleMoves.get(0);
                updatePath(selectedMove);
                return selectedMove;
            }
            // After checking the above special cases, let's proceed with selecting the move of minotaur.
            previousMove = path.get(path.size()-1)[0];
            int sameAsCame = -1;
            if(previousMove == 1) sameAsCame = 5;
            else if(previousMove == 3) sameAsCame = 7;
            else if(previousMove == 5) sameAsCame = 1;
            else if(previousMove == 7) sameAsCame = 3;
            else System.exit(1);

            higherEvaluation = possibleMovesEvaluation.lastKey();
            selectedMove = possibleMovesEvaluation.get(higherEvaluation);

            while(selectedMove == sameAsCame) selectedMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));

            // If minotaur has a specific path that follows then we need to add the extra move that may have done
            // or remove it if it the same as expected
            if(!followCertainPath.isEmpty() && selectedMove != followCertainPath.get(0)){
                int extraMove = 0;
                if(selectedMove == 1) extraMove = 5;
                else if(selectedMove == 3) extraMove = 7;
                else if(selectedMove == 5) extraMove = 1;
                else if(selectedMove == 7) extraMove = 3;
                else System.exit(1);
                followCertainPath.add(0, extraMove);
            }
            else if(!followCertainPath.isEmpty() && followCertainPath.get(0) == selectedMove) followCertainPath.remove(0);

            updatePath(selectedMove);
            return selectedMove;
        }
        else if(!followCertainPath.isEmpty()){
            int selectedMove = followCertainPath.get(0);
            followCertainPath.remove(0);
            updatePath(selectedMove);
            return selectedMove;
        }
        else if(roundsWithoutFindAnySupplies > 15){
            // Minotaur remembers the supplies that he saw before and tries to follow the inverse path and roam
            // towards the supplies that detected earlier in the game.
            // Figure out this path in the followingCertainPath List.
            ArrayList<Integer> tempList = new ArrayList<>(); // temp list to sort the rounds in ascending order
            for(Map.Entry<Supply, Integer> entry : suppliesSawBefore.entrySet()) tempList.add(entry.getValue());
            Collections.sort(tempList);

            // First find the path towards the latest supply saw
            int round = tempList.get(0);
            for(int i=path.size()-1; i>=round; i--){
                if(path.get(i)[0] == 1) followCertainPath.add(5);
                else if(path.get(i)[0] == 3) followCertainPath.add(7);
                else if(path.get(i)[0] == 5) followCertainPath.add(1);
                else if(path.get(i)[0] == 7) followCertainPath.add(3);
            }
            // Now we have the exact steps so that minotaur follow the inverse path
            int selectedMove = followCertainPath.get(0);
            followCertainPath.remove(0);
            roundsWithoutFindAnySupplies = 0;
            updatePath(selectedMove);
            return selectedMove;
        }
        else{
            // Instead of letting Theseus move randomly, we choose to just make him move to a direction
            // different than the one he came from.
            Random rand = new Random();
            int previousMove;
            int selectedMove;
            if(path.size() == 0){
                selectedMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
                updatePath(selectedMove);
                return selectedMove;
            }
            // If minotaur goes in the tile with id 0 and has only one way to move:
            if(currentPos == 0 && possibleMoves.size() == 1){
                selectedMove = possibleMoves.get(0);
                updatePath(selectedMove);
                return selectedMove;
            }

            previousMove = path.get(path.size()-1)[0];
            int sameAsCame = -1;
            if(previousMove == 1) sameAsCame = 5;
            else if(previousMove == 3) sameAsCame = 7;
            else if(previousMove == 5) sameAsCame = 1;
            else if(previousMove == 7) sameAsCame = 3;

            do{
                selectedMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
            } while(selectedMove == sameAsCame);

            updatePath(selectedMove);
            return selectedMove;
        }
    }

    // In the next method we try to evaluate all the possible moves and then return these evaluation values
    // that are calculated and return in the getNextMove() method to compare these values. We mention that in minotaurs
    // case the formula doesn't make sense as minotaur always has to go to Theseus direction if he sees him. In contrast with
    // the evaluate method of Theseus, minotaur doesn't care for collecting the supplies but only knowing where they are.
    // So, we need to change the evaluate() method so that minotaur doesn't lose points when he sees the opponent (Theseus)
    // but earn more. As minotaur's existence is so that he tries to kill Theseus, when he see him, he must always go to his
    // direction. Also minotaur doesn't really care about which supply is closer as he has the same theoretical possibilities to find Theseus
    // in any of these directions.
    @Override
    public double evaluate(int possibleMove){
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

    // In the following method we update the path field of minotaur as we did for Theseus. The only difference is that
    // minotaur doesn't collect supplies so we have Integer arrays of size three in the path. These arrays contain information
    // for: [selected move, distance from supply, distance from opponent]
    @Override
    public void updatePath(int selectedMove){
        Integer[] moreInfo = new Integer[3];
        // If the selected move is illegal, terminate the program
        if(selectedMove < 0){
            System.out.println("\n\n\n\n\n\n\nSomething went wrong in updatePath. Program terminated :(   "+ selectedMove );
            System.exit(1);
        }
        // Store the move
        moreInfo[0] = selectedMove;
        // Store the distance from a supply at a certain direction
        moreInfo[1] = allDistances.get(selectedMove);
        // The same but for opponent
        moreInfo[2] = allDistances.get(selectedMove-1); // cause the orientations for the opponent have different encoding
        // Lastly just update the path List
        path.add(moreInfo);
    }

    // In the method below we update the suppliesSawBefore field that we derived from the parent class.
    @Override
    protected void updateSuppliesSawBefore(ArrayList<Supply> suppliesFound){
        for(Supply sup : suppliesFound){
            if(!suppliesSawBefore.containsKey(sup)) suppliesSawBefore.put(sup, Game.currentRound);
            if(!suppliesCurrentlyVisible.containsKey(sup)) suppliesCurrentlyVisible.put(sup, Game.currentRound);
        }
        // Now remove from the suppliesCurrentlyVisible field the supplies that are no longer visible
        // In the ArrayList below store the supplies that will be removed from the currently visible map
        ArrayList<Supply> toBeRemoved = new ArrayList<>();
        for(Map.Entry<Supply, Integer> entry : suppliesCurrentlyVisible.entrySet()){
            for(Supply sup : suppliesFound)
                if (sup.getSupply_id() != entry.getKey().getSupply_id()) toBeRemoved.add(sup);
        }
        // Now remove them safely without getting ConcurrentModificationException
        for(Supply supply : toBeRemoved) suppliesCurrentlyVisible.remove(supply);
    }
}
