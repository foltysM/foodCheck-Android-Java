package net.foltys.foodcheck;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.util.ArrayList;
import java.util.List;

public class PastScansCardViewAdapter extends RecyclerView.Adapter<PastScansCardViewAdapter.ViewHolder> {
    private static final String TAG = "PastScansCardViewAdapter";

    private List<PastScan> pastScanProducts = new ArrayList<>();
    private Context context;

    public PastScansCardViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pastscans_foodproduct_card_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        // TODO clickable fav button
        return holder;
        //FavProdViewModel mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class); // todo check czy jest fav czy nie
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called");
        holder.productName.setText(pastScanProducts.get(position).getName());
        holder.productDate.setText(pastScanProducts.get(position).getDate());
        holder.productHour.setText(pastScanProducts.get(position).getHour());
        holder.productEnergy.setText(context.getResources().getString(R.string.energy) + " - " + pastScanProducts.get(position).getEnergy() + context.getResources().getString(R.string.g));
        holder.productFat.setText(context.getResources().getString(R.string.fat) + " - " + pastScanProducts.get(position).getFat() + context.getResources().getString(R.string.g));
        holder.productSaturates.setText(context.getResources().getString(R.string.saturates) + " - " + pastScanProducts.get(position).getSaturates() + context.getResources().getString(R.string.g));
        holder.productCarbohydrates.setText(context.getResources().getString(R.string.carbohydrates) + " - " + pastScanProducts.get(position).getCarbohydrates() + context.getResources().getString(R.string.g));
        holder.productSugars.setText(context.getResources().getString(R.string.sugars) + " - " + pastScanProducts.get(position).getSugars() + context.getResources().getString(R.string.g));
        holder.productProtein.setText(context.getResources().getString(R.string.protein) + " - " + pastScanProducts.get(position).getProtein() + context.getResources().getString(R.string.g));
        holder.productSalt.setText(context.getResources().getString(R.string.salt) + " - " + pastScanProducts.get(position).getSalt() + context.getResources().getString(R.string.g));

        String url = "http://foltys.net/food-check/img/" + pastScanProducts.get(position).getBarcode() + ".jpg";

        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return pastScanProducts.size();
    }

    public void setProducts(List<PastScan> products) {
        this.pastScanProducts = products;
        notifyDataSetChanged();
    }

    public PastScan getPastAt(int pos) {
        return pastScanProducts.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private ImageView productImage;
        private TextView productDate;
        private TextView productHour;
        private TextView productEnergy;
        private TextView productFat;
        private TextView productSaturates;
        private TextView productCarbohydrates;
        private TextView productSugars;
        private TextView productProtein;
        private TextView productSalt;
        private Button favButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImagePast);
            productName = itemView.findViewById(R.id.productNamePast);
            productDate = itemView.findViewById(R.id.datePastScans);
            productHour = itemView.findViewById(R.id.hourTextViewPast);
            productEnergy = itemView.findViewById(R.id.energyTextView);
            productFat = itemView.findViewById(R.id.fatTextView);
            productSaturates = itemView.findViewById(R.id.saturatesTextView);
            productCarbohydrates = itemView.findViewById(R.id.carbohydratesTextView);
            productSugars = itemView.findViewById(R.id.sugarsPastTextView);
            productProtein = itemView.findViewById(R.id.proteinPastTextView);
            productSalt = itemView.findViewById(R.id.saltPastTextView);
            favButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}
