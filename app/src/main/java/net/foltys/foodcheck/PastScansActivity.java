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

    // inaczej
    private RecyclerView pastScansCardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scans);

        // FloatingActionButton backButton = findViewById(R.id.backFloatingButton);
        pastScansCardView = findViewById(R.id.cardViewPastScans);
        //pastScansCardView = findViewById(R.layout.pastscans_foodproduct_card_view);

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO faktycznie back, a nie do main
//                // TODO zamienić na taki ładny, nowiutki wygląd
//                Intent intent = new Intent(PastScansActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

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
