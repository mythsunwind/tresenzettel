package net.nostate.drugstore.tresenzettel;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.nostate.drugstore.tresenzettel.controller.SheetsController;
import net.nostate.drugstore.tresenzettel.controller.StockFileController;
import net.nostate.drugstore.tresenzettel.exceptions.ImportException;
import net.nostate.drugstore.tresenzettel.exceptions.LoadSheetsException;
import net.nostate.drugstore.tresenzettel.exceptions.LoadStockException;
import net.nostate.drugstore.tresenzettel.models.Beverage;
import net.nostate.drugstore.tresenzettel.models.Sheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    private static final String TAG = StockActivity.class.getSimpleName();
    private static final int COUNT_BEVERAGE_ACTIVITY = 10;
    public static final String COUNT_BEVERAGE_ACTIVITY_ITEMID = "ITEMID";
    public static final String COUNT_BEVERAGE_ACTIVITY_NAME = "NAME";
    public static final String COUNT_BEVERAGE_ACTIVITY_CASES = "CASES";
    public static final String COUNT_BEVERAGE_ACTIVITY_BOTTLES = "BOTTLES";
    public static final String COUNT_BEVERAGE_ACTIVITY_BOTTLES_PER_CASE = "BOTTLES_PER_CASE";
    public static final String COUNT_BEVERAGE_ACTIVITY_SKP = "SKP";

    private List<Beverage> beverages = new ArrayList<>();
    private BeverageCountAdapter adapter;
    private Date date;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        Intent intent = getIntent();
        String stock = intent.getExtras().getString("stock");
        filename = intent.getExtras().getString("filename");

        if (filename == null || filename.isEmpty()) {
            date = new Date();
            String dateLabel = DateFormat.format(StockFileController.STOCK_FILE_DATEFORMAT, date).toString();
            filename = dateLabel + " " + stock + ".csv";
            int sheetNumber = intent.getExtras().getInt("sheetNumber");
            if(stock.equals(Sheet.HEADER_OPENING_STOCK)) {
                try {
                    SheetsController.saveOpeningStockFilename(getApplicationContext(), sheetNumber, filename);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.stockListView), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
            } else {
                try {
                    SheetsController.saveFinalStockFilename(getApplicationContext(), sheetNumber, filename);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.stockListView), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
            }

            try {
                beverages = StockFileController.createNewFileFromImport(getApplicationContext(), filename);
            } catch (ImportException e) {
                Snackbar.make(findViewById(R.id.stockListView), "Import failed: " + e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
        } else {
            if (filename.split(" ").length > 1) {
                SimpleDateFormat format = new SimpleDateFormat(StockFileController.STOCK_FILE_DATEFORMAT);

                String dateString = filename.split(" ")[0];
                try {
                    date = format.parse(dateString);
                } catch (ParseException e) {
                    Snackbar.make(findViewById(R.id.stockListView), "Couldn't parse Date from filename " + filename + ":" + e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
            } else {
                Snackbar.make(findViewById(R.id.stockListView), "Couldn't parse Date from filename " + filename, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
            try {
                beverages = StockFileController.loadStockFromFile(getApplicationContext(), filename);
            } catch (ImportException | LoadStockException e) {
                Snackbar.make(findViewById(R.id.stockListView), "Load stock failed: " + e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
        }

        getSupportActionBar().setTitle(stock + " " + formatForTitle(date));

        // Construct the data source
        adapter = new BeverageCountAdapter(this, beverages);
        ListView listView = (ListView) findViewById(R.id.stockListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BeverageCountActivity.class);
                intent.putExtra(COUNT_BEVERAGE_ACTIVITY_ITEMID, position);
                intent.putExtra(COUNT_BEVERAGE_ACTIVITY_NAME, beverages.get(position).getName());
                intent.putExtra(COUNT_BEVERAGE_ACTIVITY_CASES, beverages.get(position).getCases());
                intent.putExtra(COUNT_BEVERAGE_ACTIVITY_BOTTLES, beverages.get(position).getBottles());
                intent.putExtra(COUNT_BEVERAGE_ACTIVITY_BOTTLES_PER_CASE, beverages.get(position).getBottlesPerCase());
                intent.putExtra(COUNT_BEVERAGE_ACTIVITY_SKP, beverages.get(position).getSKP());
                startActivityForResult(intent, COUNT_BEVERAGE_ACTIVITY);
            }
        });
    }

    private String formatForTitle(Date date) {
        DateFormat dateFormat = new DateFormat();
        return dateFormat.format("EEEE, d.M.yyyy HH:mm", date).toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case COUNT_BEVERAGE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Log.v(TAG, "Modify beverage");
                    int itemId = intent.getExtras().getInt(COUNT_BEVERAGE_ACTIVITY_ITEMID);
                    int cases = intent.getExtras().getInt(COUNT_BEVERAGE_ACTIVITY_CASES);
                    int bottles = intent.getExtras().getInt(COUNT_BEVERAGE_ACTIVITY_BOTTLES);
                    Beverage modifiedBeverage = beverages.get(itemId);
                    modifiedBeverage.setCases(cases);
                    modifiedBeverage.setBottles(bottles);
                    beverages.set(itemId, modifiedBeverage);
                    StockFileController.saveStockToFile(getApplicationContext(), filename, beverages);
                    adapter.remove(adapter.getItem(itemId));
                    adapter.insert(modifiedBeverage, itemId);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
