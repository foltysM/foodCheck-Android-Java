package net.foltys.foodcheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment {
    private static final String TAG = "MainActivity";

    private Button saveBtn, backBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        saveBtn = getView().findViewById(R.id.saveButton); // TODO NULL jest
        backBtn = getView().findViewById(R.id.backButton);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save data
                Toast.makeText(getActivity(), R.string.data_saved, Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO go back
            }
        });
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


}
