package net.foltys.foodcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;

import java.util.List;

public class FavItemsActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    public static final String TAG = "FavItemsActivity";
    FavProductsCardViewAdapter adapter;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_items);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // initialization of variables connectd with database display
        adapter = new FavProductsCardViewAdapter(this);
        RecyclerView favProdCardView = findViewById(R.id.cardViewFavProd);
        FavProdViewModel mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        favProdCardView.setAdapter(adapter);
        favProdCardView.setLayoutManager(new LinearLayoutManager(this));

        mFavProdViewModel.getAllFav().observe(this, new Observer<List<FavProd>>() {
            @Override
            public void onChanged(List<FavProd> favProds) {
                adapter.setFavProducts(favProds);
                Log.d(TAG, "Changed");
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "Deleted:" + viewHolder.getAdapterPosition());
                mFavProdViewModel.deleteFav(adapter.getFavAt(viewHolder.getAdapterPosition()));
                Toast.makeText(FavItemsActivity.this, R.string.scan_deleted, Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(favProdCardView);

    }


}
