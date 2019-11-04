package com.example.yamba;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class StatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
        {
            StatusFragment fragment = new StatusFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, fragment, fragment.getClass().getSimpleName());
                  //  .commit();
        }
        setContentView(R.layout.new_activity_status);
    }

}
