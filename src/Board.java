import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Board extends JPanel {

    ////////////////////////////////////////////
    //////// VARIABLES
    ///////////////////////////////////////////

    private int N;              // dimensions of board
    private int S, W;           // supply number active and walls that can be built
    private Tile[] tiles;       // set the id at the appropriate values
    private Supply[] supplies;  // array of supply objects

    private static final int CELL_WIDTH = 25; // maze square size
    private static final int MARGIN = 50; // buffer between window edge and maze
    private static final int DOT_SIZE = 15; // size of maze solution dot
    private static final int DOT_MARGIN = 5; // space between wall and dot

    // Set Layout of panel to null
    @Override
    public void setLayout(LayoutManager mgr) {
        super.setLayout(null);
    }

    // Set the background to cyan
    @Override
    public void setBackground(Color bg) {
        super.setBackground(Color.CYAN);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(true);
    }

    @Override
    public void setAutoscrolls(boolean autoscrolls) {
        super.setAutoscrolls(true);
    }

    //---------- CONSTRUCTOR ---------------------------------

    public Board(int N, int S, int W){
        this.N = N; this.S = S; this.W = W;
        tiles = new Tile[N*N];      // all tiles are initialized to null
        supplies = new Supply[S];
        createBoard();
    }

    ////////////////////////////////////////
    ////// METHODS
    ///////////////////////////////////////

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.black);

        Tile[][] tiles2 = new Tile[N][N];
        int count = N*N - 1;
        for(int i=0; i<N; i++){
            for(int j=N-1; j>=0; j--){
                tiles2[i][j] = tiles[count];
                count--;
            }
        }

        // Draw the walls as black lines
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // If the wall is northern draw with this pattern
                if (tiles2[i][j].isUp()){
                    g.drawLine((j * CELL_WIDTH + MARGIN), (i * CELL_WIDTH + MARGIN),
                            ((j + 1) * CELL_WIDTH + MARGIN), (i * CELL_WIDTH + MARGIN));
                }
                // Southern wall case
                if (tiles2[i][j].isDown()) {
                    g.drawLine(j * CELL_WIDTH + MARGIN, (i + 1) * CELL_WIDTH
                            + MARGIN, (j + 1) * CELL_WIDTH + MARGIN, (i + 1) * CELL_WIDTH
                            + MARGIN);
                }
                // East wall case
                if (tiles2[i][j].isRight()) {
                    g.drawLine((j + 1) * CELL_WIDTH + MARGIN, i * CELL_WIDTH
                            + MARGIN, (j + 1) * CELL_WIDTH + MARGIN, (i + 1) * CELL_WIDTH
                            + MARGIN);
                }
                // West wall case
                if (tiles2[i][j].isLeft()){
                    g.drawLine(j * CELL_WIDTH + MARGIN, i * CELL_WIDTH + MARGIN, j
                            * CELL_WIDTH + MARGIN, (i + 1) * CELL_WIDTH + MARGIN);
                }
            }
        }

        // Draw supplies with yellow
        g.setColor(new Color(255, 249, 0)); // changes color to draw the dots
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tileHasSupply(tiles2[i][j].getTile_id())){
                    g.fillOval(j * CELL_WIDTH + MARGIN + DOT_MARGIN, i * CELL_WIDTH
                            + MARGIN + DOT_MARGIN, DOT_SIZE, DOT_SIZE);
                }
            }
        }

        // Draw Theseus in blue oval
        g.setColor(new Color(29, 12, 203));
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles2[i][j].getTile_id() == Game.theseus.getTileId()){
                    g.fillOval(j * CELL_WIDTH + MARGIN + DOT_MARGIN, i * CELL_WIDTH
                            + MARGIN + DOT_MARGIN, DOT_SIZE, DOT_SIZE);
                }
            }
        }

        // Draw minotaur in brown
        g.setColor(new Color(198, 119, 7));
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles2[i][j].getTile_id() == Game.minotaur.getTileId()){
                    g.fillOval(j * CELL_WIDTH + MARGIN + DOT_MARGIN, i * CELL_WIDTH
                            + MARGIN + DOT_MARGIN, DOT_SIZE, DOT_SIZE);
                }
            }
        }

        setPreferredSize(windowSize());
        this.setBackground(Color.lightGray);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
    }

    // returns the ideal size of the window (for
    // JScrollPanes)
    public Dimension windowSize() {
        return new Dimension(N * CELL_WIDTH + MARGIN * 2, N * CELL_WIDTH + MARGIN * 2);
    }

    // In this method i initialize every single tile in tiles array
    private void createTile(){
        Random rand = new Random(); // create a Random object to use below
        int buildWallTileId;        // stores the random wall that will be built inside board
        int wallOrientation;        // find randomly the orientation (up, down etc) of the wall that will be built in a tile
        int row, column;            // store the row and column value

        // Now i will go through all tiles in the tiles array and initialize the standard outside walls
        // tile with id=0 won't have a tile on its south.
        // The middle tiles will be put walls later with the use of the setters.
        for(int i=0; i<N*N; i++){
            row = i/N;
            column = i%N;

            // From now on, all the corner tiles and especially the 0(because it doesn't have wall on its south
            // are treated differently than the others. So that leaves us with 5 other main categories of tiles
            // that will be treated differently each other: bottom row, upper row, first column, last column
            // The following IF-ELSE-IF treat differently these 9 categories of tiles

            //////// IF-ELSE-IF FOR THE BOTTOM ROW

            if(row == 0 && column == 0)
                tiles[i] = new Tile(i, row, column, false, false, false, true);
            else if(row == 0 && column == N-1)
                tiles[i] = new Tile(i, row, column, false, true, true, false);
            else if(row == 0)
                tiles[i] = new Tile(i, row, column, false, true, false, false);

            //////// IF-ELSE-IF FOR THE MIDDLE ROWS

            if(row > 0 && row < N-1 && column == 0)
                tiles[i] = new Tile(i, row, column, false, false, false, true);
            else if(row > 0 && row < N-1 && column == N-1)
                tiles[i] = new Tile(i, row, column, false, false, true, false);
            else if(row > 0 && row < N-1 && column > 0 && column < N-1)
                tiles[i] = new Tile(i, row, column, false, false, false, false);

            //////// IF-ELSE-IF FOR THE TOP ROW

            if(row == N-1 && column == 0)
                tiles[i] = new Tile(i, row, column, true, false, false, true);
            else if(row == N-1 && column == N-1)
                tiles[i] = new Tile(i, row, column, true, false, true, false);
            else if(row == N-1 && column > 0 && column < N-1)
                tiles[i] = new Tile(i, row, column, true, false, false, false);

        }

        // now we have all the tiles initialized properly and borders are set
        // we have to just build the walls that remain into the board
        // we can build up to W walls minus the walls in the perimeter
        // so W-4*N+1 b/c tile 0 doesn't have wall in the south

        //boolean lastWallWasConstructed = false;
        for(int count=0; count < W-4*N+1; count++){
            buildWallTileId = rand.nextInt(N*N);
            wallOrientation = rand.nextInt(4); // this random number is from 0...3
            //System.out.println("Wall orientation given is: " + wallOrientation);
            row = tiles[buildWallTileId].getX();
            column = tiles[buildWallTileId].getY();
            //System.out.println("row: " + row + ", column: " + column);

            // Firstly, i am going to check if the randomly selected tile is capable to build a wall
            // through the checkRestrictions method that checks if both of the restrictions presented
            // in the instructions pdf and returns true if it can build.
            // Next, i will check all the possible wall orientations that a certain tile can build a wall
            // and then, i will check with checkRestrictions method the tile that is going to have a mutual wall
            // between. If both of the tiles are ok with the wall between them, then i make the appropriate changes to the
            // two tiles through their set methods. Below, the various cases are presented analytically.

            if(checkRestrictions(buildWallTileId, wallOrientation)){

                // Now i will take various cases to ensure that the walls will be put correctly
                // I take various cases because of the fact that the walls in the boarders and corners
                // must be treated differently.


                // Tiles in the bottom row but not at the corners
                if(row == 0 && column > 0 && column < N-1){
                    if(wallOrientation == 0 && checkRestrictions(buildWallTileId+N, 2)){
                        tiles[buildWallTileId].setUp(true);
                        tiles[buildWallTileId + N].setDown(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                    }
                    else if(wallOrientation == 1 && checkRestrictions(buildWallTileId+1, 3)){
                        tiles[buildWallTileId].setRight(true);
                        tiles[buildWallTileId+1].setLeft(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                    }
                    else if(wallOrientation == 3 && checkRestrictions(buildWallTileId-1, 1)){
                        tiles[buildWallTileId].setLeft(true);
                        tiles[buildWallTileId-1].setRight(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                    }
                    else {
                        count--;
                        //System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                        continue;
                    }
                }

                // Tiles in the first column but not at the corners
                if(row > 0 && row < N-1 && column == 0){
                    if(wallOrientation == 0 && checkRestrictions(buildWallTileId+N, 2)){
                        tiles[buildWallTileId].setUp(true);
                        tiles[buildWallTileId + N].setDown(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 1 && checkRestrictions(buildWallTileId+1, 3)){
                        tiles[buildWallTileId].setRight(true);
                        tiles[buildWallTileId+1].setLeft(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 2 && checkRestrictions(buildWallTileId-N, 0)){
                        tiles[buildWallTileId].setDown(true);
                        tiles[buildWallTileId-N].setUp(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else {
                        count--;
                        continue;
                        //System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                }

                // All the tiles that are not in the edges (middle tiles)
                if(row > 0 && row< N-1 && column > 0 && column < N-1){
                    if(wallOrientation == 0 && checkRestrictions(buildWallTileId+N, 2)){
                        tiles[buildWallTileId].setUp(true);
                        tiles[buildWallTileId + N].setDown(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 1 && checkRestrictions(buildWallTileId+1, 3)){
                        tiles[buildWallTileId].setRight(true);
                        tiles[buildWallTileId+1].setLeft(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 2 && checkRestrictions(buildWallTileId-N, 0)){
                        tiles[buildWallTileId].setDown(true);
                        tiles[buildWallTileId-N].setUp(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 3 && checkRestrictions(buildWallTileId-1, 1)){
                        tiles[buildWallTileId].setLeft(true);
                        tiles[buildWallTileId-1].setRight(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else {
                        count--;
                        continue;
                        //System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                }

                // Tiles in the last column but not at the corners
                if(row > 0 && row < N-1 && column == N-1){
                    if(wallOrientation == 0 && checkRestrictions(buildWallTileId+N, 2)){
                        tiles[buildWallTileId].setUp(true);
                        tiles[buildWallTileId + N].setDown(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 2 && checkRestrictions(buildWallTileId-N, 0)){
                        tiles[buildWallTileId].setDown(true);
                        tiles[buildWallTileId-N].setUp(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 3 && checkRestrictions(buildWallTileId-1, 1)){
                        tiles[buildWallTileId].setLeft(true);
                        tiles[buildWallTileId-1].setRight(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else {
                        count--;
                        continue;
                        //System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                    }
                }


                // Tiles in the top row but not at the corners
                if(row == N-1 && column > 0 && column < N-1){
                    if(wallOrientation == 1 && checkRestrictions(buildWallTileId+1, 3)){
                        tiles[buildWallTileId].setRight(true);
                        tiles[buildWallTileId+1].setLeft(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 2 && checkRestrictions(buildWallTileId-N, 0)){
                        tiles[buildWallTileId].setDown(true);
                        tiles[buildWallTileId-N].setUp(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 3 && checkRestrictions(buildWallTileId-1, 1)){
                        tiles[buildWallTileId].setLeft(true);
                        tiles[buildWallTileId-1].setRight(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else{
                        count--;
                        continue;
                        //System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                }

                // Now the special cases of the four corner tiles

                // Tile with id: 0
                // It can build one more wall
                if(row == 0 && column == 0){
                    if(wallOrientation == 0 && checkRestrictions(buildWallTileId+N, 2)){
                        tiles[buildWallTileId].setUp(true);
                        tiles[buildWallTileId + N].setDown(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else if(wallOrientation == 1 && checkRestrictions(buildWallTileId+1, 3)){
                        tiles[buildWallTileId].setRight(true);
                        tiles[buildWallTileId+1].setLeft(true);
                        //count++;
                        //System.out.println("That wall was built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                    else {
                        count--;
                        continue;
                        //System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);

                    }
                }

                // The methods below won't be executed as the initial checkRestrictions won't be true
                // So they are useless here as they are just going to be compiled

                // Tile in the upper row and last column (upper and right corner)
                // This tile can not build third tile
                if(row == N-1 && column == N-1){
                    System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                    continue;
                }

                // Tile in the bottom row and last column (down and right corner)
                // This tile can not build third wall
                if(row == 0 && column == N-1){
                    System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);
                    continue;
                }

                // The upper tile of the first column (at the corner)
                // It can not build third wall
                if(row == N-1 && column == 0)
                    System.out.println("That wall was NOT built in tile " + buildWallTileId + " with orientation " + wallOrientation);
            }
            /*
            The following code would be executed if we wanted all W walls to be built in this
            board. But the formula for calculating the walls given in the instructions is not 100% correct
            so if i attempt to build all the walls the program crashes cause this for-loop becomes infinite
            as the formula overloads the board and new walls can't be built anymore. In conclusion, we can't built
            all W walls int the board with this formula.

            else if(!checkRestrictions(buildWallTileId, wallOrientation)){
                System.out.println("Wall was not constructed, count = " + count);
                count--;
            }
            */
        }
    }

    // The method below checks if the restrictions in walls construction are met
    // and if so, it returns true else false.
    // The restrictions are:
    // 1. Has not more than 1 wall built on it so it can build another one
    // 2. The orientation of the wall chosen to be built is valid
    private boolean checkRestrictions(int tileId, int wallOrientation){
        boolean tileHasWallUp = tiles[tileId].isUp();
        boolean tileHasWallDown = tiles[tileId].isDown();
        boolean tileHasWallRight = tiles[tileId].isRight();
        boolean tileHasWallLeft = tiles[tileId].isLeft();
        boolean checkOrientation = false;
        int wallsAlreadyBuilt = 0;

        // first check if a tile has less or equal than 2 walls built in it
        if(tiles[tileId].isUp()) wallsAlreadyBuilt++;
        if(tiles[tileId].isDown()) wallsAlreadyBuilt++;
        if(tiles[tileId].isRight()) wallsAlreadyBuilt++;
        if(tiles[tileId].isLeft()) wallsAlreadyBuilt++;
        if(wallsAlreadyBuilt > 1) return false;
        // greater than 1 because we check if a wall CAN be built and that said, the tile
        // must have maximum 1 wall, so with the new one they become 2 (max for one tile)

        // now we made sure that a tile has less than 3 walls

        if(!tileHasWallUp && wallOrientation == 0) checkOrientation = true;
        if(!tileHasWallRight && wallOrientation == 1) checkOrientation = true;
        if(!tileHasWallDown && wallOrientation == 2) checkOrientation = true;
        if(!tileHasWallLeft && wallOrientation == 3) checkOrientation = true;

        return checkOrientation;
    }

    // The following method initializes all the supplies in the supplies array
    // Only restrictions are:
    // 1. Supply is not created in the initial position of Theseus or Minotaur
    // 2. Supply is not created in a tile that already has a supply
    private void createSupply(){
        Random rand = new Random();
        int supplyTileId;

        int row;
        int column;

        HashSet<Tile> tilesOccupied = new HashSet<>(S);

        for(int i=0; i<S; i++){
            // The supplyTileId must not be at the tile where Theseus or Minotaur begin
            // the game so it has to be different than 0 and N*N/2 b/c these are these are
            // Theseus and Minotaur tiles respectively
            supplyTileId = 1 + rand.nextInt(N*N-1); // the randoms start from 1 so Theseus restriction is solved
            //System.out.println(supplyTileId);
            if(supplyTileId == N*N/2){
                i--;
                continue; // that resolves the minotaur tile restriction and decreases i so that the supply can be created in the next loop
            }
            // Check if the tile is occupied by another supply and if it is decrease i and start loop again
            // but if not add it and then initialize a supply there
            if(tilesOccupied.contains(tiles[supplyTileId])){
                i--;
                continue;
            }
            tilesOccupied.add(tiles[supplyTileId]); // if not contained already add in the HashSet the tile that has a supply

            // Now that we are done with checking and solving the restrictions let's initialize the object
            row = supplyTileId/N;
            column = supplyTileId%N;
            supplies[i] = new Supply(i+1, row, column, supplyTileId); // the id starts form 1...n so initialize with i+1
        }
    }

    // This method just calls the two functions
    // Basically useless
    public void createBoard(){
        createTile();
        createSupply();
    }

    ///////////////////////////////////////
    //////// THE STRING REPRESENTATION
    //////////////////////////////////////


    // The next method creates a string 2D array that illustrates the board.
    // Every two string arrays represent one actual row of the board.
    // So, one sting array has to be created alone as every tile needs two string arrays
    // to be represented.
    private String[][] getStringRepresentation(int theseusTile, int minotaurTile){
        String[][] visualBoard = new String[2*N + 1][N];    // the board that will be returned

        String horizontalWall = "+---";                     // the horizontal walls
        String lastHorizontalWall = "+---+";                // the last horizontal wall, for illustration issues
        String verticalWall = "|";                          // the vertical walls
        String noHorizontalWall = "+   ";                   // no horizontal wall
        String lastNoHorizontalWall = "+   +";              // the last horizontal tile that has no wall up or down
        String noVerticalWall = " ";                        // if there isn't a vertical wall
        String tileNoContent = "   ";                       // tile with no content ( e.g. supply, theseus, minotaur )
        String tileHasTheseus = " T ";                      // theseus representation
        String tileHasMinotaur = " M ";                     // minotaur representation
        String tileHasSupply = " S";                        // will be manipulated to look like S1, S2, .. , Sn
        String tileHasSupplyAndMinotaur = "MS";             // when minotaur is in the same tile with a supply

        boolean hasUp;
        boolean hasLeft;

        // illustrate the bottom line of the visual board
        for(int column=0; column<N; column++){
            if(column == 0) visualBoard[2*N][column] = noHorizontalWall;
            else if(column == N-1) visualBoard[2*N][column] = lastHorizontalWall;
            else visualBoard[2*N][column] = horizontalWall;
        }

        int tileId = 0; // keep track of the id of the tiles
        for(int row=2*N-2; row>=0; row-=2){
            for(int column=0; column<N; column++){
                hasUp = tiles[tileId].isUp();
                if(column == N-1 && hasUp) visualBoard[row][column] = lastHorizontalWall;
                else if(column == N-1 && !hasUp) visualBoard[row][column] = lastNoHorizontalWall;
                else if(hasUp) visualBoard[row][column] = horizontalWall;
                else visualBoard[row][column] = noHorizontalWall;
                tileId++;
            }
        }

        tileId = 0;
        for(int row=2*N-1; row>0; row-=2){

            for(int column=0; column<N; column++){
                hasLeft = tiles[tileId].isLeft();
                if(column == N-1 && hasLeft) visualBoard[row][column] = verticalWall + tileNoContent + verticalWall;
                else if(column == N-1 && !hasLeft) visualBoard[row][column] = noVerticalWall + tileNoContent + verticalWall;
                else if(hasLeft) visualBoard[row][column] = verticalWall + tileNoContent;
                else visualBoard[row][column] = noVerticalWall + tileNoContent;
                tileId++;
            }
        }

        int findRow, findColumn;
        // Declare a HashSet of supplies for easier manipulation
        HashSet<Supply> suppliesOnBoard = new HashSet<>(S);
        // Add all the supplies in the HashSet
        suppliesOnBoard.addAll(Arrays.asList(supplies).subList(0, S));
        Iterator<Supply> iterateSet = suppliesOnBoard.iterator();

        for(int i=0; i<S; i++){
            if(supplies[i].getSupply_tile_id() == minotaurTile){
                findRow = supplies[i].getX() * 2;
                findColumn = supplies[i].getY();
                String supplyIdString = tileHasSupplyAndMinotaur + supplies[i].getSupply_id();

                // The 2D string array has opposite numbering than the tiles array so the rows are calculated from another
                // point of start. To resolve this, i point at the right row by the formula (2*N-1)-findRow.
                if(findColumn < N-1)
                    visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + supplyIdString;
                else if(findColumn == N-1)
                    visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + supplyIdString + verticalWall;
            }
            if(!supplies[i].isActivated()){
                // just disappear the supply
            }
            else{
                findRow = supplies[i].getX() * 2;
                findColumn = supplies[i].getY();
                // I convert supply id in string so i can concatenate
                String supplyIdString = tileHasSupply + supplies[i].getSupply_id();

                // The 2D string array has opposite numbering than the tiles array so the rows are calculated from another
                // point of start. To resolve this, i point at the right row by the formula (2*N-1)-findRow.
                if(findColumn < N-1)
                    visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + supplyIdString;
                else if(findColumn == N-1)
                    visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + supplyIdString + verticalWall;
            }
        }

        // Check whether theseus is in the same tile with minotaur, and if he is minotaur is represented and not Theseus
        // Iterate the HashSet and declare a new boolean variable minotaurOnSupply that becomes true when
        // minotaur is in the same tile with a supply
        boolean minotaurOnSupply = false;
        while(iterateSet.hasNext()){
            if(iterateSet.next().getSupply_tile_id() == minotaurTile) minotaurOnSupply = true;
        }

        if(minotaurTile == theseusTile){
            // their coordinates are the same so i use theseus variables bellow
            findRow = theseusTile / N * 2;
            findColumn = theseusTile % N;

            // Now manipulate the proper string so that minotaur is shown in the tile as he kills Theseus
            if(findColumn < N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasMinotaur;
            else if(findColumn == N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasMinotaur + verticalWall;
        }
        else if(!minotaurOnSupply){
            findRow = theseusTile / N * 2;
            findColumn = theseusTile % N;
            if(findColumn < N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasTheseus;
            else if(findColumn == N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasTheseus + verticalWall;


            findRow = minotaurTile / N * 2;
            findColumn = minotaurTile % N;
            if(findColumn < N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasMinotaur;
            else if(findColumn == N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasMinotaur + verticalWall;
        }
        // In the case which minotaur is in the same tile with a supply is handled above in the supply strings manipulation
        // for-loop. So i have to place only Theseus in this part of the code.
        else{
            findRow = theseusTile / N * 2;
            findColumn = theseusTile % N;
            if(findColumn < N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasTheseus;
            else if(findColumn == N-1)
                visualBoard[(2*N-1) - findRow][findColumn] = visualBoard[(2*N-1) - findRow][findColumn].charAt(0) + tileHasTheseus + verticalWall;
        }

        // Return the array and finish the method
        return visualBoard;
    }

    // Next method prints the board
    public void printBoard(int theseusTile, int minotaurTile){
        String[][] toPrint = getStringRepresentation(theseusTile, minotaurTile);
        // Now print the array
        for(int row=0; row<2*N+1; row++){
            for(int column=0; column<N; column++){
                System.out.printf("%s", toPrint[row][column]);
            }
            System.out.println();
        }
    }

    // In the method below we check if in a certain tile there is a supply. If there is the method
    // will return true. The tile id is given as a parameter.
    public boolean tileHasSupply(int tileId){
        // Iterate the array of supplies with an enhanced for loop
        for(Supply x : supplies){
            if(x.getSupply_tile_id() == tileId) return true;
        }
        return false;
    }

    // The following method takes as a parameter the id of a tile and if this tile has a supply
    // it returns the supply else it returns a supply with id 0
    public Supply tileWithSupply(int tileId){
        // Iterate the array of supplies with an enhanced for loop
        for(Supply x : supplies){
            if(x.getSupply_tile_id() == tileId) return x;
        }
        return new Supply();
    }

    /////////////////////////////////////////
    ///// SETTERS
    ////////////////////////////////////////

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles.clone();
    }
    public void setSupplies(Supply[] supplies) {
        this.supplies = supplies.clone();
    }


    /////////////////////////////////////////
    ///// GETTERS
    ////////////////////////////////////////


    public Supply[] getSupplies() {
        return supplies;
    }
    public Tile[] getTiles() {
        return tiles;
    }
    public int getN() {
        return N;
    }
    public int getS() {
        return S;
    }
    public int getW() {
        return W;
    }
}
