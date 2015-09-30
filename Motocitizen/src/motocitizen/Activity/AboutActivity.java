package motocitizen.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.startup.ChangeLog;
import motocitizen.startup.Preferences;

public class AboutActivity extends ActionBarActivity implements View.OnClickListener {

//Посмотреть http://android-developers.blogspot.in/2013/08/actionbarcompat-and-io-2013-app-source.html

    Button bussinesCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setCurrentActivity(this);
        setContentView(R.layout.activity_about);

        ((TextView) this.findViewById(R.id.about_code_version)).setText(getString(R.string.code_version_prefix) + ": " + Preferences.getCurrentVersion());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        WebView wv = (WebView) findViewById(R.id.change_log);
        wv.setBackgroundColor(Color.rgb(48, 48, 48));
        wv.loadDataWithBaseURL(null, ChangeLog.getLog(this, true), "text/html", "UTF-8", null);

        bussinesCardButton = (Button) findViewById(R.id.bussinesCardButton);
        bussinesCardButton.setOnClickListener(this);

        TextView url_support = (TextView) findViewById(R.id.about_url_support);
        url_support.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bussinesCardButton:
                this.startActivity(new Intent(this, BusinessCardActivity.class));
                break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MyApp.setCurrentActivity(this);
    }
}
