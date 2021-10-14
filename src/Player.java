import java.util.*;

public class Player {

    ////////////////////////////////////////
    //////// VARIABLES
    ///////////////////////////////////////

    // Primitive type (e.g. int) are initialized to zero and all the arrays
    // and objects including strings to null
    protected int playerId; // the identification of the player
    protected String name;
    protected Board board;
    protected int score, x, y;
    protected int suppliesTheseusCollected;

    protected TreeMap<Integer, Integer> distancesMap = new TreeMap<>(); // called at each round and stores the distances
    // of every object (supply or opponent) in a key-value form.

    //////////////////////////////////////
    ///////// METHODS
    /////////////////////////////////////

    // int[] is [ id, x, y, supplyId that got]
    // The id parameter is not used but implemented because of the instructions
    public int[] move(int opponentId){
        int[] info = new int[4];

        int findTileId = x*board.getN() + y;
        distancesMap = tilesNearSupplyOrOpponent(findTileId, opponentId);
        int selectMove = this.getNextMove(findTileId, opponentId);

        // Choose whether Theseus or minotaur plays next
        // so that we move them appropriately with the suitable
        // method that are implemented below
        if(playerId == 1)
            info = moveTheseus(selectMove, findTileId);
        else if(playerId == 2)
            info = moveMinotaur(selectMove, findTileId);
        else{
            System.out.println("Something is going wrong!!!");
            System.exit(1);
        }

        return info;
    }

    // For code clarity, readability and debugging reasons i create the following
    // three methods separately and then call them in move() method

    // If the playerId shows that it's Theseus turn to play, move() method calls this one
    // and plays for Theseus, returns an array identical with the one that move() must return
    // and move returns it without any changes.
    protected int[] moveTheseus(int selectMove, int findTileId){
        int[] info = new int[4];
        System.out.println("Theseus initial coordinates (x, y): (" + x + ", " + y + ")");

        // now we have the selected move for Theseus

        //System.out.println("THESEUS MOVE IN PLAYER IS " + selectMove);
        // If he wants to go up, i store in the array the new tileId which is exactly
        // N positions after the current one, the new coordinates, change the coordinates so that
        // the object variables are updated for the next time Theseus plays
        // If he collects a supply in the new tile, set the supply variables except its id to -1
        // so i can disappear it from the board and store its id in the info[3]
        if(selectMove == 1){
            info[0] = findTileId + board.getN();
            info[1] = x + 1;
            info[2] = y;
            x = x + 1;
            if(x<0 || y<0) System.out.println("Out of bounds");

            for(Supply sup : board.getSupplies()){
                if(sup.getSupply_tile_id() == info[0]){
                    score++;
                    info[3] = sup.getSupply_id();
                    int index = sup.getSupply_id()-1;
                    board.getSupplies()[index].setSupply_tile_id(-1);
                    board.getSupplies()[index].setX(-1);
                    board.getSupplies()[index].setY(-1);
                    board.getSupplies()[index].setActivated(false);
                    board.getSupplies()[index].setCollectedInRound(Game.currentRound);
                    System.out.printf("A supply with ID %d has been collected!!!\n", info[3]);
                    suppliesTheseusCollected++;
                    break;
                }
                else info[3] = 0;
            }
        }

        // Same logic but for right move
        if(selectMove == 3){
            info[0] = findTileId + 1;
            info[1] = x;
            info[2] = y + 1;
            y = y + 1;
            if(x<0 || y<0) System.out.println("Out of bounds");

            for(Supply sup : board.getSupplies()){
                if(sup.getSupply_tile_id() == info[0]){
                    score++;
                    info[3] = sup.getSupply_id();
                    int index = sup.getSupply_id()-1;
                    board.getSupplies()[index].setSupply_tile_id(-1);
                    board.getSupplies()[index].setX(-1);
                    board.getSupplies()[index].setY(-1);
                    board.getSupplies()[index].setActivated(false);
                    board.getSupplies()[index].setCollectedInRound(Game.currentRound);
                    System.out.printf("A supply with ID %d has been collected!!!\n", info[3]);
                    suppliesTheseusCollected++;
                    break;
                }
                else info[3] = 0;
            }
        }

        // Same logic but for down move
        if(selectMove == 5){
            info[0] = findTileId - board.getN();
            info[1] = x + 1;
            info[2] = y;
            x = x - 1;
            if(x<0 || y<0) System.out.println("Out of bounds");

            for(Supply sup : board.getSupplies()){
                if(sup.getSupply_tile_id() == info[0]){
                    score++;
                    info[3] = sup.getSupply_id();
                    int index = sup.getSupply_id()-1;
                    board.getSupplies()[index].setSupply_tile_id(-1);
                    board.getSupplies()[index].setX(-1);
                    board.getSupplies()[index].setY(-1);
                    board.getSupplies()[index].setActivated(false);
                    board.getSupplies()[index].setCollectedInRound(Game.currentRound);
                    System.out.printf("A supply with ID %d has been collected!!!\n", info[3]);
                    suppliesTheseusCollected++;
                    break;
                }
                else info[3] = 0;
            }
        }

        // Same logic but for left move
        if(selectMove == 7){
            info[0] = findTileId - 1;
            info[1] = x;
            y = y - 1;
            info[2] = y;
            if(x<0 || y<0) System.out.println("Out of bounds");

            // Note: Try to set (x, y) to (-1, -1) and change string representation so that
            // with an equality if statement it removes entirely the supply from the string board
            for(Supply sup : board.getSupplies()){
                if(sup.getSupply_tile_id() == info[0]){
                    score++;
                    info[3] = sup.getSupply_id();
                    int index = sup.getSupply_id()-1;
                    board.getSupplies()[index].setSupply_tile_id(-1);
                    board.getSupplies()[index].setX(-1);
                    board.getSupplies()[index].setY(-1);
                    board.getSupplies()[index].setActivated(false);
                    board.getSupplies()[index].setCollectedInRound(Game.currentRound);
                    System.out.printf("A supply with ID %d has been collected!!!\n", info[3]);
                    suppliesTheseusCollected++;
                    break;
                }
                else info[3] = 0;
            }
        }

        // Same logic but for illegal move
        // nothing happens, info has the current tile information
        if(selectMove == -1){
            info[0] = findTileId;
            info[1] = x;
            info[2] = y;
            info[3] = 0;
        }

        System.out.printf("Theseus new coordinates (x, y): (%d, %d)\n", x, y);

        return info;
    }

    // Same logic for this class as the previous one but now minotaur plays.
    // Only difference that he can not collect a supply.
    protected int[] moveMinotaur(int selectMove, int findTileId){
        int[] info  = new int[4];
        System.out.println("Minotaur initial coordinates (x, y): (" + x + ", " + y + ")");

        // now we have the selected move

        // Go up
        if(selectMove == 1){
            info[0] = findTileId + board.getN();
            info[1] = x + 1;
            info[2] = y;
            x = x + 1;
            info[3] = 0;
        }
        // Go right
        if(selectMove == 3){
            info[0] = findTileId + 1;
            info[1] = x;
            info[2] = y + 1;
            y = y + 1;
            info[3] = 0;
        }
        // Go down
        if(selectMove == 5){
            info[0] = findTileId - board.getN();
            info[1] = x + 1;
            info[2] = y;
            x = x - 1;
            info[3] = 0;
        }
        // Go left
        if(selectMove == 7){
            info[0] = findTileId - 1;
            info[1] = x;
            info[2] = y - 1;
            y = y - 1;
            info[3] = 0;
        }
        // Can not move
        if(selectMove == -1){
            info[0] = findTileId;
            info[1] = x;
            info[2] = y;
            info[3] = 0;
        }
        System.out.printf("Minotaur new coordinates (x, y): (%d, %d)\n", x, y);
        return info;
    }

    // The below method handles the roll of the dice, the restriction to be in the interval {1, 3, 5, 7}
    // and the movement selected validity and returns -1 if is invalid or the valid value of it.
    public int getNextMove(int id, int opponentId){
        Random rand = new Random();
        ArrayList<Integer> arrayList = findValidMoves(id);
        return arrayList.get(rand.nextInt(arrayList.size()));
    }

    // In the next method we find all the possible moves that can be played
    protected ArrayList<Integer> findValidMoves(int currentPos){
        HashMap<Integer, Boolean> map = new HashMap<>();
        ArrayList<Integer> validMoves = new ArrayList<>();

        // Fill the HashMap
        map.put(1, true);
        map.put(3, true);
        map.put(5, true);
        map.put(7, true);

        // Now change to false the values of the map if one move is invalid
        //int currentPos = x*board.getN() + y;
        for(int selectMove=1; selectMove<8; selectMove+=2){
            // Make the appropriate checks
            if(currentPos == 0 && selectMove == 5){
                map.put(5, false);
                //System.out.println("You can not move!!!");
            }
            else if(board.getTiles()[currentPos].isUp() && selectMove == 1){
                map.put(1, false);
                //System.out.println("You can not move!!!");
            }
            else if(board.getTiles()[currentPos].isRight() && selectMove == 3){
                map.put(3, false);
                //System.out.println("You can not move!!!");
            }
            else if(board.getTiles()[currentPos].isDown() && selectMove == 5){
                map.put(5, false);
                //System.out.println("You can not move!!!");
            }
            else if(board.getTiles()[currentPos].isLeft() && selectMove == 7){
                map.put(7, false);
                //System.out.println("You can not move!!!");
            }
        }

        // Now the HashMap has stored in key-value pairs all the information
        // for which moves can be played and which not.
        // In this point let's get rid of the invalid moves and return an array with the possible moves.

        // We need to iterate through the HashMap elements. Using an enhanced for loop
        for(Map.Entry<Integer, Boolean> entry : map.entrySet())
            if (entry.getValue()) validMoves.add(entry.getKey());

        // Now return the Arraylist that contains all the possible moves
        return validMoves;
    }

    //======================================================================================================================================
    //======================================================================================================================================
    //=============================== METHODS BEEN TRANSFERRED FROM HeuristicPlayer ========================================================
    //======================================================================================================================================
    //======================================================================================================================================

    protected TreeMap<Integer, Integer> tilesNearSupplyOrOpponent(int currentPos, int opponentTileId){
        // Declare the TreeMap to be returned
        TreeMap<Integer, Integer> sortedMap = new TreeMap<>();
        //System.out.println("Current Position = " + currentPos);

        // Firstly store the results of the functions that find the visible tiles i new Lists
        ArrayList<Tile> upList = tilesVisibleUpwards(currentPos);
        ArrayList<Tile> rightList = tilesVisibleRightwards(currentPos);
        ArrayList<Tile> downList = tilesVisibleDownwards(currentPos);
        ArrayList<Tile> leftList = tilesVisibleLeftwards(currentPos);

        // Find current round's coordinates
        // The simulation requires bec the x, y are not really changing
        int currentX = currentPos/board.getN();
        int currentY = currentPos%board.getN();

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
        else if(suppliesFound.size() == 1) sortedMap.put(1, suppliesFound.get(0).getX() - currentX);
        else if(suppliesFound.size() == 2){
            sortedMap.put(1, suppliesFound.get(0).getX() - currentX);
            sortedMap.put(11, suppliesFound.get(1).getX() - currentX);
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(1, suppliesFound.get(0).getX() - currentX);
            sortedMap.put(11, suppliesFound.get(1).getX() - currentX);
            sortedMap.put(111, suppliesFound.get(2).getX() - currentX);
        }
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
        else if(suppliesFound.size() == 1) sortedMap.put(3, suppliesFound.get(0).getY() - currentY);
        else if(suppliesFound.size() == 2){
            sortedMap.put(3, suppliesFound.get(0).getY() - currentY);
            sortedMap.put(33, suppliesFound.get(1).getY() - currentY);
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(3, suppliesFound.get(0).getY() - currentY);
            sortedMap.put(33, suppliesFound.get(1).getY() - currentY);
            sortedMap.put(333, suppliesFound.get(2).getY() - currentY);
        }
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
        else if(suppliesFound.size() == 1) sortedMap.put(5, currentX - suppliesFound.get(0).getX());
        else if(suppliesFound.size() == 2){
            sortedMap.put(5, currentX - suppliesFound.get(0).getX());
            sortedMap.put(55, currentX - suppliesFound.get(1).getX());
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(5, currentX - suppliesFound.get(0).getX());
            sortedMap.put(55, currentX - suppliesFound.get(1).getX());
            sortedMap.put(555, currentX - suppliesFound.get(2).getX());
        }
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
        else if(suppliesFound.size() == 1) sortedMap.put(7, currentY - suppliesFound.get(0).getY());
        else if(suppliesFound.size() == 2){
            sortedMap.put(7, currentY - suppliesFound.get(0).getY());
            sortedMap.put(77, currentY - suppliesFound.get(1).getY());
        }
        else if(suppliesFound.size() == 3){
            sortedMap.put(7, currentY - suppliesFound.get(0).getY());
            sortedMap.put(77, currentY - suppliesFound.get(1).getY());
            sortedMap.put(777, currentY - suppliesFound.get(2).getY());
        }
        suppliesFound.clear();
        if(xOpponent == -1 || yOpponent == -1) sortedMap.put(6, 0);
        else sortedMap.put(6, y - yOpponent);

        //System.out.println(sortedMap.toString());

        // Only left to return the treemap
        return sortedMap;
    }

    // In the methods below we find the tiles that are visible in a certain way
    // Each method takes care of a different way (up, right, down, left).
    protected ArrayList<Tile> tilesVisibleUpwards(int findTileId){
        ArrayList<Tile> returnList = new ArrayList<>(); // the ArrayList to be returned
        //int findTileId = x*board.getN() + y; // the tile id of the player's position

        // First find the how many tiles are visible after checking if a wall interrupts the visibility
        // and then add the tiles that are visible in the ArrayList.

        // Check if the tile that player is currently on has a wall, and if so, return an empty ArrayList
        if(board.getTiles()[findTileId].isUp()){
            //System.out.println("This tile has a wall upwards!!!");
            return returnList;
        }
        // Now scan the area in this way and store the tiles that are visible
        findTileId += board.getN(); // change the index to check the upper tiles
        boolean hasWallUp; // if a tile has wall in its up orientation become true
        int maxVisibility = 3; // a player can see only three tiles further
        for(int i=0; i<maxVisibility; i++){
            hasWallUp = board.getTiles()[findTileId].isUp();
            returnList.add(board.getTiles()[findTileId]);
            //System.out.println("Tile with id: "+findTileId+" has been added to the UPWARDS list");
            if(!hasWallUp) findTileId += board.getN(); // update this variable for the next loop
            else break;
        }

        return returnList;
    }

    protected ArrayList<Tile> tilesVisibleRightwards(int findTileId){
        ArrayList<Tile> returnList = new ArrayList<>();
        // Check if the tile that player is currently on has a wall, and if so, return an empty ArrayList
        if(board.getTiles()[findTileId].isRight()){
            //System.out.println("This tile has a wall rightwards!!!");
            return returnList;
        }
        // Now scan the area in this way and store the tiles that are visible
        findTileId++; // change the index to check the right tiles
        boolean hasWallRight; // if a tile has wall in its right orientation become true
        int maxVisibility = 3; // a player can see only three tiles further
        for(int i=0; i<maxVisibility; i++){
            hasWallRight = board.getTiles()[findTileId].isRight();
            returnList.add(board.getTiles()[findTileId]);
            //System.out.println("Tile with id: "+findTileId+" has been added to the RIGHTWARDS list");
            if(!hasWallRight) findTileId++; // update this variable for the next loop
            else break;
        }

        return returnList;
    }

    protected ArrayList<Tile> tilesVisibleDownwards(int findTileId){
        ArrayList<Tile> returnList = new ArrayList<>();

        if(board.getTiles()[findTileId].isDown()) return returnList;

        // Now scan the area in this way and store the tiles that are visible
        findTileId -= board.getN(); // change the index to check the down tiles
        boolean hasWallDown; // if a tile has wall in its down orientation become true
        int maxVisibility = 3; // a player can see only three tiles further
        for(int i=0; i<maxVisibility; i++){
            if(x == 0 && y == 0) break;
            if(findTileId < 0) break;
            hasWallDown = board.getTiles()[findTileId].isDown();
            returnList.add(board.getTiles()[findTileId]);
            //System.out.println("Tile with id: "+findTileId+" has been added to the DOWNWARDS list");
            if(!hasWallDown) findTileId -= board.getN(); // update this variable for the next loop
            else break;
        }

        return returnList;
    }

    protected ArrayList<Tile> tilesVisibleLeftwards(int findTileId){
        ArrayList<Tile> returnList = new ArrayList<>();

        // Check if the tile that player is currently on has a wall, and if so, return an empty ArrayList
        if(board.getTiles()[findTileId].isLeft()){
            //System.out.println("This tile has a wall leftwards!!!");
            return returnList;
        }
        // Now scan the area in this way and store the tiles that are visible
        findTileId--; // change the index to check the left tiles
        boolean hasWallLeft; // if a tile has wall in its left orientation become true
        int maxVisibility = 3; // a player can see only three tiles further
        for(int i=0; i<maxVisibility; i++){
            hasWallLeft = board.getTiles()[findTileId].isLeft();
            returnList.add(board.getTiles()[findTileId]);
            //System.out.println("Tile with id: "+findTileId+" has been added to the LEFTWARDS list");
            if(!hasWallLeft) findTileId--; // update this variable for the next loop
            else break;
        }

        return returnList;
    }

    //////////////////////////////////////
    ///////// CONSTRUCTORS
    /////////////////////////////////////

    public Player(int playerId, String name, Board board, int score, int x, int y){
        this.playerId = playerId; this.name = name;
        this.score = score; this.x = x; this.y = y;
        // First use new keyword to initialize the object board
        this.board = new Board(board.getN(), board.getS(), board.getW());
        // Then, set the Supplies and Tiles arrays with the use of set-get methods
        this.board.setSupplies(board.getSupplies());
        this.board.setTiles(board.getTiles());
    }

    //////////////////////////////////////
    ///////// GETTERS
    /////////////////////////////////////

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getSuppliesTheseusCollected() {
        return suppliesTheseusCollected;
    }
    public int getTileId(){ return board.getN()*x+y;}
}
