package org.example;
public class RoomFair {
    private int id;
    private double value;
    private String season;

    public RoomFair(int id, double value, String season) {
        this.id = id;
        this.season = season;
        if (season.equals("winter")){
            value = value - 30;
        }
        this.value = value;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
