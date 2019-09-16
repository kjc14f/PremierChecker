package com.model;

public class Player {

    private String name;
    private float cost;
    private float form;
    private int points;
    private int minutes;
    private int playerType;
    private int chancePlayingThis;
    private int chancePlayingNext;
    private float costChangeTotal;
    private float costChangeWeek;
    private String influence;
    private String threat;
    private double ictIndex;
    private int team;
    private String news;
    private double valueToCost;
    private double valueToICT;
    private double valueToMinutes;
    private double weightedValue;
    private int cleanSheets;
    private int goalsConceded;
    private int ownGoals;
    private int penaltiesSaved;
    private int penaltiesScored;
    private int yellowCards;
    private int redCards;
    private int saves;
    private int bonus;

    public Player(String name, float cost, float form, int points, int minutes, int playerType, int chancePlayingThis, int chancePlayingNext, float costChangeTotal, float costChangeWeek, String influence, String threat, double ictIndex, int team, String news, double valueToCost, double valueToICT, double valueToMinutes, double weightedValue, int cleanSheets, int goalsConceded, int ownGoals, int penaltiesSaved, int penaltiesScored, int yellowCards, int redCards, int saves, int bonus) {
        this.name = name;
        this.cost = cost;
        this.form = form;
        this.points = points;
        this.minutes = minutes;
        this.playerType = playerType;
        this.chancePlayingThis = chancePlayingThis;
        this.chancePlayingNext = chancePlayingNext;
        this.costChangeTotal = costChangeTotal;
        this.costChangeWeek = costChangeWeek;
        this.influence = influence;
        this.threat = threat;
        this.ictIndex = ictIndex;
        this.team = team;
        this.news = news;
        this.valueToCost = valueToCost;
        this.valueToICT = valueToICT;
        this.valueToMinutes = valueToMinutes;
        this.weightedValue = weightedValue;
        this.cleanSheets = cleanSheets;
        this.goalsConceded = goalsConceded;
        this.ownGoals = ownGoals;
        this.penaltiesSaved = penaltiesSaved;
        this.penaltiesScored = penaltiesScored;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.saves = saves;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getForm() {
        return form;
    }

    public void setForm(float form) {
        this.form = form;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public int getChancePlayingThis() {
        return chancePlayingThis;
    }

    public void setChancePlayingThis(int chancePlayingThis) {
        this.chancePlayingThis = chancePlayingThis;
    }

    public int getChancePlayingNext() {
        return chancePlayingNext;
    }

    public void setChancePlayingNext(int chancePlayingNext) {
        this.chancePlayingNext = chancePlayingNext;
    }

    public float getCostChangeTotal() {
        return costChangeTotal;
    }

    public void setCostChangeTotal(float costChangeTotal) {
        this.costChangeTotal = costChangeTotal;
    }

    public float getCostChangeWeek() {
        return costChangeWeek;
    }

    public void setCostChangeWeek(float costChangeWeek) {
        this.costChangeWeek = costChangeWeek;
    }

    public String getInfluence() {
        return influence;
    }

    public void setInfluence(String influence) {
        this.influence = influence;
    }

    public String getThreat() {
        return threat;
    }

    public void setThreat(String threat) {
        this.threat = threat;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public double getValueToCost() {
        return valueToCost;
    }

    public void setValueToCost(double valueToCost) {
        this.valueToCost = valueToCost;
    }

    public double getIctIndex() {
        return ictIndex;
    }

    public void setIctIndex(double ictIndex) {
        this.ictIndex = ictIndex;
    }

    public double getValueToICT() {
        return valueToICT;
    }

    public void setValueToICT(double valueToICT) {
        this.valueToICT = valueToICT;
    }

    public double getValueToMinutes() {
        return valueToMinutes;
    }

    public void setValueToMinutes(double valueToMinutes) {
        this.valueToMinutes = valueToMinutes;
    }

    public double getWeightedValue() {
        return weightedValue;
    }

    public void setWeightedValue(double weightedValue) {
        this.weightedValue = weightedValue;
    }

    public int getCleanSheets() {
        return cleanSheets;
    }

    public void setCleanSheets(int cleanSheets) {
        this.cleanSheets = cleanSheets;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(int goalsConceded) {
        this.goalsConceded = goalsConceded;
    }

    public int getOwnGoals() {
        return ownGoals;
    }

    public void setOwnGoals(int ownGoals) {
        this.ownGoals = ownGoals;
    }

    public int getPenaltiesSaved() {
        return penaltiesSaved;
    }

    public void setPenaltiesSaved(int penaltiesSaved) {
        this.penaltiesSaved = penaltiesSaved;
    }

    public int getPenaltiesScored() {
        return penaltiesScored;
    }

    public void setPenaltiesScored(int penaltiesScored) {
        this.penaltiesScored = penaltiesScored;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
