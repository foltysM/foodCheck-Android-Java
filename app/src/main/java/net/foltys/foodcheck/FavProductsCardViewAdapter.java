package net.foltys.foodcheck;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.PastScan;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class FavProductsCardViewAdapter extends RecyclerView.Adapter<FavProductsCardViewAdapter.ViewHolder> {
    private static final String TAG = "FavProductsCardViewAdapter";

    private List<FavProd> favProds = new ArrayList<>();
    private List<PastScan> pastScans = new ArrayList<>();
    private final Context context;

    private final Observer<Integer> favClickedObserver;

    /**
     * Constructor of FavProductsCardView Adapter class
     *
     * @param context            Context
     * @param favClickedObserver On favorite click observer
     */
    public FavProductsCardViewAdapter(Context context, Observer<Integer> favClickedObserver) {
        this.context = context;
        this.favClickedObserver = favClickedObserver;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favitems_foodproduct_rec_view, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d(TAG, "Fav onBind called");
        holder.productNameFav.setText(favProds.get(position).getName());
        //Searching for last ate date
        String last = "0000-00-00";
        for (int i = 0; i < pastScans.size(); i++) {
            if (favProds.get(position).getBarcode().equals(pastScans.get(i).getBarcode())) {
                String concatenated = pastScans.get(i).getYear() + "-" + pastScans.get(i).getMonth() + "-" + pastScans.get(i).getDay();
                if (concatenated.compareTo(last) > 0) {
                    last = concatenated;
                }
            }
        }
        if (last.equals("0000-00-00"))
            holder.lastAteFav.setText(R.string.never);
        else
            holder.lastAteFav.setText(last);
        holder.energyFav.setText(String.format("%s", context.getResources().getString(R.string.energy) + " - " + favProds.get(position).getEnergy() + context.getResources().getString(R.string.g)));
        holder.fatFav.setText(String.format("%s", context.getResources().getString(R.string.fat) + " - " + favProds.get(position).getFat() + context.getResources().getString(R.string.g)));
        holder.saturatesFav.setText(String.format("%s", context.getResources().getString(R.string.saturates) + " - " + favProds.get(position).getSaturates() + context.getResources().getString(R.string.g)));
        holder.sugarsFav.setText(String.format("%s", context.getResources().getString(R.string.sugars) + " - " + favProds.get(position).getSugars() + context.getResources().getString(R.string.g)));
        holder.carbohydratesFav.setText(String.format("%s", context.getResources().getString(R.string.carbohydrates) + " - " + favProds.get(position).getCarbohydrates() + context.getResources().getString(R.string.g)));
        holder.saltFav.setText(String.format("%s", context.getResources().getString(R.string.salt) + " - " + favProds.get(position).getSalt() + context.getResources().getString(R.string.g)));

        String url = favProds.get(position).getUrl();

        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageFav);

        holder.itemView.setOnClickListener(view -> {
            Observable<Integer> favClickEmitter = Observable.just(position);
            favClickEmitter.subscribe(favClickedObserver);
        });

    }


    /**
     * Method returns size of favorite products list
     *
     * @return Size of favorite products list
     */
    @Override
    public int getItemCount() {
        return favProds.size();
    }

    /**
     * Methods sets favorite list and past scans list and notify that it was changed
     *
     * @param favProducts List of favorite products
     * @param pastScans   List of past scans
     */
    public void setFavProducts(List<FavProd> favProducts, List<PastScan> pastScans) {
        this.favProds = favProducts;
        this.pastScans = pastScans;
        notifyDataSetChanged();
    }

    /**
     * Method returns favorite product object by its position
     *
     * @param pos Position on the list
     * @return Favorite product with specified position
     */
    public FavProd getFavAt(int pos) {
        return favProds.get(pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageFav;
        private final TextView productNameFav;
        private final TextView lastAteFav;
        private final TextView energyFav;
        private final TextView fatFav;
        private final TextView saturatesFav;
        private final TextView carbohydratesFav;
        private final TextView sugarsFav;
        private final TextView saltFav;

        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageFav = itemView.findViewById(R.id.productImageFav);
            productNameFav = itemView.findViewById(R.id.titleTextView);
            lastAteFav = itemView.findViewById(R.id.lastAteDate);
            energyFav = itemView.findViewById(R.id.energyTextView);
            fatFav = itemView.findViewById(R.id.fatTextView);
            saturatesFav = itemView.findViewById(R.id.saturatesTextView);
            carbohydratesFav = itemView.findViewById(R.id.carbohydratesTextView);
            sugarsFav = itemView.findViewById(R.id.sugarsTextView);
            saltFav = itemView.findViewById(R.id.saltTextView);

            viewBackground = itemView.findViewById(R.id.fav_background);
            viewForeground = itemView.findViewById(R.id.fav_foreground);
        }
    }
}
