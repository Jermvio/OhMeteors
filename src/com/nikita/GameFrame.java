package com.nikita;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {

    private static GameFrame gm;
    private static GameField gameField;

    public static void main(String[] args) {
        gm = new GameFrame();
        gm.startGame();
    }

    private GameFrame () {
        setSize(400, 650);
        setLocation(500, 200);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        gameField = GameField.getInstance(this);

    }

    public void startGame() {
        add(gameField);
        gameField.startGame();
    }
}
