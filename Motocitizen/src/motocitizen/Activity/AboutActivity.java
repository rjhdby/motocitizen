package motocitizen.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

import motocitizen.main.R;
import motocitizen.startup.ChangeLog;
import motocitizen.startup.MCPreferences;

public class AboutActivity extends ActionBarActivity {

//Посмотреть http://android-developers.blogspot.in/2013/08/actionbarcompat-and-io-2013-app-source.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ((TextView) this.findViewById(R.id.about_code_version)).setText(getString(R.string.code_version_prefix) + ": " + (new MCPreferences(this)).getCurrentVersion());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        WebView wv = (WebView) findViewById(R.id.change_log);
        wv.setBackgroundColor(Color.rgb(48, 48, 48));
        wv.loadDataWithBaseURL(null, ChangeLog.getLog(this, true), "text/html", "UTF-8", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }
}
