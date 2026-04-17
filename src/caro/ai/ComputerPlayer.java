package caro.ai;

import caro.config.GameConfig;
import caro.model.Difficulty;
import caro.model.GameState;
import caro.model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ComputerPlayer {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public Move chooseMove(GameState gameState, Difficulty difficulty) {
        List<Move> candidates = gameState.collectCandidateMoves();
        if (candidates.isEmpty()) {
            int center = gameState.getBoardSize() / 2;
            return new Move(center, center);
        }

        if (difficulty == Difficulty.EASY) {
            return candidates.get(random.nextInt(candidates.size()));
        }

        List<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        String computerSymbol = gameState.getComputerSymbol();

        for (Move move : candidates) {
            int score = evaluateMove(gameState, move, computerSymbol, difficulty == Difficulty.HARD);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        if (difficulty == Difficulty.MEDIUM && bestMoves.size() > 1) {
            return bestMoves.get(random.nextInt(Math.min(3, bestMoves.size())));
        }
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int evaluateMove(GameState gameState, Move move, String symbol, boolean hardMode) {
        gameState.setCell(move.getRow(), move.getCol(), symbol);
        if (gameState.checkWinner(symbol) != null) {
            gameState.setCell(move.getRow(), move.getCol(), "");
            return 1_000_000;
        }

        int attackScore = evaluatePosition(gameState, move, symbol, hardMode);
        gameState.setCell(move.getRow(), move.getCol(), "");

        String opponent = "X".equals(symbol) ? "O" : "X";
        gameState.setCell(move.getRow(), move.getCol(), opponent);
        int defenseScore = gameState.checkWinner(opponent) != null
                ? 900_000
                : evaluatePosition(gameState, move, opponent, hardMode);
        gameState.setCell(move.getRow(), move.getCol(), "");

        int boardSize = gameState.getBoardSize();
        int centerBias = boardSize
                - (Math.abs(move.getRow() - boardSize / 2) + Math.abs(move.getCol() - boardSize / 2));
        return attackScore + defenseScore * (hardMode ? 2 : 1) + centerBias;
    }

    private int evaluatePosition(GameState gameState, Move move, String symbol, boolean hardMode) {
        int totalScore = 0;
        int[][] directions = {
                {0, 1},
                {1, 0},
                {1, 1},
                {1, -1}
        };

        for (int[] direction : directions) {
            int countForward = countDirection(gameState, move.getRow(), move.getCol(), direction[0], direction[1], symbol);
            int countBackward = countDirection(gameState, move.getRow(), move.getCol(), -direction[0], -direction[1], symbol);
            int total = countForward + countBackward + 1;
            int openEnds = countOpenEnds(gameState, move.getRow(), move.getCol(), direction[0], direction[1], countForward, countBackward);
            totalScore += scorePattern(total, openEnds, hardMode);
        }

        return totalScore;
    }

    private int countDirection(GameState gameState, int row, int col, int rowStep, int colStep, String symbol) {
        int count = 0;
        int currentRow = row + rowStep;
        int currentCol = col + colStep;

        while (gameState.isInsideBoard(currentRow, currentCol) && symbol.equals(gameState.getCell(currentRow, currentCol))) {
            count++;
            currentRow += rowStep;
            currentCol += colStep;
        }

        return count;
    }

    private int countOpenEnds(GameState gameState, int row, int col, int rowStep, int colStep, int countForward, int countBackward) {
        int openEnds = 0;

        int forwardRow = row + (countForward + 1) * rowStep;
        int forwardCol = col + (countForward + 1) * colStep;
        if (gameState.isInsideBoard(forwardRow, forwardCol) && gameState.getCell(forwardRow, forwardCol).isEmpty()) {
            openEnds++;
        }

        int backwardRow = row - (countBackward + 1) * rowStep;
        int backwardCol = col - (countBackward + 1) * colStep;
        if (gameState.isInsideBoard(backwardRow, backwardCol) && gameState.getCell(backwardRow, backwardCol).isEmpty()) {
            openEnds++;
        }

        return openEnds;
    }

    private int scorePattern(int total, int openEnds, boolean hardMode) {
        if (total >= GameConfig.WIN_CONDITION) {
            return 500_000;
        }
        if (total == 4 && openEnds == 2) {
            return hardMode ? 120_000 : 70_000;
        }
        if (total == 4 && openEnds == 1) {
            return hardMode ? 45_000 : 20_000;
        }
        if (total == 3 && openEnds == 2) {
            return hardMode ? 15_000 : 7_000;
        }
        if (total == 3 && openEnds == 1) {
            return hardMode ? 4_000 : 1_800;
        }
        if (total == 2 && openEnds == 2) {
            return hardMode ? 1_500 : 700;
        }
        if (total == 2 && openEnds == 1) {
            return 300;
        }
        if (total == 1 && openEnds == 2) {
            return 80;
        }
        return 10;
    }
}
