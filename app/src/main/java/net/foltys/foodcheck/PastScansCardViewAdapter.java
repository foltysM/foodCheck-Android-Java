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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class PastScansCardViewAdapter extends RecyclerView.Adapter<PastScansCardViewAdapter.ViewHolder> {
    private static final String TAG = "PastScansCardViewAdapter";
    private final Context context;
    private List<PastScan> pastScanProducts = new ArrayList<>();
    private List<FavProd> favProducts = new ArrayList<>();
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final Observer<RxHelper> observer;
    private final Observer<Integer> clickObserver;


    /**
     * Constructor of the PastScansCardViewAdapter class
     *
     * @param context       Context
     * @param observer      Favorite change observer
     * @param clickObserver On past scan click observer
     */
    public PastScansCardViewAdapter(Context context, Observer<RxHelper> observer, Observer<Integer> clickObserver) {
        this.context = context;
        this.observer = observer;
        this.clickObserver = clickObserver;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pastscans_foodproduct_card_view, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Method responsible for binding observer and emitter, and value change of specified element
     *
     * @param pos Element position on the list
     * @param val Value that the element will be changed to
     */
    public void goChangeFav(int pos, Boolean val) {
        RxHelper rxHelper = new RxHelper(pos, val);
        Observable<RxHelper> emitter = Observable.just(rxHelper);
        emitter.subscribe(observer);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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

        holder.productEnergy.setText(String.format("%s", context.getResources().getString(R.string.energy) + " - " + shortenDecimal(pastScanProducts.get(position).getEnergy() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.kcal)));
        holder.productFat.setText(String.format("%s", context.getResources().getString(R.string.fat) + " - " + shortenDecimal(pastScanProducts.get(position).getFat() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.g)));
        holder.productSaturates.setText(String.format("%s", context.getResources().getString(R.string.saturates) + " - " + shortenDecimal(pastScanProducts.get(position).getSaturates() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.g)));
        holder.productCarbohydrates.setText(String.format("%s", context.getResources().getString(R.string.carbohydrates) + " - " + shortenDecimal(pastScanProducts.get(position).getCarbohydrates() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.g)));
        holder.productSugars.setText(String.format("%s", context.getResources().getString(R.string.sugars) + " - " + shortenDecimal(pastScanProducts.get(position).getSugars() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.g)));
        holder.productProtein.setText(String.format("%s", context.getResources().getString(R.string.protein) + " - " + shortenDecimal(pastScanProducts.get(position).getProtein() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.g)));
        holder.productSalt.setText(String.format("%s", context.getResources().getString(R.string.salt) + " - " + shortenDecimal(pastScanProducts.get(position).getSalt() * pastScanProducts.get(position).getPercentEaten()) + context.getResources().getString(R.string.g)));

        String url = pastScanProducts.get(position).getUrl();

        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.productImage);

        holder.itemView.setOnClickListener(view -> {
            Log.d("position", Integer.toString(position));
            Observable<Integer> clickEmitter = Observable.just(position);
            clickEmitter.subscribe(clickObserver);
        });

        // check if it is fav
        boolean found = false;
        for (int i = 0; i < favProducts.size(); i++) {
            if (favProducts.get(i).getBarcode().equals(pastScanProducts.get(position).getBarcode())) {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                holder.fav = true;
                found = true;
            }
        }
        if (!found) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            holder.fav = false;
        }

    }

    /**
     * Method returns size of past scans list
     *
     * @return Size of past scans list
     */
    @Override
    public int getItemCount() {
        return pastScanProducts.size();
    }

    /**
     * Methods sets favorite list and past scans list and notify that it was changed
     *
     * @param favorites List of favorite products
     * @param products  List of past scans
     */
    public void setProducts(List<PastScan> products, List<FavProd> favorites) {
        this.pastScanProducts = products;
        this.favProducts = favorites;
        notifyDataSetChanged();
    }

    /**
     * Method returns past scan object by its position
     *
     * @param pos Position on the list
     * @return Scan with specified position
     */
    public PastScan getPastAt(int pos) {
        return pastScanProducts.get(pos);
    }

    /**
     * Method shortens double number to 2 decimal numbers
     *
     * @param input Number to shorten
     * @return Shortened number
     */
    private double shortenDecimal(double input) {
        String a = decimalFormat.format(input);
        double newDouble = 0;
        try {
            newDouble = Double.parseDouble(a.replace(',', '.'));
        } catch (NumberFormatException e) {
            //Error
            Log.e(TAG, e.getMessage());
        }
        return newDouble;
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
        public RelativeLayout viewBackground, viewForeground;

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
            viewBackground = itemView.findViewById(R.id.past_background);
            viewForeground = itemView.findViewById(R.id.past_foreground);

            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Log.d(TAG, position + " position");

                if (fav) {
                    fav = false;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                    goChangeFav(position, false);
                } else {
                    fav = true;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                    goChangeFav(position, true);
                }
            });
        }

    }


}

