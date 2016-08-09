package net.nostate.drugstore.tresenzettel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BeverageCountActivity extends AppCompatActivity {

    private static final String TAG = BeverageCountActivity.class.getSimpleName();

    private int itemId = 0;
    private String name = null;
    private int cases = 0;
    private int bottles = 0;

    private EditText casesEditText;
    private EditText bottlesEditText;
    private TextView beverageNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beverage_count);

        beverageNameTextView = (TextView) findViewById(R.id.beverageNameTextView);
        casesEditText = (EditText) findViewById(R.id.casesEditText);
        bottlesEditText = (EditText) findViewById(R.id.bottlesEditText);

        Intent intent = getIntent();
        itemId = intent.getExtras().getInt(StockActivity.COUNT_BEVERAGE_ACTIVITY_ITEMID, itemId);
        name = intent.getExtras().getString(StockActivity.COUNT_BEVERAGE_ACTIVITY_NAME);
        cases = intent.getExtras().getInt(StockActivity.COUNT_BEVERAGE_ACTIVITY_CASES, cases);
        bottles = intent.getExtras().getInt(StockActivity.COUNT_BEVERAGE_ACTIVITY_BOTTLES, bottles);

        beverageNameTextView.setText(name);
        casesEditText.setText(String.valueOf(cases));
        bottlesEditText.setText(String.valueOf(bottles));

        Button decreaseCasesButton = (Button) findViewById(R.id.decreaseCasesButton);
        decreaseCasesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int current = Integer.parseInt(casesEditText.getText().toString());
                if (current != 0) {
                    casesEditText.setText(String.valueOf(current - 1));
                }
            }
        });

        Button increaseCasesButton = (Button) findViewById(R.id.increaseCasesButton);
        increaseCasesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText casesEditText = (EditText) findViewById(R.id.casesEditText);
                int current = Integer.parseInt(casesEditText.getText().toString());
                casesEditText.setText(String.valueOf(current + 1));
            }
        });

        Button decreaseBottlesButton = (Button) findViewById(R.id.decreaseBottlesButton);
        decreaseBottlesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText bottlesEditText = (EditText) findViewById(R.id.bottlesEditText);
                int current = Integer.parseInt(bottlesEditText.getText().toString());
                if (current != 0) {
                    bottlesEditText.setText(String.valueOf(current - 1));
                }
            }
        });

        Button increaseBottlesButton = (Button) findViewById(R.id.increaseBottlesButton);
        increaseBottlesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText bottlesEditText = (EditText) findViewById(R.id.bottlesEditText);
                int current = Integer.parseInt(bottlesEditText.getText().toString());
                bottlesEditText.setText(String.valueOf(current + 1));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(StockActivity.COUNT_BEVERAGE_ACTIVITY_ITEMID, itemId);
        intent.putExtra(StockActivity.COUNT_BEVERAGE_ACTIVITY_CASES, Integer.parseInt(casesEditText.getText().toString()));
        intent.putExtra(StockActivity.COUNT_BEVERAGE_ACTIVITY_BOTTLES, Integer.parseInt(bottlesEditText.getText().toString()));
        setResult(Activity.RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
