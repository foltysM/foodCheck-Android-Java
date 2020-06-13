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

        //holder.productImage;
        Glide.with(context)
                .asBitmap()
                .load(products.get(position).getUrl())
                .thumbnail(0.5f)
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImagePast);
            productName = itemView.findViewById(R.id.productNamePast);
        }
    }
}
