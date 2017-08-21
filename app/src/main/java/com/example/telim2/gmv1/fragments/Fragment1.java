package com.example.telim2.gmv1.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.telim2.gmv1.R;

/**
 * Created by telim2 on 21.08.2017.
 */

public class Fragment1 extends Fragment {

    private Button btn;
    private TextView tw;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment1,container,false);

        btn=(Button)view.findViewById(R.id.button);
        tw=(TextView)view.findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               tw.setText("okkay");
            }
        });

        return view;

    }
}