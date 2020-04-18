package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;

public class afterScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);

        // TODO uzyskanie barcode

        // TODO polaczenie z baza w necie

        // TODO inserting after scan
        /*ContentValues contentValues = new ContentValues();
        contentValues.put("barcode", 00000);
        contentValues.put("protein", 1.1);
        db.insert("past_scans", null, contentValues);*/


    }


}
