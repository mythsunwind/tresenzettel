package net.nostate.drugstore.tresenzettel.controller;

import android.util.Log;

import net.nostate.drugstore.tresenzettel.exceptions.CalculatorException;
import net.nostate.drugstore.tresenzettel.models.Beverage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class CalculatorController {

    private static MathContext mathContext = new MathContext(2);

    private static final String TAG = CalculatorController.class.getSimpleName();

    public static void checkStock(List<Beverage> getraenkeVorher, List<Beverage> getraenkeNachher) {
        // Checke ob eine Getränke-Art vorher da war, aber nicht mehr nachher
        for(Beverage getraenkVorher: getraenkeVorher) {
            for (Beverage getraenkNachher : getraenkeNachher) {

            }
        }

        // Checke ob eine neue Getränke-Art nachher da ist
    }

    public static double getGetraenkeTotal(List<Beverage> getraenkeVorher, List<Beverage> getraenkeNachher) {
        double getraenkeTotal = 0.0;
        for(Beverage getraenkVorher: getraenkeVorher) {
            Log.v(TAG, "Kalkuliere " + getraenkVorher.getName() + "...");
            for(Beverage getraenkNachher: getraenkeNachher) {
                if(getraenkVorher.getName().equals(getraenkNachher.getName())) {
                    Log.v(TAG, "Getränk in Endbestand gefunden: " + getraenkVorher.getName());
                    Log.v(TAG, "Kästen vorher: " + getraenkVorher.getCases());
                    Log.v(TAG, "Flaschen vorher: " + getraenkVorher.getBottles());
                    Log.v(TAG, "Kästen nachher: " + getraenkNachher.getCases());
                    Log.v(TAG, "Flaschen nachher: " + getraenkNachher.getBottles());
                    Log.v(TAG, "Flaschen pro Kasten: " + getraenkVorher.getBottlesPerCase());
                    int flaschenVorher = getraenkVorher.getCases() * getraenkVorher.getBottlesPerCase() + getraenkVorher.getBottles();
                    int flaschenNachher = getraenkNachher.getCases() * getraenkNachher.getBottlesPerCase() + getraenkNachher.getBottles();
                    Log.v(TAG, "Insgesamt Flaschen vorher: " + flaschenVorher);
                    Log.v(TAG, "Insgesamt Flaschen nachher: " + flaschenNachher);

                    int wenigerFlaschen = flaschenVorher - flaschenNachher;

                    Log.v(TAG, wenigerFlaschen + " Flaschen weniger bei " + getraenkVorher.getName());

                    double versoffen = wenigerFlaschen * getraenkVorher.getSKP();
                    Log.v(TAG, wenigerFlaschen + " Flaschen * SKP von " + getraenkVorher.getSKP() + " = " + versoffen);

                    getraenkeTotal = getraenkeTotal + versoffen;
                    Log.v(TAG, "Neuer Total-Wert: " + getraenkeTotal);
                }
            }
        }

        return getraenkeTotal;
    }
}
