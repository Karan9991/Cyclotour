package com.example.cyclotourhub.fragment_dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cyclotourhub.R;

public class BottomSheetFragmentActivity extends AppCompatActivity {

    Button showBottomSheet;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_fragment);
        showBottomSheet = findViewById(R.id.showSheetButton);
        showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment fragment = new BottomSheetFragment();
                fragment.show(getSupportFragmentManager(), TAG);
            }
        });
    }
}
