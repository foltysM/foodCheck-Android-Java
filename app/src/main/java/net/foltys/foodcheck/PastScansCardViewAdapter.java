package net.foltys.foodcheck;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class PastScansCardViewAdapter extends RecyclerView.Adapter<PastScansCardViewAdapter.ViewHolder> {
    private static final String TAG = "PastScansCardViewAdapter";

    private ArrayList<FoodProduct> products = new ArrayList<>();
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
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called");
        holder.productName.setText(products.get(position).getName());
        holder.productDate.setText(products.get(position).getDate());
        holder.productEnergy.setText(context.getResources().getString(R.string.energy) + " - " + products.get(position).getEnergy() + context.getResources().getString(R.string.g));
        holder.productFat.setText(context.getResources().getString(R.string.fat) + " - " + products.get(position).getFat() + context.getResources().getString(R.string.g));
        holder.productSaturates.setText(context.getResources().getString(R.string.saturates) + " - " + products.get(position).getSaturates() + context.getResources().getString(R.string.g));
        holder.productCarbohydrates.setText(context.getResources().getString(R.string.carbohydrates) + " - " + products.get(position).getCarbohydrates() + context.getResources().getString(R.string.g));
        holder.productSugars.setText(context.getResources().getString(R.string.sugars) + " - " + products.get(position).getSugar() + context.getResources().getString(R.string.g));
        holder.productProtein.setText(context.getResources().getString(R.string.protein) + " - " + products.get(position).getProtein() + context.getResources().getString(R.string.g));
        holder.productSalt.setText(context.getResources().getString(R.string.salt) + " - " + products.get(position).getSalt() + context.getResources().getString(R.string.g));

        //TODO wyswietlanie zdjecia
        Glide.with(context)
                .asBitmap()
                .load(products.get(position).getUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(ArrayList<FoodProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private ImageView productImage;
        private TextView productDate;
        private TextView productEnergy;
        private TextView productFat;
        private TextView productSaturates;
        private TextView productCarbohydrates;
        private TextView productSugars;
        private TextView productProtein;
        private TextView productSalt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImagePast);
            productName = itemView.findViewById(R.id.productNamePast);
            productDate = itemView.findViewById(R.id.datePastScans);
            productEnergy = itemView.findViewById(R.id.energyTextView);
            productFat = itemView.findViewById(R.id.fatTextView);
            productSaturates = itemView.findViewById(R.id.saturatesTextView);
            productCarbohydrates = itemView.findViewById(R.id.carbohydratesTextView);
            productSugars = itemView.findViewById(R.id.sugarsPastTextView);
            productProtein = itemView.findViewById(R.id.proteinPastTextView);
            productSalt = itemView.findViewById(R.id.saltPastTextView);
        }
    }
}
