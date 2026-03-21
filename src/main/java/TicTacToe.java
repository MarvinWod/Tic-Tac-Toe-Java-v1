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

    // Modus: true = gegen Computer, false = 2 Spieler
    boolean vsComputer = false;

    //Spielstatus
    boolean gameOver = false;
    int turns = 0;

    //Punktestand
    int scoreX = 0;
    int scoreO = 0;

    // Steuert, ob der Spieler ziehen darf
    boolean playerCanMove = true;

    // Schwierigkeitsgrad: 0 = Leicht, 1 = Mittel, 2 = Schwer
    int aiDifficulty = 0;
    
    // Timer für KI-Züge (um ihn später zu stoppen)
    Timer aiTimer = null;

    TicTacToe() {
        // Menüleiste mit Modus-Auswahl
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menü");
        JMenuItem modeItem = new JMenuItem("Modus wählen");
        modeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showModeDialog();
                scoreX = 0;
                scoreO = 0;
                updateScoreLabel();
                resetGame();
            }
        });
        menu.add(modeItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Modus-Auswahl beim Start
        showModeDialog();
        scoreX = 0;
        scoreO = 0;
        updateScoreLabel();
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
                        if (tile.getText().isEmpty() && playerCanMove) {
                            tile.setText(currentPlayer);
                            turns++;
                            checkWinner();
                            if (!gameOver) {
                                currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                                textLabel.setText(currentPlayer + " ist am Zug");
                                // KI-Zug, falls aktiviert und jetzt Computer am Zug
                                if (vsComputer && currentPlayer.equals(playerO)) {
                                    computerMoveWithDelay();
                                }
                            }
                        }
                    }
                });
            }
        }

        // Falls Computer beginnt
        if (vsComputer && currentPlayer.equals(playerO)) {
            computerMoveWithDelay();
        }
    }

    // Zeigt den Modus-Auswahldialog an und setzt den Modus
    void showModeDialog() {
        Object[] options = {"2 Spieler", "Gegen Computer"};
        int n = JOptionPane.showOptionDialog(
                frame,
                "Wähle den Spielmodus",
                "Spielmodus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        
        // Wenn Dialog geschlossen wurde, Anwendung beenden
        if (n == JOptionPane.CLOSED_OPTION) {
            if (aiTimer != null) {
                aiTimer.stop();
            }
            System.exit(0);
        }
        
        vsComputer = (n == 1);
        if (vsComputer) {
            Object[] diffOptions = {"Leicht", "Mittel", "Schwer"};
            int diff = JOptionPane.showOptionDialog(
                    frame,
                    "Wähle den Schwierigkeitsgrad",
                    "Schwierigkeitsgrad",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    diffOptions,
                    diffOptions[0]
            );
            
            // Wenn Dialog geschlossen wurde, Anwendung beenden
            if (diff == JOptionPane.CLOSED_OPTION) {
                if (aiTimer != null) {
                    aiTimer.stop();
                }
                System.exit(0);
            }
            
            aiDifficulty = diff; // 0=Leicht, 1=Mittel, 2=Schwer
        }
    }

    // Einfache KI: wählt zufällig ein freies Feld
    void computerMove() {
        if (gameOver) return;
        if (aiDifficulty == 0) {
            // Leicht: Zufälliger legaler Zug
            randomAIMove();
        } else if (aiDifficulty == 1) {
            // Mittel: Minimax mit Tiefe 2
            Point move = minimaxMove(currentPlayer, 2);
            if (move != null) {
                board[move.x][move.y].setText(currentPlayer);
            } else {
                randomAIMove();
            }
            turns++;
            checkWinner();
            if (!gameOver) {
                currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                textLabel.setText(currentPlayer + " ist am Zug");
            }
        } else {
            // Schwer: Vollständiger Minimax mit Alpha-Beta-Pruning
            Point move = minimaxMove(currentPlayer, 9);
            if (move != null) {
                board[move.x][move.y].setText(currentPlayer);
            } else {
                randomAIMove();
            }
            turns++;
            checkWinner();
            if (!gameOver) {
                currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                textLabel.setText(currentPlayer + " ist am Zug");
            }
        }
    }

    // Zufälliger Zug für leichte KI
    void randomAIMove() {
        java.util.List<Point> freieFelder = new java.util.ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c].getText().isEmpty()) {
                    freieFelder.add(new Point(r, c));
                }
            }
        }
        if (freieFelder.isEmpty()) return;
        Point zug = freieFelder.get((int)(Math.random() * freieFelder.size()));
        board[zug.x][zug.y].setText(currentPlayer);
        turns++;
        checkWinner();
        if (!gameOver) {
            currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
            textLabel.setText(currentPlayer + " ist am Zug");
        }
    }

    // Minimax-Algorithmus mit begrenzter Tiefe
    Point minimaxMove(String symbol, int maxDepth) {
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c].getText().isEmpty()) {
                    board[r][c].setText(symbol);
                    int score = minimax(symbol, 0, false, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[r][c].setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Point(r, c);
                    }
                }
            }
        }
        return bestMove;
    }

    // Minimax mit Alpha-Beta-Pruning
    int minimax(String symbol, int depth, boolean isMax, int maxDepth, int alpha, int beta) {
        String winner = getWinner();
        if (winner != null) {
            if (winner.equals(symbol)) return 10 - depth;
            else if (winner.equals(currentPlayer.equals(playerX) ? playerO : playerX)) return depth - 10;
            else return 0;
        }
        if (depth >= maxDepth) return 0;
        if (isBoardFull()) return 0;
        String nextSymbol = isMax ? symbol : (symbol.equals(playerX) ? playerO : playerX);
        int best;
        if (isMax) {
            best = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c].getText().isEmpty()) {
                        board[r][c].setText(nextSymbol);
                        int score = minimax(symbol, depth + 1, false, maxDepth, alpha, beta);
                        board[r][c].setText("");
                        best = Math.max(best, score);
                        alpha = Math.max(alpha, best);
                        if (beta <= alpha) break;
                    }
                }
            }
        } else {
            best = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c].getText().isEmpty()) {
                        board[r][c].setText(nextSymbol);
                        int score = minimax(symbol, depth + 1, true, maxDepth, alpha, beta);
                        board[r][c].setText("");
                        best = Math.min(best, score);
                        beta = Math.min(beta, best);
                        if (beta <= alpha) break;
                    }
                }
            }
        }
        return best;
    }

    // Hilfsmethode: Gibt Gewinner zurück ("X", "O" oder null)
    String getWinner() {
        for (int r = 0; r < 3; r++) {
            if (!board[r][0].getText().isEmpty() &&
                board[r][0].getText().equals(board[r][1].getText()) &&
                board[r][1].getText().equals(board[r][2].getText())) {
                return board[r][0].getText();
            }
        }
        for (int c = 0; c < 3; c++) {
            if (!board[0][c].getText().isEmpty() &&
                board[0][c].getText().equals(board[1][c].getText()) &&
                board[1][c].getText().equals(board[2][c].getText())) {
                return board[0][c].getText();
            }
        }
        if (!board[0][0].getText().isEmpty() &&
            board[0][0].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][2].getText())) {
            return board[0][0].getText();
        }
        if (!board[0][2].getText().isEmpty() &&
            board[0][2].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][0].getText())) {
            return board[0][2].getText();
        }
        if (isBoardFull()) return "draw";
        return null;
    }

    boolean isBoardFull() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c].getText().isEmpty()) return false;
            }
        }
        return true;
    }

    void checkWinner() {
        //horizontal
        for (int r = 0; r < 3; r++) {
            if (board[r][0].getText().isEmpty()) continue;

            if (board[r][0].getText().equals(board[r][1].getText()) &&
                board[r][1].getText().equals(board[r][2].getText())) {
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
            if (board[0][c].getText().isEmpty()) continue;

            if (board[0][c].getText().equals(board[1][c].getText()) &&
                board[1][c].getText().equals(board[2][c].getText())) {
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
        if (board[0][0].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][2].getText()) &&
            !board[0][0].getText().isEmpty()) {
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
        if (board[0][2].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][0].getText()) &&
            !board[0][2].getText().isEmpty()) {
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

        // Nach Reset: Spieler darf ziehen, außer die KI beginnt
        playerCanMove = !(vsComputer && currentPlayer.equals(playerO));

        // Falls Computer beginnt
        if (vsComputer && currentPlayer.equals(playerO)) {
            computerMoveWithDelay();
        }
    }

    // Führt den KI-Zug mit Verzögerung aus
    void computerMoveWithDelay() {
        // Vorherigen Timer stoppen falls noch aktiv
        if (aiTimer != null) {
            aiTimer.stop();
        }
        
        playerCanMove = false; // Spieler darf nicht ziehen, solange die KI "überlegt"
        aiTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ((Timer)evt.getSource()).stop();
                computerMove();
                if (!gameOver) {
                    playerCanMove = true; // Nach KI-Zug wieder erlauben
                }
            }
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }
}