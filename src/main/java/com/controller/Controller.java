package com.controller;

import com.model.Fixture;
import com.model.Player;
import com.model.PlayerValue;
import com.model.Team;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.Main.SCREEN_HEIGHT;
import static com.Main.SCREEN_WIDTH;

public class Controller {

    public static int GAMEDAYS = 8;
    public static int WEEK_OFFSET = 0;
    public static int CURRENT_GAMEWEEK = 0;
    public static final String BASE_FPL_URL = "https://fantasy.premierleague.com/api/";
    public static final String LOGIN_FPL_URL = "https://users.premierleague.com/accounts/login/";
    public static JSONObject FPL_DATA;
    private int OPTIMAL_WEEKS = 5;

    private Map<Integer, Player> players;

    @FXML
    private ScrollPane preSeasonGoodValue;
    @FXML
    private VBox teamsVBox, preSeasonBadValue;
    @FXML
    private Label teamLabel, teamRatingsLabel, selectedPlayers, totalValue, totalPoints, averagePoints, averageCost;
    @FXML
    private TitledPane strikersPane, midfieldersPane, defendersPane, goalkeepersPane;
    @FXML
    private TitledPane strikersPaneAll, midfieldersPaneAll, defendersPaneAll, goalkeepersPaneAll;
    @FXML
    private TextField gameweeksBox, startingFromBox, matrixStarting, matrixOptimal;
    @FXML
    private AnchorPane leagueTablePane, difficultyPane, playersPane;

    public Controller() {
        setupTeams();
        setupPlayers();
    }

    public void setupTeams() {

        new Thread(() -> {

            TeamChecker teamChecker = new TeamChecker();

            Thread leagueThread = new Thread(() -> teamChecker.getLeaguePlaces());
            leagueThread.start();

            List<Team> optimalTeams = new ArrayList<>(4);

            int gameWeeks = CURRENT_GAMEWEEK;

            for (int i = 1; i <= GAMEDAYS; i++) {

                List<Team> teams = teamChecker.calculatePlays(i);
                teamChecker.calculatePlaces(teams);

                if (i == OPTIMAL_WEEKS) {
                    optimalTeams.addAll(teams.subList(16, 20));
                }

                Label weekLabel = new Label((i + WEEK_OFFSET) + " - GAMEWEEK " + gameWeeks + "  **** " + teams.get(0).getGroupedFixtures().keySet().toArray()[i - 1] + " ****");
                weekLabel.setFont(Font.font("Verdana", 16));
                weekLabel.setTextFill(Color.BLUEVIOLET);
                weekLabel.setStyle("-fx-font-color:blue");

                Platform.runLater(() -> {
                    teamsVBox.getChildren().add(weekLabel);
                    teamsVBox.getChildren().add(createTeamTable(teams));
                    teamsVBox.getChildren().add(new Separator());
                });

                gameWeeks++;
            }

            try {
                leagueThread.join();
                TableView<Team> leagueTable = createLeagueTeamTable(teamChecker.getTeams().values());
                AnchorPane.setLeftAnchor(leagueTable, 0.0);
                AnchorPane.setRightAnchor(leagueTable, 0.0);
                Platform.runLater(() -> {
                    leagueTablePane.getChildren().add(leagueTable);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TableView<List<StringProperty>> difficultyTable = createDifficultyTable(teamChecker.getTeams(), optimalTeams);
            Platform.runLater(() -> difficultyPane.getChildren().add(difficultyTable));

        }).start();
    }

    public void setupPlayers() {
        new Thread(() -> {
            TeamAdviser teamAdviser = new TeamAdviser();
            players = teamAdviser.getPlayers();

            TableView<Player> playerTable = createPlayerTable(players.values());
            TableView<Player> strikers = createPlayerPositionTable(players.values(), 4, 200);
            TableView<Player> midfielders = createPlayerPositionTable(players.values(), 3, 300);
            TableView<Player> defenders = createPlayerPositionTable(players.values(), 2, 300);
            TableView<Player> goalkeepers = createPlayerPositionTable(players.values(), 1, 150);

            Platform.runLater(() -> {
                playersPane.getChildren().add(playerTable);
                strikersPaneAll.setContent(strikers);
                midfieldersPaneAll.setContent(midfielders);
                defendersPaneAll.setContent(defenders);
                goalkeepersPaneAll.setContent(goalkeepers);
            });

            Map<PlayerValue, List<Player>> buys = teamAdviser.findGoodBadBuys();

            List<Player> goodBuys = buys.get(PlayerValue.GOOD);
            TableView<Player> goodBuysTable = createPlayerTable(goodBuys);
            Platform.runLater(() -> {
                preSeasonGoodValue.setContent(goodBuysTable);
            });


            List<Player> badBuys = buys.get(PlayerValue.BAD);
            TableView<Player> badBuysTable = createPlayerTable(badBuys);
            Platform.runLater(() -> {
                preSeasonBadValue.getChildren().add(badBuysTable);
            });


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
        for (List<Fixture> fixList : team.getGroupedFixtures().values()) {
            for (Fixture fix : fixList) {
                fixtures += (fix.getHomeTeam() == team.getId() ? fix.getAwayTeamName() : fix.getHomeTeamName()) + ", ";
            }
        }

        teamLabel.setText(team.getName().substring(0, team.getName().length() - 4) + " - " + fixtures);

        List<Player> teamPlayers = players.values().parallelStream()
                .filter(p -> p.getTeam() == team.getId())
                .collect(Collectors.toList());

        TableView<Player> strikers = createPlayerPositionTable(teamPlayers, 4, 200);
        strikersPane.setContent(strikers);

        TableView<Player> midfielders = createPlayerPositionTable(teamPlayers, 3, 300);
        midfieldersPane.setContent(midfielders);

        TableView<Player> defenders = createPlayerPositionTable(teamPlayers, 2, 300);
        defendersPane.setContent(defenders);

        TableView<Player> goalkeepers = createPlayerPositionTable(teamPlayers, 1, 150);
        goalkeepersPane.setContent(goalkeepers);

    }

    public TableView<Player> createPlayerPositionTable(Collection<Player> players, int playerType, int prefHeight) {

        ObservableList<Player> observablePlayers = FXCollections.observableArrayList(players.stream()
                .filter(p -> p.getPlayerType() == playerType)
                .collect(Collectors.toList()));

        TableView<Player> table = createGenericPlayerTable(observablePlayers);
        table.setPrefHeight(prefHeight);

        return table;
    }

    public TableView<Player> createPlayerTable(Collection<Player> players) {

        TableView<Player> table = createGenericPlayerTable(FXCollections.observableArrayList(players));
        table.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        return table;
    }

    private TableView<Player> createGenericPlayerTable(ObservableList<Player> players) {
        TableView<Player> table = new TableView<>();

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

        TableColumn weightedValue = new TableColumn("Weighted");
        weightedValue.setCellValueFactory(new PropertyValueFactory<Player, String>("weightedValue"));

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

        TableColumn team = new TableColumn("Team ID");
        team.setCellValueFactory(new PropertyValueFactory<Player, String>("team"));

        TableColumn cleanSheets = new TableColumn("Clean Sheets");
        cleanSheets.setCellValueFactory(new PropertyValueFactory<Player, String>("cleanSheets"));

        TableColumn goalsConceded = new TableColumn("Conceded");
        goalsConceded.setCellValueFactory(new PropertyValueFactory<Player, String>("goalsConceded"));

        TableColumn ownGoals = new TableColumn("Own Goals");
        ownGoals.setCellValueFactory(new PropertyValueFactory<Player, String>("ownGoals"));

        TableColumn penaltiesSaved = new TableColumn("Pens saved");
        penaltiesSaved.setCellValueFactory(new PropertyValueFactory<Player, String>("penaltiesSaved"));

        TableColumn penaltiesScored = new TableColumn("Pens scored");
        penaltiesScored.setCellValueFactory(new PropertyValueFactory<Player, String>("penaltiesScored"));

        TableColumn yellowCards = new TableColumn("Yellows");
        yellowCards.setCellValueFactory(new PropertyValueFactory<Player, String>("yellowCards"));

        TableColumn redCards = new TableColumn("Reds");
        redCards.setCellValueFactory(new PropertyValueFactory<Player, String>("redCards"));

        TableColumn saves = new TableColumn("Saves");
        saves.setCellValueFactory(new PropertyValueFactory<Player, String>("saves"));

        TableColumn bonus = new TableColumn("Bonus");
        bonus.setCellValueFactory(new PropertyValueFactory<Player, String>("bonus"));

        table.getColumns().addAll(
                name,
                points,
                cost,
                ictIndex,
                minutes,
                valueToCost,
                valueToICT,
                valueToMinutes,
                weightedValue,
                chancePlayingThis,
                chancePlayingNext,
                form,
                influence,
                threat,
                costChange,
                news,
                team,
                cleanSheets,
                goalsConceded,
                ownGoals,
                penaltiesSaved,
                penaltiesScored,
                yellowCards,
                redCards,
                saves,
                bonus
        );
        table.setRowFactory(tv -> {
            final TableRow<Player> row = new TableRow<Player>() {
                @Override
                protected void updateItem(Player player, boolean empty) {
                    if (player != null) {
                        if (player.getChancePlayingNext() == 75) {
                            setStyle("-fx-background-color:yellow");
                        } else if ((player.getChancePlayingThis() > -1 && player.getChancePlayingThis() < 75) ||
                                (player.getChancePlayingNext() > -1 && player.getChancePlayingNext() < 75)) {
                            setStyle("-fx-background-color:lightcoral");
                        } else if (player.getChancePlayingThis() == 75) {
                            setStyle("-fx-background-color:lightyellow");
                        } else {
                            setStyle("");
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
                    if (player.getChancePlayingNext() == 75) {
                        row.setStyle("-fx-background-color:yellow");
                    } else if ((player.getChancePlayingThis() > -1 && player.getChancePlayingThis() < 75) ||
                            (player.getChancePlayingNext() > -1 && player.getChancePlayingNext() < 75)) {
                        row.setStyle("-fx-background-color:lightcoral");
                    } else if (player.getChancePlayingThis() == 75) {
                        row.setStyle("-fx-background-color:lightyellow");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });

        table.setItems(players);
        points.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(points);

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListChangeListener<Player> multiSelection = e -> {
            double totalValueDouble = Double.parseDouble(totalValue.getText());
            int totalPointsInt = Integer.parseInt(totalPoints.getText());
            int selected = Integer.parseInt(selectedPlayers.getText());

            e.next();

                for (Player player : e.getAddedSubList()) {
                    totalValueDouble += player.getCost();
                    totalPointsInt += player.getPoints();
                }
                selected += e.getAddedSubList().size();

                for (Player player : e.getRemoved()) {
                    totalValueDouble -= player.getCost();
                    totalPointsInt -= player.getPoints();
                }
                selected -= e.getRemoved().size();


            totalValueDouble = Math.round(totalValueDouble * 100);
            totalValueDouble /= 100;
            totalValue.setText("" + totalValueDouble);
            totalPoints.setText("" + totalPointsInt);
            selectedPlayers.setText("" + selected);
            averagePoints.setText("" + (selected == 0 ? 0 : (totalPointsInt / selected)));
            averageCost.setText("" + (selected == 0 ? 0 : (totalValueDouble/selected)));
        };

        table.getSelectionModel().getSelectedItems().addListener(multiSelection);

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

        TableColumn goalsScored = new TableColumn("Goals Scored");
        goalsScored.setCellValueFactory(new PropertyValueFactory<Team, String>("goalsScored"));

        TableColumn goalsConceded = new TableColumn("Goals Conceded");
        goalsConceded.setCellValueFactory(new PropertyValueFactory<Team, String>("goalsConceded"));

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
                goalsScored,
                goalsConceded,
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

    public TableView<List<StringProperty>> createDifficultyTable(Map<Integer, Team> teams, List<Team> optimalTeams) {

        TableView table = new TableView<>();
        table.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        int gameweeksRemaining = (38 - CURRENT_GAMEWEEK) + 1;

        TableColumn<List<StringProperty>, String> id = new TableColumn("ID");
        id.setCellValueFactory(data -> data.getValue().get(0));
        table.getColumns().add(id);

        TableColumn<List<StringProperty>, String> name = new TableColumn("Name");
        name.setCellValueFactory(data -> data.getValue().get(1));
        table.getColumns().add(name);

        TableColumn<List<StringProperty>, String> points = new TableColumn("Points");
        points.setCellValueFactory(data -> data.getValue().get(2));
        table.getColumns().add(points);

        TableColumn<List<StringProperty>, String> difficulty = new TableColumn("Difficulty");
        difficulty.setCellValueFactory(data -> data.getValue().get(3));
        difficulty.setEditable(true);
        table.getColumns().add(difficulty);

        for (int i = 0; i < gameweeksRemaining; i++) {
            TableColumn<List<StringProperty>, String> column = new TableColumn("" + (CURRENT_GAMEWEEK + i));
            column.setPrefWidth(50);
            int finalI = i + 4;
            column.setCellValueFactory(data -> data.getValue().get(finalI));

            int gameweekColumn = i;
            column.setCellFactory(new Callback<TableColumn<List<StringProperty>, String>, TableCell<List<StringProperty>, String>>() {
                @Override
                public TableCell<List<StringProperty>, String> call(
                        TableColumn<List<StringProperty>, String> param) {
                    return new TableCell<List<StringProperty>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            if (!empty) {
                                if (item.contains("|")) {
                                    String[] parts = item.split(Pattern.quote("|"));
                                    String colour1 = getColour(parts[0]);
                                    String colour2 = getColour(parts[1]);
                                    setStyle(makeDoubleCss(colour1, colour2));
                                } else {
                                   setStyle(makeSingleCss(getColour(item)));
                                }

                                int id = Integer.parseInt(((StringProperty)((ArrayList)getTableRow().getItem()).get(0)).get());
                                List<Fixture> fixList = (List<Fixture>) teams.get(id).getGroupedFixtures().values().toArray()[gameweekColumn];
                                String tooltipText = "";
                                for (Fixture fix : fixList) {
                                    if (!tooltipText.equals("")) tooltipText += "  |  ";
                                    tooltipText += fix.getHomeTeam() == id ? fix.getAwayTeamName() + " (A)" : fix.getHomeTeamName() + " (H)";
                                }
                                setTooltip(new Tooltip(tooltipText.equals("") ? "BLANK" : tooltipText));

                                int index = table.getColumns().indexOf(getTableColumn());
                                if (index > 3 && index < 3 + Integer.parseInt(matrixOptimal.getText())) {
                                    for (Team team : optimalTeams) {
                                        if (team.getId() == id) {
                                            if (getStyle().contains("%")) {
                                                String colour1 = getStyle().substring(getStyle().length() - 12, getStyle().length() - 5);
                                                String colour2 = getStyle().substring(getStyle().length() - 25, getStyle().length() - 18);
//                                                String colour1 = getStyle().substring(getStyle().length() - 13, getStyle().length() - 6);
//                                                String colour2 = getStyle().substring(getStyle().length() - 39, getStyle().length() - 32);
                                                setStyle("-fx-background-color: linear-gradient(from 41% 34% to 50% 50%, reflect, " + colour1 + " 30%, orange 47%, " + colour2 + " 60%);");
                                            } else {
                                                String colour = getStyle().substring(getStyle().length() - 7);
                                                setStyle("-fx-background-color: linear-gradient(from 41% 34% to 50% 50%, reflect, orange 25%, " + colour + " 47%);");
                                            }
                                        }
                                    }
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

        for (Team team : teams.values()) {

            List<StringProperty> rowCells = new ArrayList<>();
            rowCells.add(0, new SimpleStringProperty("" + team.getId()));
            rowCells.add(1, new SimpleStringProperty(team.getName()));
            rowCells.add(2, new SimpleStringProperty("" + team.getPoints()));
            rowCells.add(3, new SimpleStringProperty("" + team.getStrength()));

            for (int i = 0; i < gameweeksRemaining; i++) {
                List<Fixture> fixList = (List<Fixture>)team.getGroupedFixtures().values().toArray()[i];

                String cellValue = "";
                for (int j = 0; j < fixList.size(); j++) {
                    if (j == 1) {
                        cellValue += "|";
                    }
                    Fixture fix = fixList.get(j);
                    cellValue += fix.getHomeTeam() == team.getId() ? fix.getHomeDifficulty() : fix.getAwayDifficulty();
                }
                if (cellValue.equals("")) cellValue = "6";
                rowCells.add(i + 4, new SimpleStringProperty(cellValue));

            }
            data.add(rowCells);
        }

        table.setRowFactory(tv -> {
            final TableRow<List<StringProperty>> row = new TableRow<>();
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:lightgreen;"));
            row.setOnMouseExited(e -> row.setStyle(""));
            return row;
        });

        table.setStyle("-fx-border-color: red; -fx-table-cell-border-color:red;");
        table.setFixedCellSize(40.0);
        table.setItems(data);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        points.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(points);

        return table;
    }

    public String getColour(String item) {
        if (item.equals("1")) {
            return "#00910e";
        } else if (item.equals("2")) {
           return "#00ff86";
        } else if (item.equals("3")) {
           return "#ebebe4";
        } else if (item.equals("4")) {
           return "#ff005a";
        } else if (item.equals("5")) {
           return "#861d46";
        } else if (item.equals("6")) {
           return "#000000";
        }
        return "#0f5cd8";
    }

    public String makeSingleCss(String colour1) {
        return "-fx-background-color: " + colour1;
    }

    public String makeDoubleCss(String colour1, String colour2) {
        //return "-fx-background-color: linear-gradient(from 0px 0px to 50px 0px, " + colour1 + " 0%, " + colour1 + " 50%, " + colour2 + " 50%, " + colour2 + " 100%)";
        return "-fx-background-color: linear-gradient(from 41% 34% to 50% 50%, reflect, " + colour1 + " 45%, black 50%, " + colour2 + " 55%)";
    }

    public void setMatrixListener() {
        OPTIMAL_WEEKS = Integer.parseInt(matrixOptimal.getText());
        setupTeams();
    }
}
