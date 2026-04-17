package caro.app;

import caro.ui.GameFrame;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().show());
    }
}
