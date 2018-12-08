package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public Main() {
//        getTeams();
//        matchFixtures(getFixtures());
//
//        for (int i = 1; i <= GAMEDAYS; i ++) {
//
//            calculatePlays(i);
//            calculatePlaces();
//
//            System.out.println(i + " ***** GAMEWEEK " + gameweeks +  "  ---  " + formatter.format(teams.get(0).getFixtures().get(i).getDeadlineTime()) + " *****");
//            System.out.format("%24s%13s%13s%13s%13s%13s%13s%13s%13s\n", "NAME", "STRENGTH", "DIFFICULTY", "DIF DIC", "NET PLACE",
//                    "H ATTACK", "H DEFENSE", "A ATTACK", "A DEFENSE");
//            System.out.format("%54s\n", "-----------------------------------------------------------------------------------------------------------------------");
//            for (com.Model.Team team : teams) {
//                System.out.format("%24s%13d%13d%13d%13d%13d%13d%13d%13d\n", team.getName(), team.getStrengthTotal(),
//                        team.getDifficultyTotal(), team.getDifferenceDifficulty(), team.getOutOfPlace(), team.getHomeAttackStrength(),
//                        team.getHomeDefenseStrength(), team.getAwayAttackStrength(), team.getAwayDefenseStrength());
//            }
//            System.out.println("\n\n");
//            gameweeks++;
//        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("PremierChecker.fxml"));
        //new Controller().setup();
        primaryStage.setTitle("Premier Checker");
        primaryStage.setScene(new Scene(root, 1900, 900));
        primaryStage.show();
    }

}
