package com.Model;

public class Player {

    private String name;
    private float cost;
    private float form;
    private int points;
    private int minutes;
    private int playerType;
    private int chancePlayingThis;
    private int chancePlayingNext;
    private float costChange;
    private String influence;
    private String threat;
    private double ictIndex;
    private int team;
    private String news;
    private double valueToCost;
    private double valueToICT;

    public Player(String name, float cost, float form, int points, int minutes, int playerType, int chancePlayingThis,
                  int chancePlayingNext, float costChange, String influence, String threat, double ictIndex, int team,
                  String news, double valueToCost, double valueToICT) {
        this.name = name;
        this.cost = cost;
        this.form = form;
        this.points = points;
        this.minutes = minutes;
        this.playerType = playerType;
        this.chancePlayingThis = chancePlayingThis;
        this.chancePlayingNext = chancePlayingNext;
        this.costChange = costChange;
        this.influence = influence;
        this.threat = threat;
        this.ictIndex = ictIndex;
        this.team = team;
        this.news = news;
        this.valueToCost = valueToCost;
        this.valueToICT = valueToICT;
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

    public float getCostChange() {
        return costChange;
    }

    public void setCostChange(float costChange) {
        this.costChange = costChange;
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
}
