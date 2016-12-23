package motocitizen.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import motocitizen.MyApp;
import motocitizen.main.R;

public class BusinessCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setCurrentActivity(this);
        setContentView(R.layout.activity_business_card);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.setCurrentActivity(this);
    }
}
