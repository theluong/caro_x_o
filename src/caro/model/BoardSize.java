package caro.model;

public enum BoardSize {
    SMALL(15, "15x15", 25),
    MEDIUM(30, "30x30", 25),
    LARGE(50, "50x50", 25);

    private final int size;
    private final String label;
    private final int cellSize;

    BoardSize(int size, String label, int cellSize) {
        this.size = size;
        this.label = label;
        this.cellSize = cellSize;
    }

    public int getSize() {
        return size;
    }

    public int getCellSize() {
        return cellSize;
    }

    @Override
    public String toString() {
        return label;
    }
}
