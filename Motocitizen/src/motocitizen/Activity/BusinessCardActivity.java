package motocitizen.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import motocitizen.MyApp;
import motocitizen.main.R;

public class BusinessCardActivity extends ActionBarActivity {

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
