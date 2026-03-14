import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToe {
    int boardWidth = 600;
    int boardHeight = 650;

    //Hauptfenster
    JFrame frame = new JFrame("Tic-Tac-Toe");
    JLabel textLabel = new JLabel();
    JLabel scoreLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    // Spielfeld
    JButton[][] board = new JButton[3][3];
    JButton resetButton = new JButton("Neues Spiel");

    //Spieler
    String playerX = "X";
    String playerO = "O";
    String currentPlayer = playerX;
    String lastWinner = null;
    String lastStarter = null;

    //Spielstatus
    boolean gameOver = false;
    int turns = 0;

    //Punktestand
    int scoreX = 0;
    int scoreO = 0;

    TicTacToe() {
        //Fenster Einstellungen
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //Kopfzeile
        textLabel.setBackground(Color.darkGray);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial", Font.BOLD, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic-Tac-Toe");
        textLabel.setOpaque(true);

        //Punktestand Label
        scoreLabel.setBackground(Color.darkGray);
        scoreLabel.setForeground(Color.white);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 25));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("X: 0  |  O: 0");
        scoreLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        textPanel.add(scoreLabel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);

        // Spielfeld
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.darkGray);
        boardPanel.setOpaque(true);
        frame.add(boardPanel);

        //Reset Button
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setOpaque(true);
        resetButton.setFont(new Font("Arial", Font.BOLD, 20));
        resetButton.setFocusable(false);
        resetButton.setVisible(false);
        resetButton.setBackground(Color.darkGray); // Hintergrundfarbe
        resetButton.setForeground(Color.black); // Textfarbe
        resetButton.setOpaque(true); // Für macOS notwendig

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        buttonPanel.add(resetButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Startspieler für die erste Runde merken
        lastStarter = currentPlayer;

        //Spielfeld erstellen (3x3)
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = new JButton();
                board[r][c] = tile;
                boardPanel.add(tile);

                //Buttondesign
                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.black);
                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setFocusable(false);
                tile.setOpaque(true);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (gameOver) return;
                        JButton tile = (JButton) e.getSource();
                        if (tile.getText() == "") {
                            tile.setText(currentPlayer);
                            turns++;
                            checkWinner();
                            if(!gameOver) {
                                currentPlayer = currentPlayer == playerX ? playerO : playerX;
                                textLabel.setText(currentPlayer + " ist am Zug");
                            }
                        }
                    }
                });
            }
        }
    }

    void checkWinner() {
        //horizontal
        for (int r = 0; r < 3; r++) {
            if (board[r][0].getText() == "") continue;

            if (board[r][0].getText() == board[r][1].getText() &&
                board[r][1].getText() == board[r][2].getText()) {
                for (int i = 0; i < 3; i++) {
                    setWinner(board[r][i]);
                }
                // Punktestand erhöhen (nur einmal)
                if (currentPlayer.equals(playerX)) {
                    scoreX++;
                } else {
                    scoreO++;
                }
                updateScoreLabel();
                gameOver = true;
                return;
            }
        }
        //vertical
        for (int c = 0; c < 3; c++) {
            if (board[0][c].getText() == "") continue;

            if (board[0][c].getText() == board[1][c].getText() &&
                board[1][c].getText() == board[2][c].getText()) {
                for (int i = 0; i < 3; i++) {
                    setWinner(board[i][c]);
                }
                // Punktestand erhöhen
                if (currentPlayer.equals(playerX)) {
                    scoreX++;
                } else {
                    scoreO++;
                }
                updateScoreLabel();
                gameOver = true;
                return;
            }
        }
        //diagonal
        if (board[0][0].getText() == board [1][1].getText() &&
            board[1][1].getText() == board[2][2].getText() &&
            board[0][0].getText() != "") {
            for (int i = 0; i < 3; i++) {
                setWinner(board[i][i]);
            }
            // Punktestand erhöhen
            if (currentPlayer.equals(playerX)) {
                scoreX++;
            } else {
                scoreO++;
            }
            updateScoreLabel();
            gameOver = true;
            return;
        }
        //anti diagonal
        if (board[0][2].getText() == board [1][1].getText() &&
            board[1][1].getText() == board[2][0].getText() &&
            board[0][2].getText() != "") {
            for (int i = 0; i < 3; i++) {
                setWinner(board[i][2 - i]);
            }
            // Punktestand erhöhen
            if (currentPlayer.equals(playerX)) {
                scoreX++;
            } else {
                scoreO++;
            }
            updateScoreLabel();
            gameOver = true;
            return;
        }
        //draw
        if (turns == 9) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    setTie(board[r][c]);
                }
            }
            gameOver = true;
        }
    }
    void setWinner(JButton tile) {
        tile.setForeground(Color.green);
        textLabel.setText (currentPlayer + " gewinnt!");
        lastWinner = currentPlayer;
        resetButton.setVisible(true);
    }
    void setTie(JButton tile) {
        tile.setBackground(Color.darkGray);
        tile.setForeground(Color.orange);
        textLabel.setText("Unentschieden!");
        lastWinner = null;
        resetButton.setVisible(true);
    }

    void updateScoreLabel() {
        scoreLabel.setText("X: " + scoreX + "  |  O: " + scoreO);
    }

    void resetGame() {
        // Verlierer beginnt im nächsten Spiel, bei Unentschieden der jeweils andere
        if (lastWinner != null) {
            currentPlayer = lastWinner.equals(playerX) ? playerO : playerX;
        } else if (lastStarter != null) {
            currentPlayer = lastStarter.equals(playerX) ? playerO : playerX;
        } else {
            currentPlayer = playerX;
        }
        lastStarter = currentPlayer;

        // Neues Spiel
        gameOver = false;
        turns = 0;
        textLabel.setText("Tic-Tac-Toe");
        resetButton.setVisible(false);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c].setText("");
                board[r][c].setBackground(Color.darkGray);
                board[r][c].setForeground(Color.black);
            }
        }
    }
}