package net.nostate.drugstore.tresenzettel;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class BalanceActivity extends AppCompatActivity {

    private NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

    private TextView balanceTextView;
    private EditText balanceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        getSupportActionBar().setTitle("Kasse");

        Intent intent = getIntent();
        String string = intent.getExtras().getString("string");
        Double balance = intent.getExtras().getDouble("balance");

        balanceTextView = (TextView) findViewById(R.id.BalanceTextView);
        balanceEditText = (EditText) findViewById(R.id.BalanceEditText);

        balanceTextView.setText(string);
        balanceEditText.setText(format.format(balance));
        balanceEditText.setSelection(balanceEditText.getText().length());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        try {
            intent.putExtra("balance", format.parse(balanceEditText.getText().toString()).doubleValue());
            setResult(Activity.RESULT_OK, intent);
        } catch (ParseException e) {
            setResult(Activity.RESULT_CANCELED, intent);
        }
        finish();
        super.onBackPressed();
    }
}
