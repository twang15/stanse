package cz.muni.stanse.gui;

import javax.swing.JTextArea;

final class GuiConsoleManager {

    // package-private section

    GuiConsoleManager(final javax.swing.JTextArea consoleTextArea) {
        this.consoleTextArea = consoleTextArea;
    }

    void clear() {
        getConsoleTextArea().setText("");
    }

    void appendText(final String text) {
        getConsoleTextArea().append(text + '\n');
    }

    // private section

    private JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    private final javax.swing.JTextArea consoleTextArea;
}