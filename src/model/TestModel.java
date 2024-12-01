package model;

import javax.swing.*;

public class TestModel {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setSize(840, 630);
        jFrame.setLocationRelativeTo(null);

        jFrame.add(new TestComponent());

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
