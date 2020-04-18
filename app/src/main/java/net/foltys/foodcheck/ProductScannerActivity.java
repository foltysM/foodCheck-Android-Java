package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Button;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ProductScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private Button scanButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_scanner);
        //TODO skanowanie kodów
        scannerView = new ZXingScannerView(this);
        scanButton2 = findViewById(R.id.scanBtn);

    }

    @Override
    public void handleResult(Result result) {

    }

    //scanButton2.setOnClickLis

}
