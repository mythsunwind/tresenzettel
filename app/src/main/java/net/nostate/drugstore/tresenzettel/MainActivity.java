package net.nostate.drugstore.tresenzettel;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.nostate.drugstore.tresenzettel.controller.SheetsController;
import net.nostate.drugstore.tresenzettel.exceptions.LoadSheetsException;
import net.nostate.drugstore.tresenzettel.models.Sheet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NEW_SHEET_ACTIVITY = 10;
    private static final int EDIT_SHEET_ACTIVITY = 20;

    private SheetAdapter adapter;
    private List<Sheet> sheets = new ArrayList<>();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // get file named AlleTresenzettel.csv from DIRECTORY_DOCUMENTS
        // create row for each row in file
        // if AlleTresenzettel.csv does not exist, create file
        try {
            sheets = SheetsController.getAllSheets(getApplicationContext());
        } catch (LoadSheetsException e) {
            Snackbar.make(findViewById(R.id.sheetsListView), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            fab.setEnabled(false);
            return;
        }

        adapter = new SheetAdapter(this, sheets);
        ListView listView = (ListView) findViewById(R.id.sheetsListView);
        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SheetActivity.class);
                intent.putExtra("LastSheetNumber", getLastSheetNumber());
                startActivityForResult(intent, NEW_SHEET_ACTIVITY);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SheetActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("SheetNumber", adapter.getItem(position).getNumber());
                startActivityForResult(intent, EDIT_SHEET_ACTIVITY);
            }
        });
    }

    private int getLastSheetNumber() {
        int lastSheetNumber = 0;

        if(!sheets.isEmpty()) {
            for(Sheet sheet: sheets) {
                if(sheet.getNumber() > lastSheetNumber) {
                    lastSheetNumber = sheet.getNumber();
                }
            }
        }

        return lastSheetNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK) {
            adapter.clear();
            try {
                adapter.addAll(SheetsController.getAllSheets(getApplicationContext()));
            } catch (LoadSheetsException e) {
                Snackbar.make(findViewById(R.id.sheetsListView), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
        }
    }
}
