package com.controller;

import com.Model.Fixture;
import com.Model.Player;
import com.Model.Team;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.Main.SCREEN_HEIGHT;
import static com.Main.SCREEN_WIDTH;

public class Controller {

    public static int GAMEDAYS = 8;
    public static int WEEK_OFFSET = 0;
    public static final String BASE_FPL_URL = "https://fantasy.premierleague.com/drf/";
    public static final String BASE_LEAGUE_URL = "https://fantasy.premierleague.com/drf/";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm E dd/MM/yyyy");

    private List<Player> players;

    @FXML
    private VBox teamsVBox;
    @FXML
    private Label teamLabel;
    @FXML
    private Label teamRatingsLabel;
    @FXML
    private TitledPane strikersPane;
    @FXML
    private TitledPane midfieldersPane;
    @FXML
    private TitledPane defendersPane;
    @FXML
    private TitledPane goalkeepersPane;
    @FXML
    private TextField gameweeksBox;
    @FXML
    private TextField startingFromBox;
    @FXML
    private AnchorPane leagueTablePane;
    @FXML
    private AnchorPane difficultyPane;

    public Controller() {
        setupTeams();
        setupPlayers();
    }

    public void setupTeams() {

        Platform.runLater(() -> {
            TeamChecker teamChecker = new TeamChecker();
            Thread leagueThread = new Thread(() -> teamChecker.getLeaguePlaces());
            leagueThread.start();
            int gameWeeks = teamChecker.getGameWeeks();

            for (int i = 1; i <= GAMEDAYS; i++) {

                List<Team> teams = teamChecker.calculatePlays(i);
                teamChecker.calculatePlaces(teams);

                Label weekLabel = new Label((i + WEEK_OFFSET) + " - GAMEWEEK " + gameWeeks + "  **** " + formatter.format(teams.get(0).getFixtures().get(i - 1).getDeadlineTime()) + " ****");
                weekLabel.setFont(Font.font("Verdana", 16));
                weekLabel.setTextFill(Color.BLUEVIOLET);
                weekLabel.setStyle("-fx-font-color:blue");
                teamsVBox.getChildren().add(weekLabel);
                teamsVBox.getChildren().add(createTeamTable(teams));
                teamsVBox.getChildren().add(new Separator());
                gameWeeks++;
            }

            try {
                leagueThread.join();
                TableView<Team> leagueTable = createLeagueTeamTable(teamChecker.getTeams().values());
                AnchorPane.setLeftAnchor(leagueTable, 0.0);
                AnchorPane.setRightAnchor(leagueTable, 0.0);
                leagueTablePane.getChildren().add(leagueTable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            difficultyPane.getChildren().add(createDifficultyTable(teamChecker.getTeams().values(), teamChecker.getGameWeeks()));

        });
    }

    public void setupPlayers() {
        new Thread(() -> {
            TeamAdviser teamAdviser = new TeamAdviser();
            players = teamAdviser.getPlayers();
        }).start();
    }

    public void gameWeeksUpdate() {
        teamsVBox.getChildren().removeAll(teamsVBox.getChildren());
        teamsVBox.getChildren().add(teamRatingsLabel);
        GAMEDAYS = Integer.parseInt(gameweeksBox.getText());
        setupTeams();
    }

    public void startingFromUpdate() {
        teamsVBox.getChildren().removeAll(teamsVBox.getChildren());
        teamsVBox.getChildren().add(teamRatingsLabel);
        WEEK_OFFSET = Integer.parseInt(startingFromBox.getText());
        setupTeams();
    }

    public void teamClick(Team team) {
        String fixtures = "";
        for (Fixture fix : team.getFixtures()) {
            fixtures += (fix.getHomeTeam() == team.getId() ? fix.getAwayTeamName() : fix.getHomeTeamName()) + ", ";
        }

        teamLabel.setText(team.getName().substring(0, team.getName().length() - 4) + " - " + fixtures);

        List<Player> teamPlayers = players.parallelStream()
                .filter(p -> p.getTeam() == team.getId())
                .collect(Collectors.toList());

        TableView<Player> strikers = createGenericPlayerTable(teamPlayers, 4, 200);
        strikersPane.setContent(strikers);

        TableView<Player> midfielders = createGenericPlayerTable(teamPlayers, 3, 300);
        midfieldersPane.setContent(midfielders);

        TableView<Player> defenders = createGenericPlayerTable(teamPlayers, 2, 300);
        defendersPane.setContent(defenders);

        TableView<Player> goalkeepers = createGenericPlayerTable(teamPlayers, 1, 150);
        goalkeepersPane.setContent(goalkeepers);

    }

    public TableView<Player> createGenericPlayerTable(List<Player> players, int playerType, int prefHeight) {
        TableView<Player> table = new TableView<>();
        table.setPrefHeight(prefHeight);

        TableColumn name = new TableColumn("Name");
        name.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));

        TableColumn points = new TableColumn("Points");
        points.setCellValueFactory(new PropertyValueFactory<Player, String>("points"));

        TableColumn cost = new TableColumn("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<Player, String>("cost"));

        TableColumn ictIndex = new TableColumn("ICT");
        ictIndex.setCellValueFactory(new PropertyValueFactory<Player, String>("ictIndex"));

        TableColumn minutes = new TableColumn("Minutes");
        minutes.setCellValueFactory(new PropertyValueFactory<Player, String>("minutes"));

        TableColumn valueToCost = new TableColumn("Value/Cost");
        valueToCost.setCellValueFactory(new PropertyValueFactory<Player, String>("valueToCost"));

        TableColumn valueToICT = new TableColumn("ICT/Cost");
        valueToICT.setCellValueFactory(new PropertyValueFactory<Player, String>("valueToICT"));

        TableColumn valueToMinutes = new TableColumn("Mins/Points");
        valueToMinutes.setCellValueFactory(new PropertyValueFactory<Player, String>("valueToMinutes"));

        TableColumn chancePlayingThis = new TableColumn("% Playing Last");
        chancePlayingThis.setCellValueFactory(new PropertyValueFactory<Player, String>("chancePlayingThis"));

        TableColumn chancePlayingNext = new TableColumn("% Playing");
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
                ictIndex,
                minutes,
                valueToCost,
                valueToICT,
                valueToMinutes,
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

        table.setRowFactory(tv -> {
            final TableRow<Player> row = new TableRow<>() {
                @Override
                protected void updateItem(Player player, boolean empty) {
                    if (player != null) {
                        if (player.getChancePlayingThis() == 75) {
                            setStyle("-fx-background-color:lightyellow");
                        } else if (player.getChancePlayingNext() == 75) {
                            setStyle("-fx-background-color:yellow");
                        } else if ((player.getChancePlayingThis() > -1 && player.getChancePlayingThis() < 75) ||
                                (player.getChancePlayingNext() > -1 && player.getChancePlayingNext() < 75)) {
                            setStyle("-fx-background-color:lightcoral");
                        } else {
                            getStyleClass().removeAll();
                        }
                        super.updateItem(player, empty);
                    }
                }
            };

            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:lightgreen"));
            row.setOnMouseExited(e -> {
                row.setStyle("");
                Player player = row.getItem();
                if (player != null) {
                    if ((player.getChancePlayingThis() == 75) || (player.getChancePlayingNext() == 75)) {
                        row.setStyle("-fx-background-color:yellow");
                    } else if ((player.getChancePlayingThis() > -1 && player.getChancePlayingThis() < 75) ||
                            (player.getChancePlayingNext() > -1 && player.getChancePlayingNext() < 75)) {
                        row.setStyle("-fx-background-color:lightcoral");
                    } else {
                        row.getStyleClass().removeAll();
                    }
                }
            });
            return row;
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

            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:lightgreen"));
            row.setOnMouseExited(e -> row.setStyle(""));
            return row;
        });

        table.setItems(FXCollections.observableArrayList(teams));
        difficulty.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(difficulty);

        return table;
    }

    public TableView<Team> createLeagueTeamTable(Collection<Team> teams) {
        TableView<Team> table = new TableView<>();
        table.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        TableColumn position = new TableColumn("");
        position.setCellValueFactory(new PropertyValueFactory<Team, String>("positionChangeImage"));

        TableColumn name = new TableColumn("Team");
        name.setCellValueFactory(new PropertyValueFactory<Team, String>("name"));

        TableColumn played = new TableColumn("Played");
        played.setCellValueFactory(new PropertyValueFactory<Team, String>("played"));

        TableColumn points = new TableColumn("Points");
        points.setCellValueFactory(new PropertyValueFactory<Team, String>("points"));

        TableColumn wins = new TableColumn("Wins");
        wins.setCellValueFactory(new PropertyValueFactory<Team, String>("wins"));

        TableColumn draws = new TableColumn("Draws");
        draws.setCellValueFactory(new PropertyValueFactory<Team, String>("draws"));

        TableColumn losses = new TableColumn("Losses");
        losses.setCellValueFactory(new PropertyValueFactory<Team, String>("losses"));

        TableColumn goalsFor = new TableColumn("Goals For");
        goalsFor.setCellValueFactory(new PropertyValueFactory<Team, String>("goalsFor"));

        TableColumn goalsAgainst = new TableColumn("Goals Against");
        goalsAgainst.setCellValueFactory(new PropertyValueFactory<Team, String>("goalsAgainst"));

        TableColumn cleanSheets = new TableColumn("Clean Sheets");
        cleanSheets.setCellValueFactory(new PropertyValueFactory<Team, String>("cleanSheets"));

        table.getColumns().addAll(
                position,
                name,
                played,
                points,
                wins,
                draws,
                losses,
                goalsFor,
                goalsAgainst,
                cleanSheets
        );

        table.setRowFactory(tv -> {
            TableRow<Team> row = new TableRow<>();
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:lightgreen"));
            row.setOnMouseExited(e -> row.setStyle(""));
            return row;
        });

        table.setItems(FXCollections.observableArrayList(teams));
        points.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(points);

        return table;
    }

    public TableView<List<StringProperty>> createDifficultyTable(Collection<Team> teams, int gameweeks) {

        TableView table = new TableView<>();
        table.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        int gameweeksRemaining = 38 - gameweeks;

        TableColumn<List<StringProperty>, String> name = new TableColumn("Name");
        name.setCellValueFactory(data -> data.getValue().get(0));
        table.getColumns().add(name);

        TableColumn<List<StringProperty>, String> points = new TableColumn("Points");
        points.setCellValueFactory(data -> data.getValue().get(1));
        table.getColumns().add(points);

        TableColumn<List<StringProperty>, String> difficulty = new TableColumn("Difficulty");
        difficulty.setCellValueFactory(data -> data.getValue().get(2));
        difficulty.setEditable(true);
        table.getColumns().add(difficulty);

        for (int i = 0; i < gameweeksRemaining; i++)  {
            TableColumn<List<StringProperty>, String> column = new TableColumn("" + (gameweeks + i));
            column.setPrefWidth(50);
            int finalI = i + 3;
            column.setCellValueFactory(data -> data.getValue().get(finalI));

            column.setCellFactory(new Callback<TableColumn<List<StringProperty>, String>, TableCell<List<StringProperty>, String>>()
            {
                @Override
                public TableCell<List<StringProperty>, String> call(
                        TableColumn<List<StringProperty>, String> param) {
                    return new TableCell<List<StringProperty>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            if (!empty) {
                                if (item.equals("1")) {
                                    setStyle("-fx-background-color: #00910e");
                                } else if (item.equals("2")) {
                                    setStyle("-fx-background-color: #00ff86");
                                } else if (item.equals("3")) {
                                    setStyle("-fx-background-color: #ebebe4");
                                } else if (item.equals("4")) {
                                    setStyle("-fx-background-color: #ff005a");
                                } else if (item.equals("5")) {
                                    setStyle("-fx-background-color: #861d46");
                                }
                                setStyle(getStyle() + "; -fx-border-color: black");
                            }
                        }
                    };
                }
            });

            table.getColumns().add(column);
        }

        ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();

        for (Team team : teams) {

            List<StringProperty> row = new ArrayList<>();
            row.add(0, new SimpleStringProperty(team.getName()));
            row.add(1, new SimpleStringProperty("" + team.getPoints()));
            row.add(2, new SimpleStringProperty("" + team.getStrength()));

            for (int i = 0; i < gameweeksRemaining; i++) {
                Fixture fix = team.getFixtures().get(i);

                row.add(i + 3, new SimpleStringProperty("" + (fix.getHomeTeam() == team.getId() ? fix.getHomeDifficulty() : fix.getAwayDifficulty())));

            }
            data.add(row);
        }

        table.setRowFactory(tv -> {
            final TableRow<List<StringProperty>> row = new TableRow<>();
//            row.setStyle("-fx-padding: 10 10 10 10");
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:lightgreen"));
            row.setOnMouseExited(e -> row.setStyle(""));
            return row;
        });

        table.setStyle("-fx-border-color: red; -fx-table-cell-border-color:red");
        table.setFixedCellSize(40.0);
        table.setItems(data);

        points.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(points);

        return table;
    }

}
