package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

public class AfterScanActivity extends AppCompatActivity {

    private static final String TAG = "AfterScanActivity";

    private ImageView productImg;
    private TextView barcodeResultTextView;
    private TextView nameTextView;
    private TextView weightResultTextView;
    private TextView energyResultTextView;
    private TextView fatResultTextView;
    private TextView saturatesResultTextView;
    private TextView carbohydratesResultTextView;
    private TextView sugarsResultTextView;
    private TextView fibreResultTextView;
    private TextView proteinResultTextView;
    private TextView saltResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);

        Button favoriteButton = findViewById(R.id.favoriteButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);
        activityInit();


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO zmiana ikony i dodanie do bazy danych fav
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO inserting into database after scan
                ContentValues contentValues = new ContentValues();
                contentValues.put("date", "");
                contentValues.put("barcode", 00000);
                contentValues.put("name", "");
                contentValues.put("weight", "");
                contentValues.put("energy", "");
                contentValues.put("carbohydrates", "");


                contentValues.put("protein", 1.1);
                //db.insert("past_scans", null, contentValues);

            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AfterScanActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });



        // TODO uzyskanie barcode
        long barcode = 5904730161183L; // dev only0
        barcodeResultTextView.setText(Double.toString(barcode));

        // TODO polaczenie z baza w necie
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://murmuring-coast-47385.herokuapp.com/api/products/" + barcode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //resultTextView.setText(response);
                Log.d(TAG, response);
                Gson gson = new Gson();
                // we get array with one element as a result
                FoodProduct[] product = gson.fromJson(response, FoodProduct[].class);
                Log.d(TAG, "Product barcode: " + product[0].getBarcode());
                nameTextView.setText(product[0].getName());
                weightResultTextView.setText(Double.toString(product[0].getWeight()));
                energyResultTextView.setText(Double.toString(product[0].getEnergy()));
                fatResultTextView.setText(Double.toString((product[0].getFat())));
                saturatesResultTextView.setText(Double.toString(product[0].getSaturates()));
                carbohydratesResultTextView.setText(Double.toString(product[0].getCarbohydrates()));
                sugarsResultTextView.setText(Double.toString(product[0].getSugar()));
                fibreResultTextView.setText(Double.toString(product[0].getFibre()));
                proteinResultTextView.setText(Double.toString(product[0].getProtein()));
                saltResultTextView.setText(Double.toString(product[0].getSalt()));
                // TODO Glide.with(AfterScanActivity.class).asBitmap().load(product[0].getUrl()).into(productImg);



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nameTextView.setText(R.string.error_occured);
            }
        });
        queue.add(stringRequest);


    }

    private void activityInit() {
        Log.d(TAG, "TextView init started");
        barcodeResultTextView = findViewById(R.id.barcodeResultTextView);
        nameTextView = findViewById(R.id.productNameTextView);
        weightResultTextView = findViewById(R.id.weightTextView);
        energyResultTextView = findViewById(R.id.energyResultTextView);
        fatResultTextView = findViewById(R.id.fatResultTextView);
        saturatesResultTextView = findViewById(R.id.saturatesResultTextView);
        carbohydratesResultTextView = findViewById(R.id.carbohydratesResultTextView);
        sugarsResultTextView = findViewById(R.id.sugarsResultTextView);
        fibreResultTextView = findViewById(R.id.fibreResultTextView);
        proteinResultTextView = findViewById(R.id.proteinResultTextView);
        saltResultTextView = findViewById(R.id.saltResultTextView);
        productImg = findViewById(R.id.productImage);
    }


}
