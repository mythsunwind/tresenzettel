package net.nostate.drugstore.tresenzettel.models;

import java.util.Date;

public class Sheet {

    public static final String HEADER_NUMBER = "Nummer";
    public static final String HEADER_OPENING_BALANCE = "Kasse vorher";
    public static final String HEADER_OPENING_BALANCE_DATE = "Zählzeit vorher";
    public static final String HEADER_FINAL_BALANCE = "Kasse nachher";
    public static final String HEADER_FINAL_BALANCE_DATE = "Zählzeit nachher";
    public static final String HEADER_OPENING_STOCK = "Anfangsbestand";
    public static final String HEADER_FINAL_STOCK = "Endbestand";
    public static final String HEADER_BEVERAGES_TOTAL = "Getränke Total";
    public static final String HEADER_REVENUE = "Umsatz";
    public static final String HEADER_SOLI = "Soli";

    private int number;
    private double openingBalance = 0.0;
    private Date openingBalanceDate = null;
    private double finalBalance = 0.0;
    private Date finalBalanceDate = null;
    private String openingStockFilename = "";
    private String finalStockFilename = "";
    private double beveragesTotal = 0.0;
    private double revenue = 0.0;
    private double soli = 0.0;

    public Sheet(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public Date getOpeningBalanceDate() {
        return openingBalanceDate;
    }

    public double getFinalBalance() {
        return finalBalance;
    }

    public Date getFinalBalanceDate() {
        return finalBalanceDate;
    }

    public String getOpeningStockFilename() {
        return openingStockFilename;
    }

    public String getFinalStockFilename() {
        return finalStockFilename;
    }

    public double getBeveragesTotal() {
        return beveragesTotal;
    }

    public double getRevenue() {
        return revenue;
    }

    public double getSoli() {
        return soli;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public void setOpeningBalanceDate(Date openingBalanceDate) {
        this.openingBalanceDate = openingBalanceDate;
    }

    public void setFinalBalance(double finalBalance) {
        this.finalBalance = finalBalance;
    }

    public void setFinalBalanceDate(Date finalBalanceDate) {
        this.finalBalanceDate = finalBalanceDate;
    }

    public void setOpeningStockFilename(String openingStockFilename) {
        this.openingStockFilename = openingStockFilename;
    }

    public void setFinalStockFilename(String finalStockFilename) {
        this.finalStockFilename = finalStockFilename;
    }

    public void setBeveragesTotal(double beverageTotal) {
        this.beveragesTotal = beverageTotal;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setSoli(double soli) {
        this.soli = soli;
    }
}
