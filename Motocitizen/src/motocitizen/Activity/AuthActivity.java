package motocitizen.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.startup.MCPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

public class AuthActivity extends ActionBarActivity/* implements View.OnClickListener*/ {

    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;

    private EditText login;
    private EditText password;
    private CheckBox anonim;
    //private SharedPreferences prefs;
    private MCPreferences prefs;

    private static Context context;

    private void enableActionBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        loginBtn.setEnabled(anonim.isChecked() || logPasReady);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_auth);

        context = this;
        //prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = new MCPreferences(this);
        login = (EditText) findViewById(R.id.mc_auth_login);
        login.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableActionBtn();
            }
        });

        password = (EditText) findViewById(R.id.mc_auth_password);
        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableActionBtn();
            }
        });

        anonim = (CheckBox) findViewById(R.id.mc_auth_anonim);
        anonim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                login.setEnabled(!checkBox.isChecked());
                password.setEnabled(!checkBox.isChecked());
                enableActionBtn();
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
                    if(Startup.isOnline()) {
                        prefs.setAnonim(false);
                        if (MCAccidents.auth.auth(Startup.context, login.getText().toString(), password.getText().toString())) {
                            finish();
                        } else {
                            TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
                            authErrorHelper.setText("Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
                        }
                    } else {
                        //TODO Перенести в ресурсы
                        Toast.makeText(context, "Авторизация не возможна, пожалуйста, проверьте доступность Internet.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        fillCtrls();
    }

    void fillCtrls() {

        login.setText(prefs.getLogin());
        password.setText(prefs.getPassword());
        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);

        //Авторизованы?
        if (MCAccidents.auth.isAuthorized()) {
            loginBtn.setEnabled(false);
            logoutBtn.setEnabled(true);
            anonim.setEnabled(false);
            accListYesterdayLine.setEnabled(false);
        } else {
//        if (prefs.isAnonim()) {
            loginBtn.setEnabled(true);
            logoutBtn.setEnabled(false);
            anonim.setEnabled(true);
            accListYesterdayLine.setEnabled(true);
            enableActionBtn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
