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
    private PastScanViewModel mPastScanViewModel;
    private Context context;
    private TextView lastAte;
    private TextView number;

    /**
     * Constructor of Home Fragment, empty by default
     */
    public HomeFragment() {
    }

    /**
     * Method returns new Instance of Home Fragment with parameters attached
     *
     * @param name     User name
     * @param photoURL URL to user's Google profile picture
     * @return New instance of HomeFragment
     */
    public static HomeFragment newInstance(String name, String photoURL) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("url", photoURL);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when a fragment is first attached to its context.
     *
     * @param context Context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    /**
     * Method called while creating Fragment. Initialises view model
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);

    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and non-graphical fragments can return null (which is the default implementation). This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view. This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created. The fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.photo_home);
        TextView nameTextView = view.findViewById(R.id.name_home);
        lastAte = view.findViewById(R.id.last_scan);
        number = view.findViewById(R.id.number_of_scans);

        assert getArguments() != null;
        String name = getArguments().getString("name");
        String photoURL = getArguments().getString("url");
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