public class Supply {

    // Primitive type (e.g. int) are initialized to zero and all the arrays
    // and objects including strings to null
    private int supply_id;          // the identification of the specific supply
    private int x; private int y;   // the coordinates in the x and y axes
    private int supply_tile_id;     // the id of the tile where the supply is
    private boolean activated;      // if the supply hasn't been collected it returns true
    private int collectedInRound;   // If the supply was collected, we store the round here

    /////////////////////////////////////
    /////////// CONSTRUCTORS
    ////////////////////////////////////

    public Supply(){
        // empty constructor
        // all fields are initialized to zero by default
    }

    public Supply(int id, int x, int y, int tile){
        supply_id = id; this.x = x; this.y = y; supply_tile_id = tile; activated = true; collectedInRound = -1;
    }

    public Supply(Supply supply){
        supply_id = supply.getSupply_id();
        supply_tile_id = supply.getSupply_tile_id();
        x = supply.getX();
        y = supply.getY();
    }

    public String toString(){
        return "Supply "+Integer.toString(supply_id)+" at tile :"+Integer.toString(supply_tile_id);
    }

    ///////////////////////////////////////////////
    /////////// SETTERS
    //////////////////////////////////////////////

    public void setSupply_id(int supply_id) {
        this.supply_id = supply_id;
    }
    public void setSupply_tile_id(int supply_tile_id) {
        this.supply_tile_id = supply_tile_id;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getCollectedInRound() {
        return collectedInRound;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    //////////////////////////////////////////
    ///////////// GETTERS
    /////////////////////////////////////////

    public int getSupply_id() {
        return supply_id;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setCollectedInRound(int collectedInRound) {
        this.collectedInRound = collectedInRound;
    }

    public int getSupply_tile_id() {
        return supply_tile_id;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
