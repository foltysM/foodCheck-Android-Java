package net.foltys.foodcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class SQliteDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "scans";

    public SQliteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE past_scans (_ID INTEGER PRIMARY KEY AUTOINCREMENT, date DATE, barcode INT, name TEXT, weight FLOAT, energy FLOAT, fat FLOAT, saturates FLOAT, carbohydrates FLOAT, sugars FLOAT, fibre FLOAT, protein FLOAT, salt DOUBLE, imURL TEXT, fav BOOLEAN);";
        db.execSQL(sqlCommand);
        sqlCommand = "CREATE TABLE favorite (_ID INTEGER PRIMARY KEY AUTOINCREMENT, barcode INT, name TEXT, weight FLOAT, energy FLOAT, fat FLOAT, saturates FLOAT, carbohydrates FLOAT, sugars FLOAT, fibre FLOAT, protein FLOAT, salt DOUBLE, imURL TEXT);";
        db.execSQL(sqlCommand);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO in case of need to change columns in Database, onDowngrade too
    }

    public void insertPastScans(@org.jetbrains.annotations.NotNull SQLiteDatabase db, int barcode, String name, float weight, float energy, float fat, float saturates, float carbohydrates, float sugars, float fibre, float protein, double salt) {
        ContentValues contentValues = new ContentValues();

        // TODO get date
        /*
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = simpleDateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(date);

        contentValues.put("date",);*/
        contentValues.put("barcode", barcode);
        contentValues.put("name", name);
        contentValues.put("weight", weight);
        contentValues.put("energy", energy);
        contentValues.put("fat", fat);
        contentValues.put("saturates", saturates);
        contentValues.put("carbohydrates", carbohydrates);
        contentValues.put("sugars", sugars);
        contentValues.put("fibre", fibre);
        contentValues.put("protein", protein);
        contentValues.put("salt", salt);
        String url = "http://foltys.net/food-check/img/" + barcode + ".jpg";
        contentValues.put("imURL", url);
        contentValues.put("fav", false);

        db.insert("past_scans", null, contentValues);
    }

    public void insertFavorite(SQLiteDatabase db, int barcode, String name, float weight, float energy, float fat, float saturates, float carbohydrates, float sugars, float fibre, float protein, double salt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("barcode", barcode);
        contentValues.put("name", name);
        contentValues.put("weight", weight);
        contentValues.put("energy", energy);
        contentValues.put("fat", fat);
        contentValues.put("saturates", saturates);
        contentValues.put("carbohydrates", carbohydrates);
        contentValues.put("sugars", sugars);
        contentValues.put("fibre", fibre);
        contentValues.put("protein", protein);
        contentValues.put("salt", salt);
        String url = "http://foltys.net/food-check/img/" + barcode + ".jpg";
        contentValues.put("imURL", url);
        db.insert("favorite", null, contentValues);
    }

    public Cursor read(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        return cursor;
    }

    public void delete(SQLiteDatabase db, String tableName, int id) {
        // Both should work
        //db.delete(tableName, "_ID=?", new String[] {Integer.valueOf(id)});
        String sqlStatement = "DELETE FROM " + tableName + " WHERE `_ID`='" + id + "';";
        db.execSQL(sqlStatement);
    }


}
