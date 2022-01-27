package com.nikita;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameField extends JPanel implements ActionListener {

    private static GameFrame gm;
    private static GameField instance;
    private final int fieldWidth;
    private final int fieldHeight;
    private ArrayList<GameObjectMeteor> activeMeteorsList;
    private int shipStep = 5;
    private final int shipSize = 25;
    private int shipOffsetX;
    private int shipOffsetY;
    private int pointsCounter;
    private long startTime;
    Timer timer;

    boolean moveUp = false;
    boolean moveDown = false;
    boolean moveRight = false;
    boolean moveLeft = false;
    boolean inGame = true;

    private GameField (GameFrame gm) {
        this.gm = gm;
        this.fieldWidth = gm.getWidth();
        this.fieldHeight= gm.getHeight();
        init();
    }

    private void init () {
        activeMeteorsList = new ArrayList<>();
        pointsCounter = 0;
        startTime = System.currentTimeMillis();
        timer = new Timer(10, this);
        setFocusable(true);
    }

    public void startGame () {
        this.shipOffsetX = (fieldWidth - 25) / 2;
        this.shipOffsetY = (fieldHeight - 25) / 2;
        addKeyListener(new FieldKeyListener());
        timer.start();
    }


    public static GameField getInstance(GameFrame gm) {
        if (instance == null) {
            instance = new GameField(gm);
        }
        return instance;
    }

    public void checkCollision() {
        int[] colliderPointsX = new int[shipSize + 1];
        int[] colliderPointsY = new int[shipSize + 1];
        for(int i = 0; i < shipSize + 1; i++) {
            colliderPointsX[i] = shipOffsetX + i;
            colliderPointsY[i] = shipOffsetY + i;
        }

        for (GameObjectMeteor meteor : activeMeteorsList) {
            for(int i = 0; i < shipSize + 1; i++) {
                if(colliderPointsX[i] >= meteor.meteorOffsetX && colliderPointsX[i] <= meteor.meteorOffsetX + meteor.meteorSize
                && colliderPointsY[i] >= meteor.meteorOffsetY && colliderPointsY[i] <= meteor.meteorOffsetY + meteor.meteorSize) {
                    inGame = false;
                    removeAll();
                }
            }
        }
    }

    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        if (inGame) {
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.cyan);
            g.fillOval(shipOffsetX, shipOffsetY, shipSize, shipSize);

            g.setColor(Color.yellow);
            for (GameObjectMeteor meteor : activeMeteorsList) {
                g.fillRect(meteor.meteorOffsetX, meteor.meteorOffsetY, meteor.meteorSize, meteor.meteorSize);
            }
            gm.setTitle("Timer: " + (System.currentTimeMillis() - startTime) / 1000 + "   " + "Score: " + pointsCounter);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            moveShip();
            for(GameObjectMeteor meteor : activeMeteorsList) {
                meteor.meteorFall();
            }
            checkCollision();
            checkMeteorAlive();

            if ((System.currentTimeMillis() - startTime) % GameObjectMeteor.spawnDelayInMillis == 0) {
                activeMeteorsList.add(new GameObjectMeteor(getWidth(), 30, 5));
            }
        }

        repaint();
    }

    private void checkMeteorAlive() {
        for(int i = 0; i < activeMeteorsList.size(); i++) {
            if (activeMeteorsList.get(i).meteorOffsetY > getHeight()) {
                pointsCounter++;
                activeMeteorsList.remove(i);
            }
        }
    }

    private void moveShip() {
        if (moveUp && upIsClear()) {
           shipOffsetY -= shipStep;
        }
        if (moveDown && downIsClear()) {
            shipOffsetY += shipStep;
        }
        if (moveRight && rightIsClear()) {
            shipOffsetX += shipStep;
        }
        if (moveLeft && leftIsClear()) {
            shipOffsetX -= shipStep;
        }
    }

    public boolean upIsClear() {
        return (shipOffsetY - shipStep) >= 0;
    }

    public boolean downIsClear() {
        return (shipOffsetY + shipStep + shipSize) <= getHeight();
    }

    public boolean leftIsClear() {
        return (shipOffsetX - shipStep) >= 0;
    }

    public boolean rightIsClear() {
        return (shipOffsetX + shipStep + shipSize) <= getWidth();
    }

    class FieldKeyListener extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP) {
                moveUp = true;
            }
            if (key == KeyEvent.VK_DOWN) {
                moveDown = true;
            }
            if (key == KeyEvent.VK_RIGHT) {
                moveRight = true;
            }
            if (key == KeyEvent.VK_LEFT) {
                moveLeft = true;
            }
            if (key == KeyEvent.VK_E) {
                activeMeteorsList.add(new GameObjectMeteor(getWidth(), 10, 5));
            }
            if (key == KeyEvent.VK_Q) {
                inGame = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP) {
                moveUp = false;
            }
            if (key == KeyEvent.VK_DOWN) {
                moveDown = false;
            }
            if (key == KeyEvent.VK_RIGHT) {
                moveRight = false;
            }
            if (key == KeyEvent.VK_LEFT) {
                moveLeft = false;
            }
        }
    }

    static class GameObjectMeteor {

        private static int activeMeteorsCount = 0;
        private static long spawnDelayInMillis = 10;
        private int meteorSize;
        private int meteorOffsetX;
        private int meteorOffsetY;
        private int meteorSpeed;

        private GameObjectMeteor (int fieldWidth, int meteorSize, int meteorSpeed) {
            this.meteorSpeed = meteorSpeed;
            this.meteorSize = meteorSize;
            meteorOffsetX = (int) ((fieldWidth - meteorSize) * Math.random());
            meteorOffsetY = -meteorSize;
            activeMeteorsCount++;
        }


        public void meteorFall () {
            meteorOffsetY += meteorSpeed;
        }

    }

}
