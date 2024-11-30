package model;

import model.plant.*;
import model.zombie.*;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class TestModel {
    public static void main(String[] args){
        JFrame jFrame=new JFrame();
        jFrame.setSize(800,600);
        jFrame.setLocationRelativeTo(null);

        jFrame.add(new TestComponent());

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
