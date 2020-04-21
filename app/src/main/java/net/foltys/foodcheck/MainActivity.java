package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private Button debugButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = findViewById(R.id.scanButton);
        debugButton = findViewById(R.id.debugButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ProductScannerActivity.class);
                startActivity(myIntent);
            }
        });
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AfterScanActivity.class);
                startActivity(myIntent);
            }
        });

        DatabaseAsyncTask databaseAsyncTask = new DatabaseAsyncTask();
        databaseAsyncTask.execute();
    }


    // Creating database as a background task
    private class DatabaseAsyncTask extends AsyncTask<Void, Void, String> {
        private SQliteDatabaseHelper databaseHelper;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHelper = new SQliteDatabaseHelper(MainActivity.this);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                SQLiteDatabase database = databaseHelper.getReadableDatabase();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
