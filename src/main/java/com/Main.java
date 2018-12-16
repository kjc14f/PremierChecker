package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public Main() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("PremierChecker.fxml"));
        primaryStage.setTitle("Premier Checker");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(screenSize.getHeight());
        primaryStage.setScene(new Scene(root, screenSize.getWidth() - 50, screenSize.getHeight() - 100));
        primaryStage.show();
    }

}
