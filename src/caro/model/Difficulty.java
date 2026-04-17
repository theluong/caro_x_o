package caro.model;

public enum Difficulty {
    EASY("Dễ", 500),
    MEDIUM("Trung bình", 1_000),
    HARD("Khó", 1_200);

    private final String label;
    private final int computerDelayMs;

    Difficulty(String label, int computerDelayMs) {
        this.label = label;
        this.computerDelayMs = computerDelayMs;
    }

    public int getComputerDelayMs() {
        return computerDelayMs;
    }

    @Override
    public String toString() {
        return label;
    }
}
