import javax.swing.*;
import java.awt.*;

public class GUI {

    private final int MAIN_SCREEN_WIDTH = 600;

    private final int MAIN_SCREEN_HEIGHT = 475;

    private final int SELECT_PLAYER_WIDTH = 600;

    private final int SELECT_PLAYER_HEIGHT = 300;

    private final int FRAME_WIDTH = 700;

    private final int FRAME_HEIGHT = 700;

    private final String TITLE = "Theseus and Minotaur";

    private final String FONT = "Comic Sans MS";

    private final String BATTLE_PNG = "images/battle.png";

    private final String DRAW_PNG = "images/draw.png";

    private final String THESEUS_WON_PNG = "images/theseusWon.png";

    private final String MINO_WON_PNG = "images/minoWon.png";

    private final String FRAME_ICON = "images/frameIcon.png";

    public GUI(){
        mainScreen();
    }

    // Create and set up the main menu user interface for selecting
    // either to view instructions or play the game.
    public void mainScreen(){
        JFrame frame = new JFrame(TITLE);

        JLabel gameName = new JLabel();
        ImageIcon imageIcon = new ImageIcon(BATTLE_PNG);

        gameName.setText("Theseus and Minotaur maze game");
        gameName.setIcon(imageIcon);
        gameName.setHorizontalTextPosition(JLabel.CENTER);
        gameName.setVerticalTextPosition(JLabel.TOP);
        gameName.setForeground(Color.black);
        gameName.setBounds(0, 0, MAIN_SCREEN_WIDTH, MAIN_SCREEN_HEIGHT/2);
        gameName.setFont(new Font(FONT, Font.PLAIN, 32));
        gameName.setHorizontalAlignment(JLabel.CENTER);
        gameName.setVerticalAlignment(JLabel.NORTH);

        // Play button
        JButton play = new JButton("PLAY");
        play.setFont(new Font(FONT, Font.PLAIN, 20));
        play.setBackground(Color.lightGray);
        play.setBounds(150, MAIN_SCREEN_HEIGHT/2, 300, 80);

        // Give functionality
        play.addActionListener(ae -> {
//            frame.dispose();
            frame.getContentPane().removeAll();
            frame.getContentPane().repaint();
            selectPlayer(frame);
        });

        // Instructions button
        JButton instructions = new JButton("INSTRUCTIONS");
        instructions.setFont(new Font(FONT, Font.PLAIN, 20));
        instructions.setBackground(Color.lightGray);
        instructions.setBounds(150, MAIN_SCREEN_HEIGHT/2+100, 300, 80);

        // Give functionality
        instructions.addActionListener(ae -> {
            // Local variable has priority.
            JFrame frame1 = new JFrame("Instructions");
            frame1.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            JLabel textLabel = new JLabel("" +"<html>" +
                    "1. Firstly decide what algorithm you want Theseus to play.<br> " +
                    "2. Do the same thing for Minotaur.<br> " +
                    "3. Theseus' goal is to find all the supplies of the maze and <br>" +
                    " not be killed by minotaur. <br>" +
                    "4. Minotaurs' goal is to find and kill Theseus.<br>" +
                    "" +
                    "5. Press new round and the game will be played automatically. <br>" +
                    "6. The game ends when all supplies are collected or theseus dies or a maximum of 100 rounds was played. </html>");
            frame1.getContentPane().add(textLabel, BorderLayout.CENTER);

            frame1.setLocationRelativeTo(null);
            frame1.pack();
            frame1.setVisible(true);
        });


        frame.getContentPane().setBackground(Color.cyan);
        frame.getContentPane().add(instructions);
        frame.getContentPane().add(play);
        frame.getContentPane().add(gameName);

        frame.setIconImage(new ImageIcon(FRAME_ICON).getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null); // set size if layout is null
        frame.setSize(new Dimension(MAIN_SCREEN_WIDTH, MAIN_SCREEN_HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /*
    ***** BEFORE THE GAME SCREEN *******
    * 1. A pop up window that lets the user choose between the three possible
    * algorithms for each player to play.
    * 2. Then some pop up windows that are implemented in the next methods let the user
    * choose the dimensions of the square board, the number of the supplies, the number of
    * maximum rounds and set whether theseus or minotaur plays first.
    * 3. These methods update the static class variables of Game class and when gameScreen() method
    * calls Game.launch() to start the game with the parameters set by the user in these windows.
    * 4. gameScreen() method "controls" the game flow.
    */
    public void selectPlayer(JFrame frame){

        // Declare the message
        JLabel label1 = new JLabel("SELECT THESEUS STYLE");
        label1.setFont(new Font(FONT, Font.PLAIN, 25));
        label1.setBounds(130, 50, 600-130-130, 30);

        // Create the buttons

        JButton selectRandom = new JButton("Random");
        selectRandom.setFont(new Font(FONT, Font.PLAIN, 20));
        selectRandom.setBackground(Color.LIGHT_GRAY);

        JButton selectHeuristic = new JButton("Heuristic");
        selectHeuristic.setFont(new Font(FONT, Font.PLAIN, 20));
        selectHeuristic.setBackground(Color.LIGHT_GRAY);

        JButton selectMinMax = new JButton("MinMax");
        selectMinMax.setFont(new Font(FONT, Font.PLAIN, 20));
        selectMinMax.setBackground(Color.LIGHT_GRAY);

        selectRandom.setBounds(25, 100, 150, 100);
        selectHeuristic.setBounds(225, 100, 150, 100);
        selectMinMax.setBounds(425, 100, 150, 100);

        frame.add(selectRandom);
        frame.add(selectHeuristic);
        frame.add(selectMinMax);
        frame.add(label1);

        frame.setSize(SELECT_PLAYER_WIDTH,SELECT_PLAYER_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // FRAME IS DONE

        // Give functionality to the buttons. If Game.theseus class variable is equal to the
        // default zero, update theseus variable else minotaurs'. After that, call setBoardDimensions()
        // to proceed. Reminder -> {1: Random, 2: Heuristic, 3: MinMax].
        selectRandom.addActionListener(ae -> {
            if(Game.theseusType == 0){
                Game.theseusType = 1;
                // Refresh the label so that the user knows to set algorithm for minotaur
                label1.setText("SELECT MINOTAUR STYLE");
                frame.repaint();
            }
            else{
                Game.minotaurType = 1;
                // Get rid of the window and proceed.
//                frame.dispose();
                frame.getContentPane().removeAll();
                frame.getContentPane().repaint();
                setBoardDimensions(frame);
            }
        });

        selectHeuristic.addActionListener(ae -> {
            if(Game.theseusType == 0) {
                Game.theseusType = 2;
                label1.setText("SELECT MINOTAUR STYLE");
                frame.repaint();
            }
            else {
                Game.minotaurType = 2;
//                frame.dispose();
                frame.getContentPane().removeAll();
                frame.getContentPane().repaint();
                setBoardDimensions(frame);
            }
        });

        selectMinMax.addActionListener(ae -> {
            if(Game.theseusType == 0) {
                Game.theseusType = 3;
                label1.setText("SELECT MINOTAUR STYLE");
                frame.repaint();
            }
            else {
                Game.minotaurType = 3;
//                frame.dispose();
                frame.getContentPane().removeAll();
                frame.getContentPane().repaint();
                setBoardDimensions(frame);
            }
        });
    }

    // Set the dimensions of the board with a pop up window.
    public void setBoardDimensions(JFrame jFrame){

        JLabel jLabel = new JLabel("Give N as the dimensions of the board");
        jLabel.setBounds(0, 50, 600, 50);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font(FONT, Font.PLAIN, 26));

        JTextField textField = new JTextField("15");
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(new Font(FONT, Font.PLAIN, 18));
        textField.setSelectedTextColor(Color.cyan);
        textField.setBounds(250, 130, 100, 50);

        JButton next = new JButton("Continue");
        next.setFocusable(false);
        next.setBackground(Color.lightGray);
        next.setFont(new Font(FONT, Font.PLAIN, 16));
        next.setBounds(250, 190, 100, 50);
        next.setEnabled(false);

        int input = Integer.parseInt(textField.getText());
        if(input > 0) next.setEnabled(true);

        next.addActionListener(ae -> {
            Game.N = Integer.parseInt(textField.getText());
            Game.W = (Game.N * Game.N * 3 + 1) / 2;
//            jFrame.dispose();
            jFrame.getContentPane().removeAll();
            jFrame.getContentPane().repaint();
            setSuppliesNumber(jFrame);
        });

        jFrame.getContentPane().add(next);
        jFrame.getContentPane().add(textField);
        jFrame.getContentPane().add(jLabel);

        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    // Set the supplies number.
    public void setSuppliesNumber(JFrame jFrame){

        JLabel jLabel = new JLabel("Give the number of supplies");
        jLabel.setBounds(0, 50, 600, 50);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font(FONT, Font.PLAIN, 26));

        JTextField textField = new JTextField("4");
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(new Font(FONT, Font.PLAIN, 18));
        textField.setSelectedTextColor(Color.cyan);
        textField.setBounds(250, 130, 100, 50);

        JButton next = new JButton("Continue");
        next.setFocusable(false);
        next.setFont(new Font(FONT, Font.PLAIN, 16));
        next.setBackground(Color.lightGray);
        next.setBounds(250, 190, 100, 50);
        next.setEnabled(false);

        int input = Integer.parseInt(textField.getText());
        if(input > 0) next.setEnabled(true);

        next.addActionListener(ae -> {
            Game.S = Integer.parseInt(textField.getText());
            jFrame.getContentPane().removeAll();
            jFrame.getContentPane().repaint();
            setMaximumRounds(jFrame);
        });

        jFrame.getContentPane().add(next);
        jFrame.getContentPane().add(textField);
        jFrame.getContentPane().add(jLabel);

        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    // Let the user set the maximum number of rounds to be played
    public void setMaximumRounds(JFrame jFrame){

        JLabel jLabel = new JLabel("Set the maximum rounds to be played");
        jLabel.setBounds(0, 50, 600, 50);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font(FONT, Font.PLAIN, 26));

        JTextField textField = new JTextField("50");
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(new Font(FONT, Font.PLAIN, 18));
        textField.setSelectedTextColor(Color.cyan);
        textField.setBounds(250, 130, 100, 50);

        JButton next = new JButton("Continue");
        next.setFocusable(false);
        next.setBackground(Color.lightGray);
        next.setFont(new Font(FONT, Font.PLAIN, 16));
        next.setBounds(250, 190, 100, 50);
        next.setEnabled(false);

        int input = Integer.parseInt(textField.getText());
        if(input > 0) next.setEnabled(true);

        next.addActionListener(ae -> {
            Game.MAX_ROUNDS = Integer.parseInt(textField.getText());
            jFrame.getContentPane().removeAll();
            jFrame.getContentPane().repaint();
            setTurns(jFrame);
        });

        jFrame.getContentPane().add(next);
        jFrame.getContentPane().add(textField);
        jFrame.getContentPane().add(jLabel);

        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    // Decide whether theseus or minotaur play first.
    public void setTurns(JFrame jFrame){

        JLabel jLabel = new JLabel("Who plays first?");
        jLabel.setBounds(0, 50, 600, 50);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font(FONT, Font.PLAIN, 26));

        JButton theseus = new JButton("Theseus");
        theseus.setFocusable(false);
        theseus.setBackground(Color.lightGray);
        theseus.setFont(new Font(FONT, Font.PLAIN, 20));
        theseus.setBounds(50, 120, 250, 80);
        theseus.setEnabled(true);

        JButton minotaur = new JButton("Minotaur");
        minotaur.setBackground(Color.lightGray);
        minotaur.setFocusable(false);
        minotaur.setFont(new Font(FONT, Font.PLAIN, 20));
        minotaur.setEnabled(true);
        minotaur.setBounds(300, 120, 250, 80);

        // Add functionality to the buttons.
        // The player chosen by the user plays first, meaning that
        // i have to update the firstToPlay: String variable in the Game class.
        theseus.addActionListener(ae -> {
            Game.firstToPlay = "Theseus";
            jFrame.getContentPane().removeAll();
            jFrame.getContentPane().repaint();
            gameScreen(jFrame);
        });

        minotaur.addActionListener(ae -> {
            Game.firstToPlay = "Minotaur";
            jFrame.getContentPane().removeAll();
            jFrame.getContentPane().repaint();
            gameScreen(jFrame);
        });

        jFrame.getContentPane().add(theseus);
        jFrame.getContentPane().add(minotaur);
        jFrame.getContentPane().add(jLabel);

        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    /*
    * Play the game.
    * Generate and draw the board.
    * Control the game flow.
    */

    public void gameScreen(JFrame frame){
        // Declare the board panel of the game
        Game.launch();
        Game.board.repaint();

        // Put the board in JScrollPane
        JScrollPane scrollPane = new JScrollPane(Game.board);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Now create the panel of the buttons
        JPanel buttons = new JPanel(new BorderLayout());

        // Display the round
        JLabel showRoundAndScore = new JLabel("Round : \t" + Game.currentRound + " | \tTheseus score : " + Game.theseus.getSuppliesTheseusCollected());
        showRoundAndScore.setHorizontalAlignment(SwingConstants.CENTER);
        showRoundAndScore.setFocusable(false);
        buttons.add(showRoundAndScore, BorderLayout.NORTH);

        // Press and go to the next round
        JButton nextRound = new JButton("Next Round");
        nextRound.setFocusable(false);
        nextRound.setEnabled(true);
        buttons.add(nextRound, BorderLayout.CENTER);

        // When the game ends make this button enabled
        JButton aContinue = new JButton("Continue");
        aContinue.setFocusable(false);
        aContinue.setEnabled(false);
        buttons.add(aContinue, BorderLayout.WEST);

        nextRound.addActionListener(ae -> {
            // As it is a special case, i need to know if the game will reach
            // the maximum number of rounds after the next
            // round, i have the following if, that checks if we play the last round.
            if(Game.currentRound == Game.MAX_ROUNDS-1){
                // Enable the button to leave the game window and disable the one
                // that plays the next round
                nextRound.setEnabled(false);
                aContinue.setEnabled(true);
                // Play the round
                Game.nextRound();
                // Display the round
                int thisRound = Game.currentRound;
                showRoundAndScore.setText("Round : \t" + thisRound + " | \tTheseus score : " + Game.theseus.getSuppliesTheseusCollected());
                // Repaint the board panel
                Game.board.repaint();
                // Add functionality to the continue button that i enabled before.
                aContinue.addActionListener(aw -> {
                    // Go to the game over screen
                    gameOverScreen();
                    // Return buttons to their initial state.
                    nextRound.setEnabled(true);
                    aContinue.setEnabled(false);
                    // Dispose the window and remove the remnants of this game. Just disposing isn't enough.
                    frame.dispose();
                    frame.remove(scrollPane);
                    frame.remove(buttons);
                });
            }
            // If the game is being played without special cases.
            else {
                Game.board.repaint();
                // Play the next round
                Game.nextRound();
                // Display the round
                showRoundAndScore.setText("Round : \t" + Game.currentRound + " | \tTheseus score : " + Game.theseus.getSuppliesTheseusCollected());
                // Check if after this round the game will end.
                // Below i check if someone won. If someone won, the Game.nextRound() must have
                // encountered one of its terminating/winning conditions and Game.gameOver() has been called and
                // Game.gameOn became false.
                if(!Game.gameOn){
                    aContinue.setEnabled(true);
                    nextRound.setEnabled(false);
                    // Display round
                    int thisRound = Game.currentRound+1; // Increase by one because as the game ended,
                    // it didn't get updated in the Game.nextRound().
                    showRoundAndScore.setText("Round : \t" + thisRound + " | \tTheseus score : " + Game.theseus.getSuppliesTheseusCollected());
                    // Add functionality
                    aContinue.addActionListener(aw -> {
                        frame.dispose();
                        gameOverScreen();
                        nextRound.setEnabled(true);
                        aContinue.setEnabled(false);
                        frame.remove(scrollPane);
                        frame.remove(buttons);
                    });
                }
            }
            Game.board.repaint();
        });

        // Press and start a new game
        JButton newGame = new JButton("New Game");
        newGame.setFocusable(false);
        buttons.add(newGame, BorderLayout.EAST);
        newGame.addActionListener(ae -> {
            frame.dispose();
            frame.getContentPane().removeAll();
            frame.getContentPane().repaint();
            Game.newGame();
        });

        // Update the layout of the frame as we need borderlayout instead of the current null
        frame.setLayout(new BorderLayout());

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttons, BorderLayout.SOUTH);

        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // When the game ends, create a new window with play again option and a nice image display.
    public void gameOverScreen(){
        JFrame frame = new JFrame(TITLE);

        // Display a different image at every possible outcome.
        // Declare the variables.
        JLabel label = new JLabel();
        ImageIcon imageIcon;
        // Based on the winner initialize them to the appropriate message and image.
        if(Game.winner == -1){
            label.setText("It's a draw");
            imageIcon = new ImageIcon(DRAW_PNG);
        }
        else if(Game.winner == 1){
            label.setText("All supplies collected. Theseus wins!!!");
            imageIcon = new ImageIcon(THESEUS_WON_PNG);
        }
        else{
            label.setText("Minotaur killed Theseus. Minotaur wins!!!");
            imageIcon = new ImageIcon(MINO_WON_PNG);
        }

        // Finalize the look of the label with the text and image positions and alignments.
        label.setIcon(imageIcon);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.TOP);
        label.setForeground(Color.black);
        label.setFont(new Font(FONT, Font.PLAIN, 32));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.NORTH);


        // Play again
        JButton play = new JButton("Play again");
        play.setFont(new Font(FONT, Font.BOLD, 20));
        play.setBackground(Color.lightGray);
        play.setBounds(200, 460, 300, 80);
        play.addActionListener(ae -> {
            frame.dispose();
            Game.newGame();
        });

        frame.getContentPane().setBackground(Color.cyan);
        frame.getContentPane().add(play);
        frame.getContentPane().add(label);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(new Dimension(700, 600));
        frame.setLocationRelativeTo(null);
    }
}
