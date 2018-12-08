package com.controller;

import com.Model.Player;
import com.Model.Team;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    public static int GAMEDAYS = 8;
    public static int WEEK_OFFSET = 0;
    public static final String BASE_URL = "https://fantasy.premierleague.com/drf/";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm E dd/MM/yyyy");

    private List<Player> players;

    @FXML private VBox teamsVBox;
    @FXML private Label teamLabel;
    @FXML private Label teamRatingsLabel;
    @FXML private TitledPane strikersPane;
    @FXML private TitledPane midfieldersPane;
    @FXML private TitledPane defendersPane;
    @FXML private TitledPane goalkeepersPane;
    @FXML private TextField gameweeksBox;
    @FXML private TextField startingFromBox;

    public Controller() {
        setupTeams();
        setupPlayers();
    }

    public void setupTeams() {

        Platform.runLater(() -> {
            TeamChecker teamChecker = new TeamChecker();
            int gameWeeks = teamChecker.getGameWeeks();

            for (int i = 1; i <= GAMEDAYS; i++) {

                List<Team> teams = teamChecker.calculatePlays(i);
                teamChecker.calculatePlaces(teams);

                Label weekLabel = new Label((i + WEEK_OFFSET) + " - GAMEWEEK " + gameWeeks + "  **** " + formatter.format(teams.get(0).getFixtures().get(i - 1).getDeadlineTime()) + " ****");
                weekLabel.setFont(Font.font ("Verdana", 16));
                weekLabel.setTextFill(Color.BLUEVIOLET);
                weekLabel.setStyle("-fx-font-color:blue");
                teamsVBox.getChildren().add(weekLabel);
                teamsVBox.getChildren().add(createTeamTable(teams));
                teamsVBox.getChildren().add(new Separator());
                gameWeeks++;
            }
        });
    }

    public void setupPlayers() {
        new Thread(() -> {
            TeamAdviser teamAdviser = new TeamAdviser();
            players = teamAdviser.getPlayers();
        }).start();
    }

    public void gameWeeksUpdate() {
//        VBox teamsVBox = (VBox) root.lookup("#TEAMS_VBOX");
//        WEEK_OFFSET = Integer.parseInt(((TextField) root.lookup("STARTING_FROM")).getText());
//        setup();
    }

    public void startingFromUpdate() {
        teamsVBox.getChildren().removeAll(teamsVBox.getChildren());
        teamsVBox.getChildren().add(teamRatingsLabel);
        WEEK_OFFSET = Integer.parseInt(startingFromBox.getText());
        setupTeams();
    }

    public void teamClick(Team team) {
        teamLabel.setText(team.getName());

        List<Player> teamPlayers = players.parallelStream()
                .filter(p -> p.getTeam() == team.getId())
                .collect(Collectors.toList());

        TableView<Player> strikers = createGenericPlayerTable(teamPlayers, 4);
        strikersPane.setContent(strikers);

        TableView<Player> midfielders = createGenericPlayerTable(teamPlayers, 3);
        midfieldersPane.setContent(midfielders);

        TableView<Player> defenders = createGenericPlayerTable(teamPlayers, 2);
        defendersPane.setContent(defenders);

        TableView<Player> goalkeepers = createGenericPlayerTable(teamPlayers, 1);
        goalkeepersPane.setContent(goalkeepers);

    }

    public TableView<Player> createGenericPlayerTable(List<Player> players, int playerType) {
        TableView<Player> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn name = new TableColumn("Name");
        name.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));

        TableColumn points = new TableColumn("Points");
        points.setCellValueFactory(new PropertyValueFactory<Player, String>("points"));

        TableColumn cost = new TableColumn("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<Player, String>("cost"));

        TableColumn valueToCost = new TableColumn("Value/Cost");
        valueToCost.setCellValueFactory(new PropertyValueFactory<Player, String>("valueToCost"));

        TableColumn ictIndex = new TableColumn("ICT");
        ictIndex.setCellValueFactory(new PropertyValueFactory<Player, String>("ictIndex"));

        TableColumn valueToICT = new TableColumn("ICT/Cost");
        valueToICT.setCellValueFactory(new PropertyValueFactory<Player, String>("valueToICT"));

        TableColumn minutes = new TableColumn("Minutes");
        minutes.setCellValueFactory(new PropertyValueFactory<Player, String>("minutes"));

        TableColumn chancePlayingThis = new TableColumn("% Playing");
        chancePlayingThis.setCellValueFactory(new PropertyValueFactory<Player, String>("chancePlayingThis"));

        TableColumn chancePlayingNext = new TableColumn("% Playing +1");
        chancePlayingNext.setCellValueFactory(new PropertyValueFactory<Player, String>("chancePlayingNext"));

        TableColumn form = new TableColumn("Form");
        form.setCellValueFactory(new PropertyValueFactory<Player, String>("form"));

        TableColumn influence = new TableColumn("Influence");
        influence.setCellValueFactory(new PropertyValueFactory<Player, String>("influence"));

        TableColumn threat = new TableColumn("Threat");
        threat.setCellValueFactory(new PropertyValueFactory<Player, String>("threat"));

        TableColumn costChange = new TableColumn("Cost Change");
        costChange.setCellValueFactory(new PropertyValueFactory<Player, String>("costChange"));

        TableColumn news = new TableColumn("News");
        news.setCellValueFactory(new PropertyValueFactory<Player, String>("news"));

        table.getColumns().addAll(
                name,
                points,
                cost,
                valueToCost,
                ictIndex,
                valueToICT,
                minutes,
                chancePlayingThis,
                chancePlayingNext,
                form,
                influence,
                threat,
                costChange,
                news
        );

        ObservableList<Player> observablePlayers = FXCollections.observableArrayList(players.stream()
                .filter(p -> p.getPlayerType() == playerType)
                .collect(Collectors.toList()));

        table.setRowFactory(new Callback<>() {
            @Override
            public TableRow<Player> call(TableView<Player> tableView) {
                final TableRow<Player> row = new TableRow<Player>() {
                    @Override
                    protected void updateItem(Player player, boolean empty) {
                        if (player != null) {
                            if ((player.getChancePlayingThis() == 75) || (player.getChancePlayingNext() == 75)) {
                                setStyle("-fx-background-color:yellow");
                            } else if ((player.getChancePlayingThis() > -1 && player.getChancePlayingThis() < 75) ||
                                    (player.getChancePlayingNext() < -1 && player.getChancePlayingNext() < 75)) {
                                setStyle("-fx-background-color:lightcoral");
                            } else {
                                getStyleClass().removeAll();
                            }
                            super.updateItem(player, empty);
                        }
                    }
                };

                return row;
            }
        });

        table.setItems(observablePlayers);
        points.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(points);

        return table;

    }

    public TableView<Team> createTeamTable(List<Team> teams) {
        TableView<Team> table = new TableView<>();
        table.setMinHeight(520);

        TableColumn name = new TableColumn("Name");
        name.setCellValueFactory(new PropertyValueFactory<Team, String>("name"));

        TableColumn strength = new TableColumn("Strength");
        strength.setCellValueFactory(new PropertyValueFactory<Team, String>("strengthTotal"));

        TableColumn difficulty = new TableColumn("Diff");
        difficulty.setCellValueFactory(new PropertyValueFactory<Team, String>("difficultyTotal"));

        TableColumn difficultyDifference = new TableColumn("Diff Dif");
        difficultyDifference.setCellValueFactory(new PropertyValueFactory<Team, String>("differenceDifficulty"));

        TableColumn netPlace = new TableColumn("Net Place");
        netPlace.setCellValueFactory(new PropertyValueFactory<Team, String>("outOfPlace"));

        TableColumn homeAttack = new TableColumn("H Att");
        homeAttack.setCellValueFactory(new PropertyValueFactory<Team, String>("homeAttackStrength"));

        TableColumn homeDefence = new TableColumn("H Def");
        homeDefence.setCellValueFactory(new PropertyValueFactory<Team, String>("homeDefenseStrength"));

        TableColumn awayAttack = new TableColumn("A Att");
        awayAttack.setCellValueFactory(new PropertyValueFactory<Team, String>("awayAttackStrength"));

        TableColumn awayDefence = new TableColumn("A Def");
        awayDefence.setCellValueFactory(new PropertyValueFactory<Team, String>("awayDefenseStrength"));

        TableColumn fixture = new TableColumn("Fixture");
        fixture.setCellValueFactory(new PropertyValueFactory<Team, String>("weeklyFixture"));

        table.getColumns().addAll(
                name,
                strength,
                difficulty,
                difficultyDifference,
                netPlace,
                homeAttack,
                homeDefence,
                awayAttack,
                awayDefence,
                fixture
        );

        table.setRowFactory(tv -> {
            TableRow<Team> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Team rowData = row.getItem();
                    teamClick(rowData);
                }
            });
            return row;
        });

        table.setItems(FXCollections.observableArrayList(teams));
        difficulty.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(difficulty);

        return table;
    }

}
