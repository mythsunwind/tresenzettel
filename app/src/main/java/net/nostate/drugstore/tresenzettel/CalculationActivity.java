package net.nostate.drugstore.tresenzettel;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import net.nostate.drugstore.tresenzettel.controller.CalculatorController;
import net.nostate.drugstore.tresenzettel.controller.CalculatorTask;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        // Construct the data source
        adapter = new CalculationAdapter(this, new ArrayList<CalculationLogEntry>());
        ListView listView = (ListView) findViewById(R.id.CalculationListView);
        listView.setAdapter(adapter);

        int sheetNumber = getIntent().getExtras().getInt("sheetNumber");

        Object[] objects = new Object[2];
        objects[0] = sheetNumber;
        objects[1] = adapter;
        new CalculatorTask().execute(objects);
    }
}
