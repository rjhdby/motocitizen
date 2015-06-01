package motocitizen.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import motocitizen.MyApp;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.startup.MyPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

public class AuthActivity extends ActionBarActivity/* implements View.OnClickListener*/ {

    private MyApp myApp = null;

    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;

    private EditText login;
    private EditText password;
    private CheckBox anonim;
    private MyPreferences prefs;

    private static Context context;

    private void enableLoginBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        loginBtn.setEnabled(anonim.isChecked() || logPasReady);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_auth);

        myApp = (MyApp) getApplicationContext();

        context = this;
        prefs = myApp.getPreferences();
        login = (EditText) findViewById(R.id.mc_auth_login);
        login.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        });

        password = (EditText) findViewById(R.id.mc_auth_password);
        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        });

        anonim = (CheckBox) findViewById(R.id.mc_auth_anonim);
        anonim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                login.setEnabled(!checkBox.isChecked());
                password.setEnabled(!checkBox.isChecked());
                enableLoginBtn();
            }
        });

        cancelBtn = (Button) findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoutBtn = (Button) findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                //TODO Добавить запрос подтверждения на выход.
                prefs.resetAuth();
                prefs.setAnonim(true);
                AccidentsGeneral.auth.logoff();
                fillCtrls();
            }
        });

        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Анонимный вход
                if (anonim.isChecked()) {
                    prefs.setAnonim(true);
                    Text.set(context, R.id.auth_error_helper, "");
                    finish();
                } else { // Авторизация
                    if (Startup.isOnline()) {
                        if (AccidentsGeneral.auth.auth(Startup.context, login.getText().toString(), password.getText().toString())) {
                            prefs.setAnonim(false);
                            finish();
                        } else {
                            TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
                            authErrorHelper.setText(R.string.auth_password_error);
                        }
                    } else {
                        //TODO Перенести в ресурсы
                        Toast.makeText(context, R.string.auth_not_available, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        TextView accListYesterdayLine = (TextView) findViewById(R.id.accListYesterdayLine);
        accListYesterdayLine.setMovementMethod(LinkMovementMethod.getInstance());

        fillCtrls();
    }

    void fillCtrls() {

        login.setText(prefs.getLogin());
        password.setText(prefs.getPassword());
        anonim.setChecked(prefs.isAnonim());
        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);
        TextView roleView = (TextView)findViewById(R.id.role);

        //Авторизованы?
        if (AccidentsGeneral.auth.isAuthorized()) {
            loginBtn.setEnabled(false);
            logoutBtn.setEnabled(true);
            anonim.setEnabled(false);
            accListYesterdayLine.setVisibility(View.GONE);
            String format = getString(R.string.mc_auth_role);
            roleView.setText(String.format(format, Role.getName(this)));
            roleView.setVisibility(View.VISIBLE);
            login.setEnabled(false);
            password.setEnabled(false);
        } else {
//        if (prefs.isAnonim()) {
            loginBtn.setEnabled(true);
            logoutBtn.setEnabled(false);
            anonim.setEnabled(true);
            login.setEnabled(!anonim.isChecked());
            password.setEnabled(!anonim.isChecked());
            accListYesterdayLine.setVisibility(View.VISIBLE);
            roleView.setVisibility(View.GONE);
            enableLoginBtn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
