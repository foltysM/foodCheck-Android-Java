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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.PastScanViewModel;

public class HomeFragment extends Fragment {
    private final String name;
    private final String photoURL;
    PastScanViewModel mPastScanViewModel;
    private Context context;
    TextView lastAte;
    TextView number;

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
        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);

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
        ImageView imageView = view.findViewById(R.id.photo_home);
        TextView nameTextView = view.findViewById(R.id.name_home);
        lastAte = view.findViewById(R.id.last_scan);
        number = view.findViewById(R.id.number_of_scans);

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
        mPastScanViewModel.getAllPastScans().observe(getViewLifecycleOwner(), pastScans -> {
            number.setText(String.format("%s", pastScans.size()));
            String last = "0000-00-00";
            for (int i = 0; i < pastScans.size(); i++) {
                String concatenated = pastScans.get(i).getYear() + "-" + pastScans.get(i).getMonth() + "-" + pastScans.get(i).getDay();
                if (concatenated.compareTo(last) > 0) {
                    last = concatenated;
                }
            }
            if (last.equals("0000-00-00"))
                last = getResources().getString(R.string.no_scans);
            lastAte.setText(last);
        });
    }
}