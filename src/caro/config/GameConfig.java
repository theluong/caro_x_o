package caro.config;

import java.awt.Color;

public final class GameConfig {
    public static final int WIN_CONDITION = 5;
    public static final Color X_COLOR = new Color(220, 53, 69);
    public static final Color O_COLOR = new Color(13, 110, 253);
    public static final Color BOARD_COLOR = Color.WHITE;
    public static final Color LAST_MOVE_HIGHLIGHT_COLOR = new Color(255, 243, 205);
    public static final int BOARD_CELL_SIZE = 18;
    public static final int FRAME_WIDTH = 1100;
    public static final int FRAME_HEIGHT = 850;

    private GameConfig() {
    }
}
