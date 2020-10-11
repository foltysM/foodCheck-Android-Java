package net.foltys.foodcheck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.util.Calendar;

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
    private String scannedBarcode;
    private Boolean isFav = false;
    private double carbohydrates;
    private double energy;
    private double fat;
    private double fibre;
    private String name;
    private double protein;
    private double salt;
    private double saturates;
    private double sugar;
    private double weight;
    private FavProdViewModel mFavProdViewModel;

    private ProgressBar imgProgressBar;

    private PastScanViewModel mPastScanViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);
        scannedBarcode = getIntent().getStringExtra("barcode");
        final Button favoriteButton = findViewById(R.id.favoriteButton);
        imgProgressBar = findViewById(R.id.imageProgressBar);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);

        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class); // error here
        mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        activityInit();


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    isFav = false;
                    favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border, 0, 0, 0);
                    // TODO usuniecie z bazy fav
                } else {
                    isFav = true;
                    favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_full, 0, 0, 0);
                    insertToFav();

                }
                favoriteButton.setTextColor(Color.BLACK);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataToDatabase();
                Intent toHistoryIntent = new Intent(AfterScanActivity.this, PastScansActivity.class);
                startActivity(toHistoryIntent);
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AfterScanActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        barcodeResultTextView.setText(scannedBarcode);

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://murmuring-coast-47385.herokuapp.com/api/products/" + scannedBarcode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, response);
                Gson gson = new Gson();
                // we get array with one element as a result
                FoodProduct[] product = gson.fromJson(response, FoodProduct[].class);
                Log.d(TAG, "Product barcode: " + product[0].getBarcode());
                name = product[0].getName();
                nameTextView.setText(name);
                weight = product[0].getWeight();
                weightResultTextView.setText(String.format(Double.toString(weight), getResources().getString(R.string.g)));
                energy = product[0].getEnergy();
                energyResultTextView.setText(String.format(Double.toString(energy), getResources().getString(R.string.kcal)));
                fat = product[0].getFat();
                fatResultTextView.setText(String.format(Double.toString(fat), getResources().getString(R.string.g)));
                saturates = product[0].getSaturates();
                saturatesResultTextView.setText(String.format(Double.toString(saturates), getResources().getString(R.string.g)));
                carbohydrates = product[0].getCarbohydrates();
                carbohydratesResultTextView.setText(String.format(Double.toString(carbohydrates), getResources().getString(R.string.g)));
                sugar = product[0].getSugar();
                sugarsResultTextView.setText(String.format(Double.toString(sugar), getResources().getString(R.string.g)));
                fibre = product[0].getFibre();
                fibreResultTextView.setText(String.format(Double.toString(fibre), getResources().getString(R.string.g)));
                protein = product[0].getProtein();
                proteinResultTextView.setText(String.format(Double.toString(protein), getResources().getString(R.string.g)));
                salt = product[0].getSalt();
                saltResultTextView.setText(String.format(Double.toString(salt), getResources().getString(R.string.g)));
                product[0].setUrl("http://foltys.net/food-check/img/" + scannedBarcode + ".jpg");
                Glide.with(AfterScanActivity.this)
                        .asBitmap()
                        .load(product[0].getUrl())
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                imgProgressBar.setVisibility(View.GONE);
                                Toast.makeText(AfterScanActivity.this, R.string.imgLoadFailed, Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                imgProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .override(200, 200)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .transform(new RoundedCorners(10))
                        .into(productImg);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nameTextView.setText(R.string.error_occured);
            }
        });
        queue.add(stringRequest);

        //Checks if the product is in the favorite database
        FavProd fav = mFavProdViewModel.getOneFav(scannedBarcode);

        if (fav == null) {
            //no such record in favorite database
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border, 0, 0, 0);
            isFav = false;
        } else {
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_full, 0, 0, 0);
            isFav = true;
        }
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

    private void insertDataToDatabase() {
        final Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String date = year + "/" + month + "/" + day;
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + calendar.get(Calendar.MINUTE);
        PastScan scan = new PastScan(0, scannedBarcode, name, weight, date, hour, energy, carbohydrates, protein, fat, saturates, sugar, fibre, salt);  //TODO id
        mPastScanViewModel.insertPast(scan);
    }

    private void insertToFav() {
        FavProd fav = new FavProd(scannedBarcode, name, weight, energy, carbohydrates, protein, fat, saturates, sugar, fibre, salt);
        mFavProdViewModel.insertFav(fav);
    }
}


