package caro.model;

import caro.config.GameConfig;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private String[][] board;
    private int boardSize;
    private String currentTurn;
    private String playerSymbol;
    private String computerSymbol;
    private boolean gameOver;

    public GameState() {
        reset(new GameOptions("X", Difficulty.MEDIUM, BoardSize.MEDIUM));
    }

    public void reset(GameOptions options) {
        playerSymbol = options.getPlayerSymbol();
        computerSymbol = options.computerSymbol();
        boardSize = options.getBoardSize().getSize();
        board = new String[boardSize][boardSize];
        currentTurn = "X";
        gameOver = false;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                board[row][col] = "";
            }
        }
    }

    public boolean makeMove(int row, int col, String symbol) {
        if (gameOver || !isInsideBoard(row, col) || !board[row][col].isEmpty()) {
            return false;
        }
        board[row][col] = symbol;
        return true;
    }

    public String checkWinner(String symbol) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col <= boardSize - GameConfig.WIN_CONDITION; col++) {
                if (hasWinningLine(row, col, 0, 1, symbol)) {
                    return symbol;
                }
            }
        }

        for (int row = 0; row <= boardSize - GameConfig.WIN_CONDITION; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (hasWinningLine(row, col, 1, 0, symbol)) {
                    return symbol;
                }
            }
        }

        for (int row = 0; row <= boardSize - GameConfig.WIN_CONDITION; row++) {
            for (int col = 0; col <= boardSize - GameConfig.WIN_CONDITION; col++) {
                if (hasWinningLine(row, col, 1, 1, symbol)) {
                    return symbol;
                }
            }
        }

        for (int row = 0; row <= boardSize - GameConfig.WIN_CONDITION; row++) {
            for (int col = GameConfig.WIN_CONDITION - 1; col < boardSize; col++) {
                if (hasWinningLine(row, col, 1, -1, symbol)) {
                    return symbol;
                }
            }
        }

        return null;
    }

    public boolean isBoardFull() {
        for (String[] row : board) {
            for (String cell : row) {
                if (cell.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Move> collectCandidateMoves() {
        List<Move> candidates = new ArrayList<>();
        if (!hasAnyPiece()) {
            return candidates;
        }

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col].isEmpty() && hasNeighbor(row, col, 2)) {
                    candidates.add(new Move(row, col));
                }
            }
        }
        return candidates;
    }

    public boolean hasNeighbor(int row, int col, int radius) {
        for (int rowOffset = -radius; rowOffset <= radius; rowOffset++) {
            for (int colOffset = -radius; colOffset <= radius; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                int newRow = row + rowOffset;
                int newCol = col + colOffset;
                if (isInsideBoard(newRow, newCol) && !board[newRow][newCol].isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public String getCell(int row, int col) {
        return board[row][col];
    }

    public void setCell(int row, int col, String symbol) {
        board[row][col] = symbol;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getPlayerSymbol() {
        return playerSymbol;
    }

    public String getComputerSymbol() {
        return computerSymbol;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    private boolean hasAnyPiece() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!board[row][col].isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasWinningLine(int startRow, int startCol, int rowStep, int colStep, String symbol) {
        for (int offset = 0; offset < GameConfig.WIN_CONDITION; offset++) {
            int row = startRow + offset * rowStep;
            int col = startCol + offset * colStep;
            if (!symbol.equals(board[row][col])) {
                return false;
            }
        }
        return true;
    }
}
