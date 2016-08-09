package net.nostate.drugstore.tresenzettel.models;

public class Beverage {

    public static final String HEADER_NAME = "Getränk";
    public static final String HEADER_BOTTLES_PER_CASE = "Flaschen pro Kasten";
    public static final String HEADER_SKP = "SKP";
    public static final String HEADER_VKP = "VKP";
    public static final String HEADER_CASES = "Kästen";
    public static final String HEADER_BOTTLES = "Flaschen";

    private String name;
    private int bottlesPerCase = 0;
    private double SKP = 0.0;
    private double VKP = 0.0;
    private int cases = 0;
    private int bottles = 0;

    public Beverage(String name) {
        this.name = name;
    }

    public Beverage(String name, int cases, int bottles) {
        this.name = name;
        this.cases = cases;
        this.bottles = bottles;
    }

    public String getName() {
        return name;
    }

    public int getBottlesPerCase() {
        return bottlesPerCase;
    }

    public double getSKP() {
        return SKP;
    }

    public double getVKP() {
        return VKP;
    }

    public int getCases() {
        return cases;
    }

    public int getBottles() {
        return bottles;
    }

    public void setBottlesPerCase(int bottlesPerCase) {
        this.bottlesPerCase = bottlesPerCase;
    }

    public void setSKP(double SKP) {
        this.SKP = SKP;
    }

    public void setVKP(double VKP) {
        this.VKP = VKP;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public void setBottles(int bottles) {
        this.bottles = bottles;
    }
}
