package com.example.yamba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import org.w3c.dom.Text;

public class StatusFragment extends Fragment {

    private static final String TAG = "[StatusFragment]";
    private EditText editStatus;
    private Button buttonTweet;
    private TextView textCount;
    private int defaultTextColor;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        editStatus = view.findViewById(R.id.editStatus);
        buttonTweet = view.findViewById(R.id.buttonTweet);
        textCount = view.findViewById(R.id.textCount);

        buttonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = editStatus.getText().toString();
                Log.d(TAG, " buttonTweet clicked, status: " + status);
                Toast.makeText(StatusFragment.this.getActivity(),TAG + " buttonTweet clicked, status: " + status, Toast.LENGTH_LONG).show();
                new PostTask().execute(status);
            }
        });

        defaultTextColor = textCount.getTextColors().getDefaultColor();
        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int count = 140 - editStatus.length();
                textCount.setText(Integer.toString(count));
                textCount.setTextColor(Color.GREEN);
                if (count < 10)
                {
                    textCount.setTextColor(Color.RED);
                }
                else textCount.setTextColor(defaultTextColor);
                if (count < 0){
                    editStatus.setText(editStatus.getText().toString().substring(0, 140));
                    editStatus.setSelection(editStatus.getText().length());
                }
            }
        });

        return view;
    }

    private final class PostTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected  String doInBackground(String ... params)
        {
        //    YambaClient yambaCloud = new YambaClient("student", "password",
        //            "http://yamba.newcircle.com/api");

            try {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
                {
                    getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                    return "Please update your username and password";
                }

                YambaClient cloud = new YambaClient(username, password, "http://yamba.newcircle.com/api");
                cloud.postStatus(params[0]);
                return "Successfully posted!";
            }catch (YambaClientException e)
            {
                e.printStackTrace();
                return "Failed to post to yamba service!";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(StatusFragment.this.getActivity(), s, Toast.LENGTH_SHORT).show();
        }
    }
}
