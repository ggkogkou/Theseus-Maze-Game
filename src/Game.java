public class Game {

    // **************** VARIABLES *******************************************************

    public static int currentRound = 0;

    public static int MAX_ROUNDS = 50;

    public static int N = 15; // default 15
    public static int S = 4; // default 4
    public static int W = (N * N * 3 + 1) / 2; // default value

    // {1 : Random, 2 : Heuristic, 3 : MinMax}
    public static int theseusType;

    public static int minotaurType;

    public static Player theseus;

    public static Player minotaur;

    public static String firstToPlay;

    public static Board board;

    private static int[] moveTheseus = {0, 0, 0, 0};

    private static int[] moveMinotaur = {N*N/2, N/2, N/2, 0};

    public static boolean gameOn = true;

    public static int winner = -1; // {0 : draw, 1 : theseus, 2 : minotaur}

    //*********** METHODS + MAIN ********************************************************************

    public static void main(String[] args) {
        new GUI();
    }

    // Launch the game, given the appropriate information by the user.
    public static void launch(){
        // Initialize the board of the game
        board = new Board(N, S, W);
        board.repaint();

        // Declare Theseus
        if(theseusType == 1)
            theseus = new Player(1, "Theseus", board, 0, 0, 0);
        else if(theseusType == 2)
            theseus = new HeuristicPlayer(board, 1, "Theseus", 0, 0, 0, N*N/2);
        else if(theseusType == 3)
            theseus = new MinMaxPlayer(board, 1, "Theseus", 0, 0, 0);

        // Declare Minotaur
        if(minotaurType == 1)
            minotaur = new Player(2, "Minotaur", board, 0, N/2, N/2);
        else if(minotaurType == 2)
            minotaur = new HeuristicMinotaur(board, 2, "Minotaur", 0, N/2, N/2, 0);
        else if(minotaurType == 3)
            minotaur = new MinMaxPlayer(board, 2, "Minotaur", 0, N/2, N/2);
    }

    // Play the next round
    public static void nextRound(){
        gameOn = currentRound < MAX_ROUNDS; // update the gameOn variable, true if there is a next round

        // Check if game is over,
        if(!gameOn) gameOver();

        // If not execute the following
        System.out.printf("\n--- ROUND: %d  ---------------------------------------------\n", currentRound+1);

        if(firstToPlay.equals("Theseus")){
            moveTheseus = theseus.move(minotaur.getX() * N + minotaur.getY());
            if (theseus.getSuppliesTheseusCollected() == S) gameOver();
            else if (moveMinotaur[0] == moveTheseus[0]) gameOver();
            else {
                // Minotaur turn to play
                moveMinotaur = minotaur.move(theseus.getX() * N + theseus.getY());
                if (moveMinotaur[0] == moveTheseus[0]) {
                    gameOver(); return;
                }
                currentRound++;
            }
        }
        else{
            moveMinotaur = minotaur.move(theseus.getX() * N + theseus.getY());
            if(moveMinotaur[0] == moveTheseus[0]) gameOver(); // termination check
            // If minotaur didn't kill theseus continue the game.
            // Theseus turn.
            moveTheseus = theseus.move(minotaur.getX() * N + minotaur.getY());
            if (theseus.getSuppliesTheseusCollected() == S) gameOver();
            else if (moveMinotaur[0] == moveTheseus[0]) gameOver();
            else currentRound++;
        }
    }

    // When the game ends, i find who won the game.
    public static void gameOver(){
        gameOn = false;
        // Find out who won. First, check if it is draw, then if theseus wins, then minotaur.
        // Update the winner variable so the GUI.gameOverScreen() use it to display the right message.
        if(currentRound == MAX_ROUNDS) winner = 0; // IT'S A DRAW
        else if(theseus.getSuppliesTheseusCollected() == S) winner = 1; // THESEUS WINS
        else winner = 2; // MINOTAUR WINS
    }

    // Return everything to default and start the game again.
    public static void newGame(){
        currentRound = 0;
        N = 15;
        S = 4;
        W = (N * N * 3 + 1) / 2;
        theseusType = 0;
        minotaurType = 0;
        theseus = null;
        minotaur = null;
        board = null;
        gameOn = true;
        moveTheseus = null;
        moveMinotaur = null;
        moveTheseus = new int[]{0, 0, 0, 0};
        moveMinotaur = new int[]{N * N / 2, N / 2, N / 2, 0};
        winner = -1;
        new GUI();
    }
}
