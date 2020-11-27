package net.foltys.foodcheck.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import net.foltys.foodcheck.FoodProduct;
import net.foltys.foodcheck.MainActivity;
import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.text.DecimalFormat;
import java.util.Calendar;

public class AfterScanActivity extends AppCompatActivity {

    private static final String TAG = "AfterScanActivity";

    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
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
    private Boolean isFav = false;
    private final double[] percent = {100};
    private ImageView productImg, favoriteButton;
    private FavProdViewModel mFavProdViewModel;

    private ProgressBar imgProgressBar;

    private PastScanViewModel mPastScanViewModel;
    private double energy, carbohydrates, fat, fibre, protein, salt, saturates, sugar, weight;
    private String name, url, scannedBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);
        scannedBarcode = getIntent().getStringExtra("barcode");
        favoriteButton = findViewById(R.id.favoriteButton);
        imgProgressBar = findViewById(R.id.imageProgressBar);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);

        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);
        mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        activityInit();


        favoriteButton.setOnClickListener(v -> {
            if (isFav) {
                isFav = false;
                favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                mFavProdViewModel.deleteFav(mFavProdViewModel.getOneFav(scannedBarcode));
            } else {
                isFav = true;
                favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                insertToFav();
            }
        });

        saveButton.setOnClickListener(v -> insertDataToDatabase());

        discardButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(AfterScanActivity.this, MainActivity.class);
            startActivity(myIntent);
        });


        barcodeResultTextView.setText(scannedBarcode);

        RequestQueue queue = Volley.newRequestQueue(this);

        url = "https://murmuring-coast-47385.herokuapp.com/api/products/" + scannedBarcode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {

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
            url = product[0].getUrl();
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


        }, error -> {
            nameTextView.setText(R.string.error_occured);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AfterScanActivity.this);
            builder.setTitle(getResources().getString(R.string.no_product_found))
                    .setMessage(getResources().getString(R.string.add_new_product_manually_question))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        Intent addNew = new Intent(AfterScanActivity.this, NewProductActivity.class);
                        addNew.putExtra("barcode", scannedBarcode);
                        startActivity(addNew);
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                        Intent goToMain = new Intent(AfterScanActivity.this, MainActivity.class);
                        startActivity(goToMain);
                    })
                    .show();
        });
        queue.add(stringRequest);

        //Checks if the product is in the favorite database
        FavProd fav = mFavProdViewModel.getOneFav(scannedBarcode);

        if (fav == null) {
            //no such record in favorite database
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            isFav = false;
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_full);
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
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        //Custom dialog to set eaten amount
        final Dialog customDialog = new Dialog(AfterScanActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setTitle(R.string.adjust_eaten_product_amount);

        Slider slider = customDialog.findViewById(R.id.sliderCustomDialog);
        Button button = customDialog.findViewById(R.id.buttonCustomDialog);
        Button okWeightButton = customDialog.findViewById(R.id.okWeightButton);
        Button okPercentageButton = customDialog.findViewById(R.id.okPercentageButton);
        EditText weightEditText = customDialog.findViewById(R.id.weightEditTextCustomDialog);
        EditText percentageEditText = customDialog.findViewById(R.id.percentageEdiTextCustomDialog);
        percentageEditText.setText(R.string._100percent);
        weightEditText.setText(String.format("%s", weight));

        slider.addOnChangeListener((slider1, value, fromUser) -> {

            percentageEditText.setText(String.format("%s", shortenDecimal(value) + "%"));

            double val = (value * weight) / 100;

            weightEditText.setText(String.format("%s", shortenDecimal(val) + getString(R.string.g)));
            double valueDouble = value;
            valueDouble = shortenDecimal((valueDouble));
            percent[0] = valueDouble;
        });

        okPercentageButton.setOnClickListener(v -> {
            String a = percentageEditText.getText().toString();
            if ((!a.equals("")) && (!a.equals("%"))) {
                String b = a.substring(0, a.length() - 1);
                if ((!b.equals("")) && (!b.equals("%"))) {
                    double doubleValue = 0;
                    try {
                        doubleValue = Double.parseDouble(b.replace(',', '.'));
                    } catch (NumberFormatException e) {
                        //Error
                        Log.e(TAG, e.getMessage());
                    }

                    if (doubleValue <= 0)
                        doubleValue = 0;

                    if (doubleValue >= 100)
                        doubleValue = 100;

                    slider.setValue((float) doubleValue);
                    percent[0] = doubleValue;
                }
            }

        });


        okWeightButton.setOnClickListener(v -> {
            String a = weightEditText.getText().toString();
            if ((!a.equals("")) && (!a.equals(getString(R.string.g)))) {
                String b = a.substring(0, a.length() - 1);
                Log.d("value b", b);
                if ((!b.equals("")) && (!b.equals(getString(R.string.g)))) {
                    double doubleValue = 0;
                    try {
                        doubleValue = Double.parseDouble(b.replace(',', '.'));
                    } catch (NumberFormatException e) {
                        //Error
                        Log.e(TAG, e.getMessage());
                    }

                    if (doubleValue <= 0)
                        doubleValue = 0;
                    if (doubleValue > weight)
                        doubleValue = weight;
                    double d = (100 * doubleValue) / weight;
                    slider.setValue((float) d);

                    percent[0] = d;
                }
            }
        });

        button.setOnClickListener(v ->
        {
            customDialog.cancel();
            percent[0] = percent[0] / 100;

            PastScan scan = new PastScan(scannedBarcode,
                    name,
                    shortenDecimal(weight),
                    day,
                    month,
                    year,
                    hour,
                    minutes,
                    shortenDecimal(energy),
                    shortenDecimal(carbohydrates),
                    shortenDecimal(protein),
                    shortenDecimal(fat),
                    shortenDecimal(saturates),
                    shortenDecimal(sugar),
                    shortenDecimal(fibre),
                    shortenDecimal(salt),
                    url,
                    percent[0]);
            mPastScanViewModel.insertPast(scan);
            Intent toHistoryIntent = new Intent(AfterScanActivity.this, MainActivity.class);
            startActivity(toHistoryIntent);
        });
        customDialog.show();
    }

    private double shortenDecimal(double input) {
        String a = decimalFormat.format(input);
        double newDouble = 0;
        try {
            newDouble = Double.parseDouble(a.replace(',', '.'));
        } catch (NumberFormatException e) {
            //Error
            Log.e(TAG, e.getMessage());
        }
        return newDouble;
    }

    private void insertToFav() {
        FavProd fav = new FavProd(scannedBarcode, name, weight, energy, carbohydrates, protein, fat, saturates, sugar, fibre, salt, url);
        mFavProdViewModel.insertFav(fav);
    }
}


