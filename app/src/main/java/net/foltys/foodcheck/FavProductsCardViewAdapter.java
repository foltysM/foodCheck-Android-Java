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

import java.util.ArrayList;
import java.util.List;

public class FavProductsCardViewAdapter extends RecyclerView.Adapter<FavProductsCardViewAdapter.ViewHolder> {
    private static final String TAG = "FavProductsCardViewAdapter";

    private List<FavProd> favProds = new ArrayList<>();
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

        Log.d(TAG, "Fav onBind called");
        holder.productNameFav.setText(favProds.get(position).getName());
        holder.lastAteFav.setText("PLACEHOLDER"); //TODO Last ate
        holder.energyFav.setText(context.getResources().getString(R.string.energy) + " - " + favProds.get(position).getEnergy() + context.getResources().getString(R.string.g));
        holder.fatFav.setText(context.getResources().getString(R.string.fat) + " - " + favProds.get(position).getFat() + context.getResources().getString(R.string.g));
        holder.saturatesFav.setText(context.getResources().getString(R.string.saturates) + " - " + favProds.get(position).getSaturates() + context.getResources().getString(R.string.g));
        holder.sugarsFav.setText(context.getResources().getString(R.string.sugars) + " - " + favProds.get(position).getSugars() + context.getResources().getString(R.string.g));
        holder.carbohydratesFav.setText(context.getResources().getString(R.string.carbohydrates) + " - " + favProds.get(position).getCarbohydrates() + context.getResources().getString(R.string.g));
        holder.saltFav.setText(context.getResources().getString(R.string.salt) + " - " + favProds.get(position).getSalt() + context.getResources().getString(R.string.g));

        String url = "http://foltys.net/food-check/img/" + favProds.get(position).getBarcode() + ".jpg";

        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageFav);

    }


    @Override
    public int getItemCount() {
        return favProds.size();
    }

    public void setFavProducts(List<FavProd> favProducts) {
        this.favProds = favProducts;
        notifyDataSetChanged();
    }

    public FavProd getFavAt(int pos) {
        return favProds.get(pos);
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

            imageFav = itemView.findViewById(R.id.productImageFav);
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
