package com.example.cyclotourhub.fragment_dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cyclotourhub.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    Button btnCache, btnCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        initialize(view);
        btnCache.setOnClickListener(this);
        btnCard.setOnClickListener(this);
        return view;
    }

    private void initialize(View view) {
        btnCache = view.findViewById(R.id.btnCache);
        btnCard = view.findViewById(R.id.btnCard);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCache:
                Toast.makeText(getActivity(), btnCache.getText().toString(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCard:
                Toast.makeText(getActivity(), btnCard.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
