package caro.model;

public class GameOptions {
    private final String playerSymbol;
    private final Difficulty difficulty;
    private final BoardSize boardSize;

    public GameOptions(String playerSymbol, Difficulty difficulty, BoardSize boardSize) {
        this.playerSymbol = playerSymbol;
        this.difficulty = difficulty;
        this.boardSize = boardSize;
    }

    public String getPlayerSymbol() {
        return playerSymbol;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public BoardSize getBoardSize() {
        return boardSize;
    }

    public String computerSymbol() {
        return "X".equals(playerSymbol) ? "O" : "X";
    }
}
