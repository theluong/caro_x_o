package caro.ui;

import caro.config.GameConfig;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class CellButton extends JButton {
    private String symbol = "";
    private int cellSize;

    public CellButton(int cellSize) {
        this.cellSize = cellSize;
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setBackground(GameConfig.BOARD_COLOR);
        applyCellSize(cellSize);
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        repaint();
    }

    public void setHighlighted(boolean highlighted) {
        setBackground(highlighted ? GameConfig.LAST_MOVE_HIGHLIGHT_COLOR : GameConfig.BOARD_COLOR);
        repaint();
    }

    public String getSymbol() {
        return symbol;
    }

    public void applyCellSize(int cellSize) {
        this.cellSize = cellSize;
        Dimension dimension = new Dimension(cellSize, cellSize);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        int fontSize = Math.max(12, cellSize / 2);
        setFont(new Font("SansSerif", Font.BOLD, fontSize));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (symbol.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if ("X".equals(symbol)) {
            g2.setColor(GameConfig.X_COLOR);
            g2.setStroke(new BasicStroke(Math.max(2.0f, cellSize / 10.0f)));
            int padding = Math.max(3, Math.min(getWidth(), getHeight()) / 5);
            g2.drawLine(padding, padding, getWidth() - padding, getHeight() - padding);
            g2.drawLine(getWidth() - padding, padding, padding, getHeight() - padding);
        } else if ("O".equals(symbol)) {
            g2.setColor(GameConfig.O_COLOR);
            g2.setStroke(new BasicStroke(Math.max(2.0f, cellSize / 10.0f)));
            int padding = Math.max(3, Math.min(getWidth(), getHeight()) / 5);
            g2.drawOval(padding, padding, getWidth() - 2 * padding, getHeight() - 2 * padding);
        } else {
            g2.setColor(Color.BLACK);
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int x = (getWidth() - metrics.stringWidth(symbol)) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString(symbol, x, y);
        }

        g2.dispose();
    }
}
