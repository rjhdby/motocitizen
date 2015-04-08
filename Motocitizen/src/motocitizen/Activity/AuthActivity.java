package motocitizen.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.utils.Text;

/**
 * Created by pavel on 26.03.15.
 */
public class AuthActivity extends ActionBarActivity/* implements View.OnClickListener*/ {

    //private Button btnAuthConfirm;
    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;

    private EditText login;
    private EditText password;
    private CheckBox anonim;
    private SharedPreferences prefs;

    public static Context context;

    private void enableActionBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        loginBtn.setEnabled(anonim.isChecked() || logPasReady);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_auth);

        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
            @Override
            public void onClick(View v) {
                //TODO Добавить запрос подтверждения на выход.
                prefs.edit().remove("mc.login").commit();
                prefs.edit().remove("mc.password").commit();
                prefs.edit().remove("mc.name").commit();
                MCAccidents.auth.setAnonim(true);
                fillCtrls();
                return;
            }
        });

        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Анонимный вход
                if (anonim.isChecked()) {
                    MCAccidents.auth.setAnonim(true);
                    Text.set(context, R.id.auth_error_helper, "");
                    finish();
                } else { // Авторизация
                    MCAccidents.auth.setAnonim(false);
                    if (MCAccidents.auth.auth(login.getText().toString(), password.getText().toString())) {
                        finish();
                    } else {
                        TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
                        authErrorHelper.setText("Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
                    }
                }
            }
        });
        fillCtrls();
    }

    protected void fillCtrls() {
        login.setText(prefs.getString("mc.login", ""));
        password.setText(prefs.getString("mc.password", ""));

        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);

        //Авторизованы?
        if (prefs.getString("mc.name", "").length() > 0) {
            loginBtn.setVisibility(View.INVISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            anonim.setVisibility(View.INVISIBLE);
            accListYesterdayLine.setVisibility(View.INVISIBLE);
        } else {
            loginBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.INVISIBLE);
            anonim.setVisibility(View.VISIBLE);
            accListYesterdayLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }
}
