package net.foltys.foodcheck;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.PastScan;

import java.util.ArrayList;
import java.util.List;

public class PastScansCardViewAdapter extends RecyclerView.Adapter<PastScansCardViewAdapter.ViewHolder> {
    private static final String TAG = "PastScansCardViewAdapter";
    private final Context context;
    private List<PastScan> pastScanProducts = new ArrayList<>();
    private List<FavProd> favProducts = new ArrayList<>();

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
        holder.productDate.setText(String.format("%s", pastScanProducts.get(position).getDay() + "/" + pastScanProducts.get(position).getMonth() + "/" + pastScanProducts.get(position).getYear()));
        int min = pastScanProducts.get(position).getMinutes();
        if (min < 10) {
            holder.productHour.setText(String.format("%s", pastScanProducts.get(position).getHour() + ":0" + min));
        } else {
            holder.productHour.setText(String.format("%s", pastScanProducts.get(position).getHour() + ":" + min));
        }

        holder.productEnergy.setText(String.format("%s", context.getResources().getString(R.string.energy) + " - " + pastScanProducts.get(position).getEnergy() + context.getResources().getString(R.string.kcal)));
        holder.productFat.setText(String.format("%s", context.getResources().getString(R.string.fat) + " - " + pastScanProducts.get(position).getFat() + context.getResources().getString(R.string.g)));
        holder.productSaturates.setText(String.format("%s", context.getResources().getString(R.string.saturates) + " - " + pastScanProducts.get(position).getSaturates() + context.getResources().getString(R.string.g)));
        holder.productCarbohydrates.setText(String.format("%s", context.getResources().getString(R.string.carbohydrates) + " - " + pastScanProducts.get(position).getCarbohydrates() + context.getResources().getString(R.string.g)));
        holder.productSugars.setText(String.format("%s", context.getResources().getString(R.string.sugars) + " - " + pastScanProducts.get(position).getSugars() + context.getResources().getString(R.string.g)));
        holder.productProtein.setText(String.format("%s", context.getResources().getString(R.string.protein) + " - " + pastScanProducts.get(position).getProtein() + context.getResources().getString(R.string.g)));
        holder.productSalt.setText(String.format("%s", context.getResources().getString(R.string.salt) + " - " + pastScanProducts.get(position).getSalt() + context.getResources().getString(R.string.g)));
        Log.d(TAG, "ID in database" + pastScanProducts.get(position).getId());

        String url = pastScanProducts.get(position).getUrl();

        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.productImage);
        // check if it is fav
        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        for (int i = 0; i < favProducts.size(); i++) {
            Log.d(TAG, "Loop");
            if (favProducts.get(i).getBarcode().equals(pastScanProducts.get(position).getBarcode())) {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                holder.fav = true;
            }

        }

    }

    @Override
    public int getItemCount() {
        return pastScanProducts.size();
    }

    public void setProducts(List<PastScan> products, List<FavProd> favorites) {
        this.pastScanProducts = products;
        this.favProducts = favorites;
        notifyDataSetChanged();
    }

    public PastScan getPastAt(int pos) {
        return pastScanProducts.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName;
        private final ImageView productImage;
        private final TextView productDate;
        private final TextView productHour;
        private final TextView productEnergy;
        private final TextView productFat;
        private final TextView productSaturates;
        private final TextView productCarbohydrates;
        private final TextView productSugars;
        private final TextView productProtein;
        private final TextView productSalt;
        private final ImageView favoriteButton;
        private Boolean fav = false;

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
            favoriteButton = itemView.findViewById(R.id.favButton);

            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Log.d(TAG, position + " position");

                if (fav) {
                    //TODO delete z fav
                    fav = false;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                } else {
                    // TODO add to fav database
                    fav = true;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                }
            });
        }
    }
}
