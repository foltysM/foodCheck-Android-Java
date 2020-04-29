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
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ProductScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private Button scanButton2;

    private static final int CAPTURE_IMAGE = 2;
    private static final String TAG = "ProductScannerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_scanner);
        // TODO intent database
        try {
            Intent intent = getIntent();
            //String name = intent.getStringExtra(getResources().getString("name"));
            //SQLiteDatabase database = intent.getda

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: NO INTENT VALUE");
        }


        //TODO skanowanie kodów
        scannerView = new ZXingScannerView(this);
        scanButton2 = findViewById(R.id.scanBtn);

        // Checking permission
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//        }else{
//            // if we already have permission, do sth
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, CAPTURE_IMAGE);
//        }


        //new IntentIntegrator(this).initiateScan();

    }

    //result of permission request
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch(requestCode){
//            case CAMERA_REQUEST_CODE:
//                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    // TODO tutaj skanowanie, jestesmy pewni uprawnien
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//
//                }else{
//                    //TODO powrót do mainActivity
//                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//
//        }
//    }

    @Override
    public void handleResult(Result result) {

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



    //scanButton2.setOnClickLis

}
