package net.foltys.foodcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    private Button scanButton;
    private Button debugButton, debug2Button;
    private static final String TAG = "MainActivity";
    private SQLiteDatabase database;
    private Cursor cursor;
    private RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO wlaczony internet check

        parent = findViewById(R.id.mainRelLayout);

        //Microsoft SDK
        AppCenter.start(getApplication(), "bb5ba2c0-ac45-4e09-937b-08d97e6c789c",
                Analytics.class, Crashes.class);

        // creating database
        DatabaseAsyncTask databaseAsyncTask = new DatabaseAsyncTask();
        databaseAsyncTask.execute();

        scanButton = findViewById(R.id.scanButton);
        debugButton = findViewById(R.id.debugButton);
        debug2Button = findViewById(R.id.debugFav);

        debug2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavItemsActivity.class);
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO checking permission
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // checks if able to show permission request
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        // shows snackbar
                        Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.grant_permission, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                } else {
                    Intent myIntent = new Intent(MainActivity.this, ProductScannerActivity.class);
                    startActivity(myIntent);
                }
            }
        });
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AfterScanActivity.class);
                startActivity(myIntent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // zamykanie bazy i zamykanie cursora
        cursor.close();
        database.close();


    }


    @Override
    // what will be done after click in permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, ProductScannerActivity.class);
                    //intent.putExtra("database", database);
                    startActivity(intent);
                } else {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // checks if able to show permission request
                        // TODO wykrzyknik ponizej czy nie?
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                            // shows snackbar
                            Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.grant_permission, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    }).show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }
                    } else {
                        Intent myIntent = new Intent(MainActivity.this, ProductScannerActivity.class);
                        startActivity(myIntent);
                    }
                }
                break;
            default:
                break;
        }
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
                database = databaseHelper.getWritableDatabase();
                //databaseHelper.insertPastScans(database, 556, "Test product", 1, 1, 2, 2, 2, 2, 2, 2, 2);
                //database.delete("past_scans", )
                cursor = database.query(true, "past_scans", null, null, null, null, null, null, null, null);

                String returnString = "";
                if (cursor.moveToFirst()) {
//                    do{
//                        for(int i = 0; i<cursor.getColumnCount();i++)
//                        {
//                            returnString = returnString + cursor.getColumnName(i)+": " + cursor.getString(i);
//
//                        }
//                        returnString += "**************";
//                        cursor.moveToNext();
//
//                    }while(!cursor.isLast());
                    for (int i = 0; i < cursor.getCount(); i++) {
                        // po każdej kolumnie
                        for (int j = 0; j < cursor.getColumnCount(); j++) {
                            cursor.getColumnName(j);
                            cursor.getDouble(j);
                            cursor.getString(j);

                        }
                        cursor.moveToNext();

                    }


                    //String name = cursor.getString(3);
                    Log.d(TAG, returnString);
                    System.out.println(returnString);
                    return returnString;


                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }
}
