package net.foltys.foodcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ProductScannerActivity extends AppCompatActivity {

    //private ZXingScannerView scannerView;
    private Button scanButton2;
    private EditText barcodeEditText;

    private static final int CAPTURE_IMAGE = 2;

    //private static final String TAG = "ProductScannerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_scanner);

        //scannerView = new ZXingScannerView(this);
        scanButton2 = findViewById(R.id.scanBtn);
        // for debug purposes TODO delete
        barcodeEditText = findViewById(R.id.barcodeEditText);
        barcodeEditText.setText("5904730161183");

        scanButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO odczytanie edittext
                String barcode = barcodeEditText.getText().toString();
                Intent intent = new Intent(ProductScannerActivity.this, AfterScanActivity.class);
                intent.putExtra("barcode", barcode);
                startActivity(intent);
            }
        });
        //TODO wysypuje
        //new IntentIntegrator(this).initiateScan();


        // TODO intent database
//        try {
//            Intent intent = getIntent();
//            //String name = intent.getStringExtra(getResources().getString("name"));
//            //SQLiteDatabase database = intent.getda
//
//        } catch (NullPointerException e) {
//            Log.d(TAG, "onCreate: NO INTENT VALUE");
//        }


        //TODO skanowanie kodów


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
