package net.nostate.drugstore.tresenzettel.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import net.nostate.drugstore.tresenzettel.R;
import net.nostate.drugstore.tresenzettel.exceptions.ImportException;
import net.nostate.drugstore.tresenzettel.exceptions.LoadStockException;
import net.nostate.drugstore.tresenzettel.models.Beverage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockFileController {

    public static final String STOCK_FILE_DATEFORMAT = "yyyy-MM-dd-HHmm";
    private static final String TAG = StockFileController.class.getSimpleName();
    private static final String IMPORT_FILE = "Import.csv";

    private static NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

    public static List<Beverage> loadStockFromFile(Context context, String filename) throws ImportException, LoadStockException {
        List<Beverage> beverages = new ArrayList<>();

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);

        try {
            FileInputStream fileIn = new FileInputStream(file);
            try {
                InputStreamReader reader = new InputStreamReader(fileIn);
                final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
                for (final CSVRecord record : parser) {
                    Beverage beverage = new Beverage(record.get(Beverage.HEADER_NAME));
                    beverage.setBottlesPerCase(Integer.parseInt(record.get(Beverage.HEADER_BOTTLES_PER_CASE)));
                    beverage.setSKP(format.parse(record.get(Beverage.HEADER_SKP)).doubleValue());
                    beverage.setVKP(format.parse(record.get(Beverage.HEADER_VKP)).doubleValue());
                    beverage.setCases(Integer.parseInt(record.get(Beverage.HEADER_CASES)));
                    beverage.setBottles(Integer.parseInt(record.get(Beverage.HEADER_BOTTLES)));
                    beverages.add(beverage);
                }
                parser.close();
                reader.close();
                return beverages;
            } catch (IOException | ParseException | NumberFormatException e) {
                throw new LoadStockException(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.v(TAG, "Couldn't find file " + filename + " will create new one.");
            return createNewFileFromImport(context, filename);
        }
    }

    public static List<Beverage> createNewFileFromImport(Context context, String filename) throws ImportException {
        List<Beverage> beverages;
        try {
            beverages = loadImportFile(context);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not find import file. Generate sample file by myself...");
            writeSampleImportFile(context);
            try {
                beverages = loadImportFile(context);
            } catch (FileNotFoundException e2) {
                throw new ImportException(e2.getMessage());
            }
        }
        Log.v(TAG, "Imported successfully!");
        return createNewStockFile(context, filename, beverages);
    }

    public static void saveStockToFile(Context context, String filename, List<Beverage> beverages) {
        createNewStockFile(context, filename, beverages);
    }

    private static List<Beverage> createNewStockFile(Context context, String filename, List<Beverage> beverages) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);
        FileOutputStream fileout = null;
        try {
            fileout = new FileOutputStream(file);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            // write header
            outputWriter.write("\"");
            outputWriter.write(Beverage.HEADER_NAME);
            outputWriter.write("\",\"");
            outputWriter.write(Beverage.HEADER_BOTTLES_PER_CASE);
            outputWriter.write("\",\"");
            outputWriter.write(Beverage.HEADER_SKP);
            outputWriter.write("\",\"");
            outputWriter.write(Beverage.HEADER_VKP);
            outputWriter.write("\",\"");
            outputWriter.write(Beverage.HEADER_CASES);
            outputWriter.write("\",\"");
            outputWriter.write(Beverage.HEADER_BOTTLES);
            outputWriter.write("\"\n");
            // write empty line for each beverage
            for (Beverage beverage : beverages) {
                outputWriter.write("\"");
                outputWriter.write(beverage.getName());
                outputWriter.write("\",\"");
                outputWriter.write(String.valueOf(beverage.getBottlesPerCase()));
                outputWriter.write("\",\"");
                outputWriter.write(format.format(beverage.getSKP()));
                outputWriter.write("\",\"");
                outputWriter.write(format.format(beverage.getVKP()));
                outputWriter.write("\",\"");
                outputWriter.write(String.valueOf(beverage.getCases()));
                outputWriter.write("\",\"");
                outputWriter.write(String.valueOf(beverage.getBottles()));
                outputWriter.write("\"\n");
            }
            outputWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not create new file from import: " + e.getMessage());
        } finally {
            try {
                fileout.flush();
                fileout.close();
            } catch (IOException e) {
                Log.e(TAG, "Couldn't close file: " + e.getMessage());
            }
        }
        return beverages;
    }

    private static List<Beverage> loadImportFile(Context context) throws ImportException, FileNotFoundException {
        List<Beverage> beverages = new ArrayList<>();

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), IMPORT_FILE);
        FileInputStream fileIn = new FileInputStream(file);

        try {
            InputStreamReader reader = new InputStreamReader(fileIn);

            final CSVParser parser;
            parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
            for (final CSVRecord record : parser) {
                Beverage beverage = new Beverage(record.get(Beverage.HEADER_NAME));
                beverage.setBottlesPerCase(Integer.parseInt(record.get(Beverage.HEADER_BOTTLES_PER_CASE)));
                beverage.setSKP(format.parse(record.get(Beverage.HEADER_SKP)).doubleValue());
                beverage.setVKP(format.parse(record.get(Beverage.HEADER_VKP)).doubleValue());
                beverage.setCases(0);
                beverage.setBottles(0);
                beverages.add(beverage);
            }
            parser.close();
            reader.close();
        } catch (Exception e) {
            throw new ImportException("Failed to read import file: " + e.getMessage());
        }
        return beverages;
    }

    private static void writeSampleImportFile(Context context) {
        FileOutputStream out = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), IMPORT_FILE);
            out = new FileOutputStream(file);
            InputStream in = context.getResources().openRawResource(R.raw.importcsv);
            byte[] buffer = new byte[1024];
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not create sample import file: " + e.getMessage());
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Couldn't close file: " + e.getMessage());
            }
        }
    }
}