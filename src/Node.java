import java.util.ArrayList;
import java.util.TreeMap;

public class Node {

    ///////////////////////////////////////////////////////////
    //////// CLASS VARIABLES
    //////////////////////////////////////////////////////////

    private Node parentNode; // the parent
    public ArrayList<Node> childrenNodes = new ArrayList<>(); // dynamic array with the children
    private int move; // the move that happened to come to this node (the branch encoding), move = {1, 3, 5, 7}
    private int nodeDepth; // the depth
    private double nodeEvaluation; // the result of the evaluate method

    /////////////////////////////////////////////////////////
    //////// CONSTRUCTORS
    ////////////////////////////////////////////////////////

    // The empty constructor that will be called
    // No need for parameters. ArrayList is declared with new keyword in the class fields
    // and parent is null until this change
    public Node(){}

    // The method that connects a child to the children array of this node
    // This means that the current node (this) is the parent of the one given as parameter.
    public void connectChild(Node child){
        childrenNodes.add(child);
        if(child != null) child.setParentNode(this);
    }

    ////////////////////////////////////////////////////////
    //////// SETTERS GETTERS
    ///////////////////////////////////////////////////////

    public double getNodeEvaluation() {
        return nodeEvaluation;
    }
    public int getNodeDepth() {
        return nodeDepth;
    }
    public Node getParentNode() {
        return parentNode;
    }

    public int getMove() {
        return move;
    }

    public void addChildren(Node aNode){
        childrenNodes.add(aNode);
    }
    public void setChildrenNodes(ArrayList<Node> childrenNodes) {
        this.childrenNodes.addAll(childrenNodes);
    }
    public void setNodeDepth(int nodeDepth) {
        this.nodeDepth = nodeDepth;
    }
    public void setNodeEvaluation(double nodeEvaluation) {
        this.nodeEvaluation = nodeEvaluation;
    }
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public void setMove(int move) {
        this.move = move;
    }
}
