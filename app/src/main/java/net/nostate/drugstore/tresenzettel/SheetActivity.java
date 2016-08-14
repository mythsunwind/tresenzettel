package net.nostate.drugstore.tresenzettel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.nostate.drugstore.tresenzettel.controller.CalculatorController;
import net.nostate.drugstore.tresenzettel.controller.SheetsController;
import net.nostate.drugstore.tresenzettel.controller.StockFileController;
import net.nostate.drugstore.tresenzettel.exceptions.LoadSheetsException;
import net.nostate.drugstore.tresenzettel.models.Beverage;
import net.nostate.drugstore.tresenzettel.models.Sheet;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SheetActivity extends AppCompatActivity {

    private static final String TAG = SheetActivity.class.getSimpleName();
    private static final int OPENING_BALANCE_ACTIVITY = 10;
    private static final int FINAL_BALANCE_ACTIVITY = 20;
    private Button openingBalanceButton;
    private Button openingStockButton;
    private Button finalBalanceButton;
    private Button finalStockButton;
    private Button calculationButton;

    private NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

    private int sheetNumber;
    private boolean changed = false;
    private Sheet sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        openingBalanceButton = (Button) findViewById(R.id.OpeningBalanceButton);
        openingStockButton = (Button) findViewById(R.id.OpeningStockButton);
        finalBalanceButton = (Button) findViewById(R.id.FinalBalanceButton);
        finalStockButton = (Button) findViewById(R.id.FinalStockButton);
        calculationButton = (Button) findViewById(R.id.CalculationButton);

        Intent intent = getIntent();

        // Check if new sheet or not
        if (intent.hasExtra("SheetNumber")) {
            sheetNumber = intent.getExtras().getInt("SheetNumber");
            try {
                sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
            } catch (LoadSheetsException e) {
                Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }

            if (sheet.getOpeningBalance() != 0.0) {
                openingBalanceButton.setText(getString(R.string.count_opening_balance) + ": "
                        + format.format(sheet.getOpeningBalance()));
            }

            if (sheet.getFinalBalance() != 0.0) {
                finalBalanceButton.setText(getString(R.string.count_final_balance) + ": "
                        + format.format(sheet.getFinalBalance()));
            }

            enableButtons(sheet);
        } else {
            // get last sheet number
            int lastSheetNumber = intent.getExtras().getInt("LastSheetNumber");
            sheetNumber = lastSheetNumber + 1;
            finalBalanceButton.setEnabled(false);
            finalStockButton.setEnabled(false);
        }

        getSupportActionBar().setTitle("Tresenzettel #" + sheetNumber);

        openingBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changed = true;
                Intent intent = new Intent(getApplicationContext(), BalanceActivity.class);
                try {
                    sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                intent.putExtra("string", getString(R.string.count_opening_balance));
                intent.putExtra("balance", sheet.getOpeningBalance());
                startActivityForResult(intent, OPENING_BALANCE_ACTIVITY);
            }
        });

        openingStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changed = true;
                Intent intent = new Intent(getApplicationContext(), StockActivity.class);
                try {
                    sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                intent.putExtra("sheetNumber", sheet.getNumber());
                intent.putExtra("stock", Sheet.HEADER_OPENING_STOCK);
                intent.putExtra("filename", sheet.getOpeningStockFilename());
                startActivity(intent);
            }
        });

        finalBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changed = true;
                Intent intent = new Intent(getApplicationContext(), BalanceActivity.class);
                try {
                    sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                intent.putExtra("string", getString(R.string.count_final_balance));
                intent.putExtra("balance", sheet.getFinalBalance());
                startActivityForResult(intent, FINAL_BALANCE_ACTIVITY);
            }
        });

        finalStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changed = true;
                Intent intent = new Intent(getApplicationContext(), StockActivity.class);
                try {
                    sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                intent.putExtra("sheetNumber", sheet.getNumber());
                intent.putExtra("stock", Sheet.HEADER_FINAL_STOCK);
                intent.putExtra("filename", sheet.getFinalStockFilename());
                startActivity(intent);
            }
        });

        calculationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalculationActivity.class);
                try {
                    sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                intent.putExtra("sheetNumber", sheet.getNumber());
                startActivity(intent);
            }
        });
    }

    private void enableButtons(Sheet sheet) {
        if (sheet.getOpeningBalance() != 0.0 && !sheet.getOpeningStockFilename().equals("")) {
            finalBalanceButton.setEnabled(true);
            finalStockButton.setEnabled(true);
            if (sheet.getFinalBalance() != 0.0 && !sheet.getFinalStockFilename().equals("")) {
                calculationButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case OPENING_BALANCE_ACTIVITY:
                Double openingBalance = intent.getExtras().getDouble("balance");
                openingBalanceButton.setText(getString(R.string.count_opening_balance) + ": " + format.format(openingBalance));
                try {
                    SheetsController.saveOpeningBalance(getApplicationContext(), sheetNumber, openingBalance);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                break;
            case FINAL_BALANCE_ACTIVITY:
                Double finalBalance = intent.getExtras().getDouble("balance");
                finalBalanceButton.setText(getString(R.string.count_final_balance) + ": " + format.format(finalBalance));
                try {
                    SheetsController.saveFinalBalance(getApplicationContext(), sheetNumber, finalBalance);
                } catch (LoadSheetsException e) {
                    Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                break;
        }

        try {
            sheet = SheetsController.getSheet(getApplicationContext(), sheetNumber);
        } catch (LoadSheetsException e) {
            Snackbar.make(findViewById(R.id.sheetLayout), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        enableButtons(sheet);
    }

    @Override
    public void onBackPressed() {
        if (changed) {
            setResult(Activity.RESULT_OK);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
        super.onBackPressed();
    }
}
