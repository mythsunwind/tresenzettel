package net.nostate.drugstore.tresenzettel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import net.nostate.drugstore.tresenzettel.controller.CalculatorController;
import net.nostate.drugstore.tresenzettel.controller.SheetsController;
import net.nostate.drugstore.tresenzettel.controller.StockFileController;
import net.nostate.drugstore.tresenzettel.exceptions.LoadSheetsException;
import net.nostate.drugstore.tresenzettel.models.Beverage;
import net.nostate.drugstore.tresenzettel.models.CalculationLogEntry;
import net.nostate.drugstore.tresenzettel.models.CalculationLogEntry.LogLevel;
import net.nostate.drugstore.tresenzettel.models.Sheet;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalculationActivity extends AppCompatActivity {

    private static final String TAG = CalculationActivity.class.getSimpleName();
    private CalculationAdapter adapter;
    private Sheet sheet;

    private NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        // Construct the data source
        adapter = new CalculationAdapter(this, new ArrayList<CalculationLogEntry>());
        ListView listView = (ListView) findViewById(R.id.CalculationListView);
        listView.setAdapter(adapter);

        int sheetNumber = getIntent().getExtras().getInt("sheetNumber");

        Log.v(TAG, "Try to calculate...");
        try {
            sheet = SheetsController.getSheet(adapter.getContext(), sheetNumber);
        } catch (LoadSheetsException e) {
            updateUI(LogLevel.ERROR, e.getMessage());
            return;
        }

        if(!sheet.getOpeningStockFilename().isEmpty() && !sheet.getFinalStockFilename().isEmpty()) {
            try {
                List<Beverage> openingStock = StockFileController.loadStockFromFile(adapter.getContext(), sheet.getOpeningStockFilename());
                List<Beverage> finalStock = StockFileController.loadStockFromFile(adapter.getContext(), sheet.getFinalStockFilename());

                double beverageTotal = CalculatorController.getBeveragesTotal(adapter, openingStock, finalStock);
                updateUI(LogLevel.RESULT, "Getränke Total:" + format.format(beverageTotal));

                double openingBalance = sheet.getOpeningBalance();
                double finalBalance = sheet.getFinalBalance();

                double revenue = CalculatorController.getRevenue(adapter, beverageTotal, openingBalance, finalBalance);
                updateUI(LogLevel.RESULT, "Umsatz:" + format.format(revenue));
                double soli = CalculatorController.getSoli(adapter, beverageTotal, openingBalance, finalBalance);
                updateUI(LogLevel.RESULT, "Soli:" + format.format(soli));

                SheetsController.saveBeverageTotal(getApplicationContext(), sheetNumber, beverageTotal);
                SheetsController.saveRevenue(getApplicationContext(), sheetNumber, revenue);
                SheetsController.saveSoli(getApplicationContext(), sheetNumber, soli);
            } catch (Exception e) {
                Log.e(TAG, "Calculation failed: " + e.getMessage());
                updateUI(LogLevel.ERROR, "Getränke-Kalkulation fehlgeschlagen:" + e.getMessage());
                return;
            }
        }
    }

    private void updateUI(LogLevel level, String entry) {
        adapter.add(new CalculationLogEntry(level, entry));
    }
}
