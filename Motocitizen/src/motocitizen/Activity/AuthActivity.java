package motocitizen.Activity;

import android.annotation.SuppressLint;
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

    private void enableLoginBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        loginBtn.setEnabled(anonim.isChecked() || logPasReady);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setCurrentActivity(this);
        setContentView(R.layout.auth);
        login = (EditText) findViewById(R.id.mc_auth_login);
        password = (EditText) findViewById(R.id.mc_auth_password);
        anonim = (CheckBox) findViewById(R.id.mc_auth_anonim);
        cancelBtn = (Button) findViewById(R.id.cancel_button);
        logoutBtn = (Button) findViewById(R.id.logout_button);
        loginBtn = (Button) findViewById(R.id.login_button);
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

        boolean isAuthorized = Content.auth.isAuthorized();
        loginBtn.setEnabled(!isAuthorized);
        logoutBtn.setEnabled(isAuthorized);
        anonim.setEnabled(!isAuthorized);
        accListYesterdayLine.setVisibility(isAuthorized ? View.GONE : View.VISIBLE);
        roleView.setVisibility(isAuthorized ? View.VISIBLE : View.GONE);
        login.setEnabled(!isAuthorized && !anonim.isChecked());
        password.setEnabled(!isAuthorized && !anonim.isChecked());
        //Авторизованы?
        if (isAuthorized) {
            String format = getString(R.string.mc_auth_role);
            roleView.setText(String.format(format, Content.auth.getRole().getName()));
        } else {
            enableLoginBtn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.setCurrentActivity(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Анонимный вход
                Preferences.setAnonim(anonim.isChecked());
                if (anonim.isChecked()) {
                    ((TextView) findViewById(R.id.auth_error_helper)).setText("");
                    finish();
                    return;
                }
                if (!Startup.isOnline()) {
                    showToast(R.string.auth_not_available);
                    return;
                }
                if (MyApp.getAuth().auth(login.getText().toString(), password.getText().toString())) {
                    finish();
                } else {
                    TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
                    authErrorHelper.setText(R.string.auth_password_error);
                }
            }


        });
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
        login.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        });
        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        });
        anonim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                login.setEnabled(!checkBox.isChecked());
                password.setEnabled(!checkBox.isChecked());
                enableLoginBtn();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showToast(int res) {
        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
    }
}
