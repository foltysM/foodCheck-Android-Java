package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PastScansActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scans);

        FloatingActionButton newScanFloatingActionBtn = findViewById(R.id.newScanFloatingActionButton);
        // inaczej
        RecyclerView pastScansCardView = findViewById(R.id.cardViewPastScans);

        newScanFloatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO nowy skan
            }
        });

        PastScansCardViewAdapter adapter = new PastScansCardViewAdapter(this);
        pastScansCardView.setAdapter(adapter);
        pastScansCardView.setLayoutManager(new LinearLayoutManager(this));

        // for debug purposes only // TODO delete
        ArrayList<FoodProduct> products = new ArrayList<>();
        products.add(new FoodProduct(1, "2020-01-01", "Parowka", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        products.add(new FoodProduct(2, "2020-01-01", "Ser", "123475", 299, 12, 12, 12, 1212, 12, 12, 21, 12, "http://foltys.net/food-check/img/5904730161183.jpg", false));
        adapter.setProducts(products);
    }
}
