package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    public static double SCREEN_WIDTH;
    public static double SCREEN_HEIGHT;
    public static boolean DUMMY = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = screenSize.getWidth() - 50;
        SCREEN_HEIGHT = screenSize.getHeight() - 150;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("PremierChecker.fxml"));
        primaryStage.setTitle("Premier Checker");
        primaryStage.setScene(new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

}
