public class Tile {

    ///////////////////////////////////////////
    /////////// VARIABLES
    //////////////////////////////////////////

    // Primitive type (e.g. int) are initialized to zero and all the arrays
    // and objects including strings to null
    private int tile_id;                    // the identification number of the tile
    private int x, y;                       // the coordinates
    private boolean up, down, right, left;  // checks if there is wall by the sides

    ///////////////////////////////////////////
    /////////// CONSTRUCTORS
    //////////////////////////////////////////

    public Tile(){
        // empty constructor, default values: 0 and false
    }

    public Tile(int tile_id, int x, int y, boolean up, boolean down, boolean right, boolean left){
        this.tile_id = tile_id;
        this.x = x; this.y = y; this.up = up; this.down = down; this.right = right; this.left = left;
    }

    public Tile(Tile tile){
        tile_id = tile.tile_id;
        x = tile.x; y = tile.y;
        up = tile.up;
        down = tile.down;
        right = tile.right;
        left = tile.left;
    }

    ///////////////////////////////////////////
    /////////// SETTERS
    //////////////////////////////////////////

    public void setY(int y) {
        this.y = y;
    }
    public void setDown(boolean down) {
        this.down = down;
    }
    public void setLeft(boolean left) {
        this.left = left;
    }
    public void setRight(boolean right) {
        this.right = right;
    }
    public void setTile_id(int tile_id) {
        this.tile_id = tile_id;
    }
    public void setUp(boolean up) {
        this.up = up;
    }
    public void setX(int x) {
        this.x = x;
    }

    //////////////////////////////////////////
    ////////// GETTERS
    /////////////////////////////////////////

    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
    public int getTile_id() {
        return tile_id;
    }
    public boolean isDown() {
        return down;
    }
    public boolean isLeft() {
        return left;
    }
    public boolean isRight() {
        return right;
    }
    public boolean isUp() {
        return up;
    }
}
