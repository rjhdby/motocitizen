package motocitizen.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.utils.ChangeLog;
import motocitizen.utils.Preferences;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

//Посмотреть http://android-developers.blogspot.in/2013/08/actionbarcompat-and-io-2013-app-source.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ((TextView) this.findViewById(R.id.about_code_version)).setText(getString(R.string.code_version_prefix) + ": " + Preferences.Companion.getInstance(this).getAppVersion());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        WebView wv = (WebView) findViewById(R.id.change_log);
        wv.setBackgroundColor(Color.rgb(48, 48, 48));
        wv.loadDataWithBaseURL(null, ChangeLog.getLog(this), "text/html", "UTF-8", null);

        findViewById(R.id.businessCardButton).setOnClickListener(this);

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
        if (view.getId() == R.id.businessCardButton) Router.INSTANCE.goTo(this, Router.Target.BUSINESS_CARD);
    }
}
