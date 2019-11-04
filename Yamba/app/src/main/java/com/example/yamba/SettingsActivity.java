package com.example.yamba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.settings);

        if (savedInstanceState == null)
        {
            //Create fragment
            SettingsFragment fragment = new SettingsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.content, fragment, fragment.getClass().getSimpleName());
        }
        setContentView(R.layout.settings_activity);
    }
}
