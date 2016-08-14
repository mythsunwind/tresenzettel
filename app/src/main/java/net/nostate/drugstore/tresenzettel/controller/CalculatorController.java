package net.nostate.drugstore.tresenzettel.controller;

import net.nostate.drugstore.tresenzettel.CalculationAdapter;
import net.nostate.drugstore.tresenzettel.models.Beverage;
import net.nostate.drugstore.tresenzettel.models.CalculationLogEntry;

import java.math.MathContext;
import java.util.List;

import static net.nostate.drugstore.tresenzettel.models.CalculationLogEntry.LogLevel;

public class CalculatorController {

    private static MathContext mathContext = new MathContext(2);

    private static final String TAG = CalculatorController.class.getSimpleName();

    public static void checkStock(List<Beverage> getraenkeVorher, List<Beverage> getraenkeNachher) {
        // Checke ob eine Getränke-Art vorher da war, aber nicht mehr nachher
        for (Beverage getraenkVorher : getraenkeVorher) {
            for (Beverage getraenkNachher : getraenkeNachher) {

            }
        }

        // Checke ob eine neue Getränke-Art nachher da ist
    }

    public static double getBeveragesTotal(CalculationAdapter adapter, List<Beverage> openingBeverages, List<Beverage> finalBeverages) {
        double beveragesTotal = 0.0;
        for (Beverage openingBeverage : openingBeverages) {
            adapter.add(new CalculationLogEntry(LogLevel.INFO, "Kalkuliere " + openingBeverage.getName() + "..."));
            for (Beverage finalBeverage : finalBeverages) {
                if (openingBeverage.getName().equals(finalBeverage.getName())) {
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Getränk in Endbestand gefunden: " + openingBeverage.getName()));
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kästen vorher: " + openingBeverage.getCases()));
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Flaschen vorher: " + openingBeverage.getBottles()));
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kästen nachher: " + finalBeverage.getCases()));
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Flaschen nachher: " + finalBeverage.getBottles()));
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Flaschen pro Kasten: " + openingBeverage.getBottlesPerCase()));
                    int openingBottles = openingBeverage.getCases() * openingBeverage.getBottlesPerCase() + openingBeverage.getBottles();
                    int finalBottles = finalBeverage.getCases() * finalBeverage.getBottlesPerCase() + finalBeverage.getBottles();
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Insgesamt Flaschen vorher: " + openingBottles));
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Insgesamt Flaschen nachher: " + finalBottles));

                    int diffBottles = openingBottles - finalBottles;

                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, diffBottles + " Flaschen weniger bei " + openingBeverage.getName()));

                    double versoffen = diffBottles * openingBeverage.getSKP();
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, diffBottles + " Flaschen * SKP von " + openingBeverage.getSKP() + " = " + versoffen));

                    beveragesTotal = beveragesTotal + versoffen;
                    adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Neuer Total-Wert: " + beveragesTotal));
                }
            }
        }

        return beveragesTotal;
    }

    public static double getRevenue(CalculationAdapter adapter, double beveragesTotal, double openingBalance, double finalBalance) {
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kalkuliere Umsatz..."));
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kasse vorher = " + openingBalance));
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kasse nachher = " + finalBalance));
        if (finalBalance < openingBalance) {
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Kasse nachher ist kleiner als Kasse vorher!"));
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Wo ist das Geld geblieben?"));
            return 0;
        }
        double whatShouldBeIn = openingBalance + beveragesTotal;
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Was sollte drin sein (Kasse vorher + Getränke Total) = " + whatShouldBeIn));
        if (finalBalance < whatShouldBeIn) {
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Kasse nachher ist kleiner als Kasse vorher + Getränke Total!"));
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Bezahlt mal euern Suff!"));
            return finalBalance - openingBalance;
        } else {
            return beveragesTotal;
        }
    }

    public static double getSoli(CalculationAdapter adapter, double beveragesTotal, double openingBalance, double finalBalance) {
        double whatShouldBeIn = openingBalance + beveragesTotal;
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kalkuliere Soli..."));
        if (finalBalance < openingBalance) {
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Kein Geld, kein Soli!"));
            return 0;
        }
        if (finalBalance < whatShouldBeIn) {
            adapter.add(new CalculationLogEntry(LogLevel.ERROR, "Kostenlos gesoffen, daher kein Soli :("));
            return 0;
        }
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Was sollte drin sein = " + whatShouldBeIn));
        adapter.add(new CalculationLogEntry(LogLevel.VERBOSE, "Kasse nachher = " + finalBalance));
        return finalBalance - whatShouldBeIn;
    }
}
