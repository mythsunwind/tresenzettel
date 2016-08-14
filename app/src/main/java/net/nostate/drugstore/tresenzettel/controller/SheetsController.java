package net.nostate.drugstore.tresenzettel.controller;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import net.nostate.drugstore.tresenzettel.exceptions.LoadSheetsException;
import net.nostate.drugstore.tresenzettel.models.Sheet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SheetsController {

    private static final String TAG = SheetsController.class.getSimpleName();
    private static final String ALL_SHEETS_FILE = "AlleTresenzettel.csv";
    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm";

    private static NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

    public static List<Sheet> getAllSheets(Context context) throws LoadSheetsException {
        List<Sheet> sheets = new ArrayList<>();
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(getAllSheetsFile(context));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Couldn't load file: " + e.getMessage());
        }
        InputStreamReader reader = new InputStreamReader(fileIn);
        try {
            final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
            for (final CSVRecord record : parser) {
                Sheet sheet = new Sheet(Integer.parseInt(record.get(Sheet.HEADER_NUMBER)));
                sheet.setOpeningBalance(format.parse(record.get(Sheet.HEADER_OPENING_BALANCE)).doubleValue());
                SimpleDateFormat date = new SimpleDateFormat(DATEFORMAT);
                String openingBalanceDate = record.get(Sheet.HEADER_OPENING_BALANCE_DATE);
                if (!openingBalanceDate.isEmpty()) {
                    sheet.setOpeningBalanceDate(date.parse(openingBalanceDate));
                }
                sheet.setFinalBalance(format.parse(record.get(Sheet.HEADER_FINAL_BALANCE)).doubleValue());
                String finalBalanceDate = record.get(Sheet.HEADER_FINAL_BALANCE_DATE);
                if (!finalBalanceDate.isEmpty()) {
                    sheet.setFinalBalanceDate(date.parse(finalBalanceDate));
                }
                sheet.setOpeningStockFilename(record.get(Sheet.HEADER_OPENING_STOCK));
                sheet.setFinalStockFilename(record.get(Sheet.HEADER_FINAL_STOCK));
                sheet.setBeveragesTotal(format.parse(record.get(Sheet.HEADER_BEVERAGES_TOTAL)).doubleValue());
                sheet.setRevenue(format.parse(record.get(Sheet.HEADER_REVENUE)).doubleValue());
                sheet.setSoli(format.parse(record.get(Sheet.HEADER_SOLI)).doubleValue());
                sheets.add(sheet);
            }
            parser.close();
        } catch(ParseException | IOException e) {
            throw new LoadSheetsException("Couldn't parse file " + ALL_SHEETS_FILE + ":" + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close import file: " + e.getMessage());
            }
        }
        return sheets;
    }

    private static File getAllSheetsFile(Context context) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), ALL_SHEETS_FILE);
        if (!file.exists()) {
            Log.v(TAG, "Couldn't find existing " + ALL_SHEETS_FILE + ". Create new file for all sheets...");
            createNewSheetsFile(file);
        }
        return file;
    }

    private static void createNewSheetsFile(File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        }
        OutputStreamWriter outputWriter = new OutputStreamWriter(out);
        try {
            writeHeader(outputWriter);
        } catch (IOException e) {
            Log.e(TAG, "Could not write import file: " + e.getMessage());
        }
        try {
            outputWriter.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close import file: " + e.getMessage());
        }
    }

    private static void writeHeader(OutputStreamWriter outputWriter) throws IOException {
        outputWriter.write("\"");
        outputWriter.write(Sheet.HEADER_NUMBER);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_OPENING_BALANCE);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_OPENING_BALANCE_DATE);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_FINAL_BALANCE);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_FINAL_BALANCE_DATE);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_OPENING_STOCK);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_FINAL_STOCK);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_BEVERAGES_TOTAL);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_REVENUE);
        outputWriter.write("\",\"");
        outputWriter.write(Sheet.HEADER_SOLI);
        outputWriter.write("\"\n");
    }

    private static Sheet createNewSheetInFile(Context context, int sheetNumber) throws LoadSheetsException {
        Sheet sheet = new Sheet(sheetNumber);
        List<Sheet> sheets = getAllSheets(context);
        sheets.add(sheet);
        overwriteFile(context, sheets);
        return sheet;
    }

    public static void saveOpeningBalance(Context context, int sheetNumber, double balance) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setOpeningBalance(balance);
        updatedSheet.setOpeningBalanceDate(new Date());
        updateSheetInFile(context, updatedSheet);
    }

    public static void saveOpeningStockFilename(Context context, int sheetNumber, String filename) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setOpeningStockFilename(filename);
        updateSheetInFile(context, updatedSheet);
    }

    public static void saveFinalStockFilename(Context context, int sheetNumber, String filename) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setFinalStockFilename(filename);
        updateSheetInFile(context, updatedSheet);
    }

    public static void saveBeverageTotal(Context context, int sheetNumber, double beverageTotal) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setBeveragesTotal(beverageTotal);
        updateSheetInFile(context, updatedSheet);
    }

    public static void saveRevenue(Context context, int sheetNumber, double revenue) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setRevenue(revenue);
        updateSheetInFile(context, updatedSheet);
    }

    public static void saveSoli(Context context, int sheetNumber, double soli) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setSoli(soli);
        updateSheetInFile(context, updatedSheet);
    }

    public static void saveFinalBalance(Context context, int sheetNumber, double balance) throws LoadSheetsException {
        Sheet updatedSheet = getSheet(context, sheetNumber);
        updatedSheet.setFinalBalance(balance);
        updatedSheet.setFinalBalanceDate(new Date());
        updateSheetInFile(context, updatedSheet);
    }

    private static void updateSheetInFile(Context context, Sheet updatedSheet) throws LoadSheetsException {
        List<Sheet> updatedSheets = new ArrayList<>();

        List<Sheet> sheets = getAllSheets(context);
        for (Sheet sheet : sheets) {
            if (sheet.getNumber() == updatedSheet.getNumber()) {
                updatedSheets.add(updatedSheet);
            } else {
                updatedSheets.add(sheet);
            }
        }
        overwriteFile(context, updatedSheets);
    }

    private static void overwriteFile(Context context, List<Sheet> sheets) {
        File file = getAllSheetsFile(context);
        createNewSheetsFile(file);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        }
        OutputStreamWriter outputWriter = new OutputStreamWriter(out);
        try {
            writeHeader(outputWriter);
            for (Sheet sheet : sheets) {
                outputWriter.write("\"");
                outputWriter.write(Integer.toString(sheet.getNumber()));
                outputWriter.write("\",\"");
                outputWriter.write(format.format(sheet.getOpeningBalance()));
                outputWriter.write("\",\"");
                if(sheet.getOpeningBalanceDate() != null) {
                    outputWriter.write(DateFormat.format(DATEFORMAT, sheet.getOpeningBalanceDate()).toString());
                }
                outputWriter.write("\",\"");
                outputWriter.write(format.format(sheet.getFinalBalance()));
                outputWriter.write("\",\"");
                if(sheet.getFinalBalanceDate() != null) {
                    outputWriter.write(DateFormat.format(DATEFORMAT, sheet.getFinalBalanceDate()).toString());
                }
                outputWriter.write("\",\"");
                outputWriter.write(sheet.getOpeningStockFilename());
                outputWriter.write("\",\"");
                outputWriter.write(sheet.getFinalStockFilename());
                outputWriter.write("\",\"");
                outputWriter.write(format.format(sheet.getBeveragesTotal()));
                outputWriter.write("\",\"");
                outputWriter.write(format.format(sheet.getRevenue()));
                outputWriter.write("\",\"");
                outputWriter.write(format.format(sheet.getSoli()));
                outputWriter.write("\"\n");
            }
            outputWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not write file: " + e.getMessage());
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close file: " + e.getMessage());
            }
        }
    }

    public static Sheet getSheet(Context context, int sheetNumber) throws LoadSheetsException {
        List<Sheet> sheets = getAllSheets(context);
        for (Sheet sheet : sheets) {
            if (sheet.getNumber() == sheetNumber) {
                return sheet;
            }
        }
        return createNewSheetInFile(context, sheetNumber);
    }
}
