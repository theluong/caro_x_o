package caro.ui;

import caro.ai.ComputerPlayer;
import caro.config.GameConfig;
import caro.model.BoardSize;
import caro.model.Difficulty;
import caro.model.GameOptions;
import caro.model.GameState;
import caro.model.Move;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class GameFrame {
    private final JFrame frame;
    private final JLabel statusLabel;
    private final GameState gameState;
    private final ComputerPlayer computerPlayer;
    private final JPanel boardPanel;
    private final JScrollPane boardScrollPane;

    private CellButton[][] buttons;
    private Difficulty difficulty;
    private BoardSize boardSize;
    private boolean waitingForComputer;
    private Timer computerMoveTimer;
    private Move lastMove;

    public GameFrame() {
        frame = new JFrame("X & O");
        statusLabel = new JLabel("", SwingConstants.CENTER);
        gameState = new GameState();
        computerPlayer = new ComputerPlayer();
        boardPanel = new JPanel();
        boardScrollPane = new JScrollPane(boardPanel);
        difficulty = Difficulty.MEDIUM;
        boardSize = BoardSize.MEDIUM;

        initializeLayout();
        configureFrame();
        startNewGameWithDialog();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void initializeLayout() {
        frame.setLayout(new BorderLayout());
        frame.add(createTopPanel(), BorderLayout.NORTH);
        frame.add(boardScrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton newGameButton = new JButton("Ván mới");
        newGameButton.addActionListener(event -> startNewGameWithDialog());

        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.add(newGameButton, BorderLayout.EAST);
        return topPanel;
    }

    private void configureFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GameConfig.FRAME_WIDTH, GameConfig.FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
    }

    private void startNewGameWithDialog() {
        GameOptions options = promptGameOptions();
        if (options == null) {
            if (!frame.isVisible()) {
                frame.dispose();
            }
            return;
        }

        difficulty = options.getDifficulty();
        boardSize = options.getBoardSize();
        resetBoard(options);
    }

    private GameOptions promptGameOptions() {
        String[] symbolOptions = {"X", "O"};
        String selectedSymbol = (String) JOptionPane.showInputDialog(
                frame,
                "Chọn quân bạn muốn đánh:",
                "Chọn quân",
                JOptionPane.QUESTION_MESSAGE,
                null,
                symbolOptions,
                gameState.getPlayerSymbol()
        );
        if (selectedSymbol == null) {
            return null;
        }

        Difficulty selectedDifficulty = (Difficulty) JOptionPane.showInputDialog(
                frame,
                "Chọn độ khó của máy:",
                "Độ khó",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Difficulty.values(),
                difficulty
        );
        if (selectedDifficulty == null) {
            return null;
        }

        BoardSize selectedBoardSize = (BoardSize) JOptionPane.showInputDialog(
                frame,
                "Chọn kích thước bàn cờ:",
                "Kích thước bàn cờ",
                JOptionPane.QUESTION_MESSAGE,
                null,
                BoardSize.values(),
                boardSize
        );
        if (selectedBoardSize == null) {
            return null;
        }

        return new GameOptions(selectedSymbol, selectedDifficulty, selectedBoardSize);
    }

    private void resetBoard(GameOptions options) {
        stopComputerTimer();
        waitingForComputer = false;
        lastMove = null;
        gameState.reset(options);
        rebuildBoardGrid();

        if (gameState.getCurrentTurn().equals(gameState.getPlayerSymbol())) {
            updateStatus("Lượt của bạn (" + gameState.getPlayerSymbol() + ") - Độ khó: " + difficulty + " - Bàn cờ: " + boardSize);
        } else {
            scheduleComputerMove();
        }
    }

    private void handlePlayerMove(int row, int col) {
        if (gameState.isGameOver()
                || waitingForComputer
                || !gameState.getCurrentTurn().equals(gameState.getPlayerSymbol())
                || !gameState.getCell(row, col).isEmpty()) {
            return;
        }

        applyMove(new Move(row, col), gameState.getPlayerSymbol());
        if (!finishTurn(gameState.getPlayerSymbol())) {
            gameState.setCurrentTurn(gameState.getComputerSymbol());
            scheduleComputerMove();
        }
    }

    private void applyMove(Move move, String symbol) {
        if (gameState.makeMove(move.getRow(), move.getCol(), symbol)) {
            clearLastMoveHighlight();
            buttons[move.getRow()][move.getCol()].setSymbol(symbol);
            buttons[move.getRow()][move.getCol()].setHighlighted(true);
            lastMove = move;
        }
    }

    private boolean finishTurn(String symbol) {
        String winner = gameState.checkWinner(symbol);
        if (winner != null) {
            gameState.setGameOver(true);
            waitingForComputer = false;
            updateStatus("Kết thúc ván");
            JOptionPane.showMessageDialog(frame, winner + " thắng!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetBoard(new GameOptions(gameState.getPlayerSymbol(), difficulty, boardSize));
            return true;
        }

        if (gameState.isBoardFull()) {
            gameState.setGameOver(true);
            waitingForComputer = false;
            updateStatus("Kết thúc ván");
            JOptionPane.showMessageDialog(frame, "Hòa!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetBoard(new GameOptions(gameState.getPlayerSymbol(), difficulty, boardSize));
            return true;
        }

        return false;
    }

    private void scheduleComputerMove() {
        waitingForComputer = true;
        updateStatus("Máy (" + gameState.getComputerSymbol() + ") sẽ đánh sau " + (difficulty.getComputerDelayMs() / 1000) + " giây - Độ khó: " + difficulty + " - Bàn cờ: " + boardSize);

        stopComputerTimer();
        computerMoveTimer = new Timer(difficulty.getComputerDelayMs(), event -> {
            waitingForComputer = false;
            playComputerMove();
        });
        computerMoveTimer.setRepeats(false);
        computerMoveTimer.start();
    }

    private void playComputerMove() {
        if (gameState.isGameOver() || !gameState.getCurrentTurn().equals(gameState.getComputerSymbol())) {
            return;
        }

        Move move = computerPlayer.chooseMove(gameState, difficulty);
        applyMove(move, gameState.getComputerSymbol());
        if (!finishTurn(gameState.getComputerSymbol())) {
            gameState.setCurrentTurn(gameState.getPlayerSymbol());
            updateStatus("Lượt của bạn (" + gameState.getPlayerSymbol() + ") - Độ khó: " + difficulty + " - Bàn cờ: " + boardSize);
        }
    }

    private void stopComputerTimer() {
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            computerMoveTimer.stop();
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void rebuildBoardGrid() {
        int size = gameState.getBoardSize();
        int cellSize = boardSize.getCellSize();
        buttons = new CellButton[size][size];

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(size, size));

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                final int currentRow = row;
                final int currentCol = col;

                CellButton button = new CellButton(cellSize);
                button.addActionListener(event -> handlePlayerMove(currentRow, currentCol));
                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        int boardPixelSize = cellSize * size;
        boardPanel.setPreferredSize(new Dimension(boardPixelSize, boardPixelSize));
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void clearLastMoveHighlight() {
        if (lastMove == null) {
            return;
        }

        int row = lastMove.getRow();
        int col = lastMove.getCol();
        if (row >= 0 && row < buttons.length && col >= 0 && col < buttons[row].length) {
            buttons[row][col].setHighlighted(false);
        }
    }
}
