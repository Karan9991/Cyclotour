package com.example.cyclotourhub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomSheetActivity extends AppCompatActivity {

    BottomSheetBehavior mBottomSheetBehaviour;
    Button openBottomSheetButton;
    Button closeBottomSheetButton;
    View nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
        bind();

        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        //Remove this line to disable peek
        mBottomSheetBehaviour.setPeekHeight(200);

        onOpenSheetClicked();
        onCloseSheetClicked();
        stateCallBacks();

    }

    private void onOpenSheetClicked() {
        openBottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void onCloseSheetClicked() {
        closeBottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void stateCallBacks() {
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                String state = "";

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        state = "DRAGGING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {
                        state = "SETTLING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        state = "EXPANDED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        state = "COLLAPSED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        state = "HIDDEN";
                        break;
                    }
                }

                Toast.makeText(BottomSheetActivity.this, "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void bind() {
        openBottomSheetButton = findViewById(R.id.openBottomSheetButton);
        closeBottomSheetButton = findViewById(R.id.closeBottomSheetButton);
        nestedScrollView = findViewById(R.id.nestedScrollView);
    }


}
