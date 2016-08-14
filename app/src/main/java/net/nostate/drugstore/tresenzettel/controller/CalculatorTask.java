package net.nostate.drugstore.tresenzettel.controller;

import android.os.AsyncTask;
import android.util.Log;

import net.nostate.drugstore.tresenzettel.CalculationAdapter;
import net.nostate.drugstore.tresenzettel.exceptions.LoadSheetsException;
import net.nostate.drugstore.tresenzettel.models.Beverage;
import net.nostate.drugstore.tresenzettel.models.CalculationLogEntry;
import net.nostate.drugstore.tresenzettel.models.CalculationLogEntry.LogLevel;
import net.nostate.drugstore.tresenzettel.models.Sheet;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CalculatorTask extends AsyncTask {

    private static final String TAG = CalculatorTask.class.getSimpleName();
    private CalculationAdapter adapter;
    private Sheet sheet;

    private NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

    @Override
    protected Object doInBackground(Object[] objects) {
        int sheetNumber = (int) objects[0];
        adapter = (CalculationAdapter) objects[1];

        Log.v(TAG, "Try to calculate...");
        try {
            sheet = SheetsController.getSheet(adapter.getContext(), sheetNumber);
        } catch (LoadSheetsException e) {
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, e.getMessage()));
            return null;
        }

        if(!sheet.getOpeningStockFilename().isEmpty() && !sheet.getFinalStockFilename().isEmpty()) {

            try {
                List<Beverage> openingStock = StockFileController.loadStockFromFile(adapter.getContext(), sheet.getOpeningStockFilename());
                List<Beverage> finalStock = StockFileController.loadStockFromFile(adapter.getContext(), sheet.getFinalStockFilename());

                double beverageTotal = CalculatorController.getBeveragesTotal(adapter, openingStock, finalStock);
                adapter.add(new CalculationLogEntry(LogLevel.RESULT, "Getränke Total:" + format.format(beverageTotal)));

                double openingBalance = sheet.getOpeningBalance();
                double finalBalance = sheet.getFinalBalance();

                double revenue = CalculatorController.getRevenue(adapter, beverageTotal, openingBalance, finalBalance);
                adapter.add(new CalculationLogEntry(LogLevel.RESULT, "Umsatz:" + format.format(revenue)));
                double soli = CalculatorController.getSoli(adapter, beverageTotal, openingBalance, finalBalance);
                adapter.add(new CalculationLogEntry(LogLevel.RESULT, "Soli:" + format.format(soli)));

            } catch (Exception e) {
                Log.e(TAG, "Calculation failed: " + e.getMessage());
                adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Getränke-Kalkulation fehlgeschlagen:" + e.getMessage()));
                return null;
            }
        }

        return null;
    }
}
