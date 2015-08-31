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

import motocitizen.app.general.user.Role;
import motocitizen.content.Content;
import motocitizen.main.R;
import motocitizen.startup.Preferences;
import motocitizen.startup.Startup;

public class AuthActivity extends ActionBarActivity/* implements View.OnClickListener*/ {

    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;

    private EditText login;
    private EditText password;
    private CheckBox anonim;

    private static Context context;

    private void enableLoginBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        loginBtn.setEnabled(anonim.isChecked() || logPasReady);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);

        context = this;
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
                Preferences.resetAuth();
                Preferences.setAnonim(true);
                Content.auth.logoff();
                fillCtrls();
            }
        });

        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Анонимный вход
                if (anonim.isChecked()) {
                    Preferences.setAnonim(true);
                    ((TextView) findViewById(R.id.auth_error_helper)).setText("");
                    finish();
                } else { // Авторизация
                    if (Startup.isOnline(context)) {
                        if (Content.auth.auth(context, login.getText().toString(), password.getText().toString())) {
                            Preferences.setAnonim(false);
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

    private void fillCtrls() {

        login.setText(Preferences.getLogin());
        password.setText(Preferences.getPassword());
        anonim.setChecked(Preferences.isAnonim());
        View     accListYesterdayLine = findViewById(R.id.accListYesterdayLine);
        TextView roleView             = (TextView) findViewById(R.id.role);

        //Авторизованы?
        if (Content.auth.isAuthorized()) {
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
