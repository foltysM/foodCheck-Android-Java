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

import java.util.ArrayList;

public class FavProductsCardViewAdapter extends RecyclerView.Adapter<FavProductsCardViewAdapter.ViewHolder> {
    private static final String TAG = "FavProductsCardViewAdapter";

    private ArrayList<FavFoodProduct> favProducts = new ArrayList<>(); // TODO inna klasa
    private Context context;

    public FavProductsCardViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favitems_foodproduct_rec_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        // TODO clickable fav button
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO uzupelnic o obrazek
        Log.d(TAG, "Fav onBind called");
        holder.productNameFav.setText(favProducts.get(position).getName());
        holder.lastAteFav.setText(favProducts.get(position).getLastAte());
        holder.energyFav.setText(context.getResources().getString(R.string.energy) + " - " + favProducts.get(position).getEnergy() + context.getResources().getString(R.string.g));
        holder.fatFav.setText(context.getResources().getString(R.string.fat) + " - " + favProducts.get(position).getFat() + context.getResources().getString(R.string.g));
        holder.saturatesFav.setText(context.getResources().getString(R.string.saturates) + " - " + favProducts.get(position).getSaturates() + context.getResources().getString(R.string.g));
        holder.sugarsFav.setText(context.getResources().getString(R.string.sugars) + " - " + favProducts.get(position).getSugar() + context.getResources().getString(R.string.g));
        holder.carbohydratesFav.setText(context.getResources().getString(R.string.carbohydrates) + " - " + favProducts.get(position).getCarbohydrates() + context.getResources().getString(R.string.g));
        holder.saltFav.setText(context.getResources().getString(R.string.salt) + " - " + favProducts.get(position).getSalt() + context.getResources().getString(R.string.g));

    }


    @Override
    public int getItemCount() {
        return favProducts.size();
    }

    public void setFavProducts(ArrayList<FavFoodProduct> favProducts) {
        this.favProducts = favProducts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageFav;
        private TextView productNameFav;
        private TextView lastAteFav;
        private TextView energyFav;
        private TextView fatFav;
        private TextView saturatesFav;
        private TextView carbohydratesFav;
        private TextView sugarsFav;
        private TextView saltFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageFav = itemView.findViewById(R.id.productImage);
            productNameFav = itemView.findViewById(R.id.titleTextView);
            lastAteFav = itemView.findViewById(R.id.lastAteTextView);
            energyFav = itemView.findViewById(R.id.energyTextView);
            fatFav = itemView.findViewById(R.id.fatTextView);
            saturatesFav = itemView.findViewById(R.id.saturatesTextView);
            carbohydratesFav = itemView.findViewById(R.id.carbohydratesTextView);
            sugarsFav = itemView.findViewById(R.id.sugarsTextView);
            saltFav = itemView.findViewById(R.id.saltTextView);

        }
    }
}
