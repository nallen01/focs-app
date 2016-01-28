package me.nallen.fox.app;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    private Spinner scorer_location;
    private EditText fox_server_ip;
    private EditText automation_server_ip;
    private Button connect_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Connect");
        setContentView(R.layout.activity_connect);

        List<String> locationOptions = new ArrayList<String>();
        for(ScorerLocation location : ScorerLocation.values()) {
            locationOptions.add(location.getName());
        }

        scorer_location = (Spinner) findViewById(R.id.spinner_scorer_location);
        fox_server_ip = (EditText) findViewById(R.id.input_fox_ip);
        automation_server_ip = (EditText) findViewById(R.id.input_automation_ip);
        connect_button = (Button) findViewById(R.id.button_connect);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationOptions);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scorer_location.setAdapter(arrayAdapter);

        scorer_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                View holder = findViewById(R.id.input_layout_automation_ip);
                if (ScorerLocation.values()[pos] == ScorerLocation.COMMENTATOR_AUTOMATION) {
                    holder.setVisibility(View.VISIBLE);
                } else {
                    holder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        boolean auto_login = true;

        if(getIntent().getIntExtra("scorer_location", -1) != -1) {
            scorer_location.setSelection(getIntent().getIntExtra("scorer_location", -1));
        }
        else {
            scorer_location.setSelection(0);
            auto_login = false;
        }

        if(getIntent().getStringExtra("fox_ip") != null) {
            fox_server_ip.setText(getIntent().getStringExtra("fox_ip"));
        }
        else {
            auto_login = false;
        }

        if(getIntent().getStringExtra("automation_ip") != null) {
            automation_server_ip.setText(getIntent().getStringExtra("automation_ip"));
        }
        else {
            auto_login = false;
        }

        // If we have data already then let's try automatically login
        if(auto_login) {
            login();
        }
    }

    private void login() {
        // Get all the fields
        String fox_ip = fox_server_ip.getText().toString();
        String automation_ip = automation_server_ip.getText().toString();
        ScorerLocation location = ScorerLocation.values()[scorer_location.getSelectedItemPosition()];

        if(!Patterns.IP_ADDRESS.matcher(fox_ip).matches()) {
            Toaster.doToast(getApplicationContext(), "Invalid Fox IP entered");
        }

        if(location == ScorerLocation.COMMENTATOR_AUTOMATION) {
            if(!Patterns.IP_ADDRESS.matcher(automation_ip).matches()) {
                Toaster.doToast(getApplicationContext(), "Invalid Automation IP entered");
            }
        }
    }
}
