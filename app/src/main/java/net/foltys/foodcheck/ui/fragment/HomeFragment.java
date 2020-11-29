package net.foltys.foodcheck.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.foltys.foodcheck.R;


public class HomeFragment extends Fragment {
    private Context context;
    private String name;
    private String photoURL;
    private ImageView imageView;
    private TextView nameTextView;

    public HomeFragment(String name, String photoURL) {
        this.name = name;
        this.photoURL = photoURL;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.photo_home);
        nameTextView = view.findViewById(R.id.name_home);

        nameTextView.setText(name);

        if (photoURL.equals("")) {
            imageView.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(photoURL)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }
}