package motocitizen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.user.Auth;
import motocitizen.utils.Preferences;
import motocitizen.utils.ShowToast;

public class AuthActivity extends AppCompatActivity/* implements View.OnClickListener*/ {

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
        try {
            Auth.init();
            if (Auth.getInstance().isAuthorized()) {
                startActivity(new Intent(this, MainScreenActivity.class));
            }
        } catch (Error e) {
            ShowToast.message(this, e.getLocalizedMessage());
        }
        setContentView(R.layout.auth);
        login = (EditText) findViewById(R.id.auth_login);
        password = (EditText) findViewById(R.id.auth_password);
        anonim = (CheckBox) findViewById(R.id.auth_anonim);
        cancelBtn = (Button) findViewById(R.id.cancel_button);
        logoutBtn = (Button) findViewById(R.id.logout_button);
        loginBtn = (Button) findViewById(R.id.login_button);
        TextView accListYesterdayLine = (TextView) findViewById(R.id.accListYesterdayLine);
        accListYesterdayLine.setMovementMethod(LinkMovementMethod.getInstance());
        fillCtrls();
    }

    private void fillCtrls() {

        login.setText(Preferences.getInstance().getLogin());
        password.setText(Preferences.getInstance().getPassword());
        anonim.setChecked(Preferences.getInstance().isAnonim());
        View     accListYesterdayLine = findViewById(R.id.accListYesterdayLine);
        TextView roleView             = (TextView) findViewById(R.id.role);

        boolean isAuthorized = Auth.getInstance().isAuthorized();
        loginBtn.setEnabled(!isAuthorized);
        logoutBtn.setEnabled(isAuthorized);
        anonim.setEnabled(!isAuthorized);
        accListYesterdayLine.setVisibility(isAuthorized ? View.GONE : View.VISIBLE);
        roleView.setVisibility(isAuthorized ? View.VISIBLE : View.GONE);
        login.setEnabled(!isAuthorized && !anonim.isChecked());
        password.setEnabled(!isAuthorized && !anonim.isChecked());
        //Авторизованы?
        if (isAuthorized) {
            String format = getString(R.string.auth_role);
            roleView.setText(String.format(format, Auth.getInstance().getRole().getName()));
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
        final Activity local = this;
        loginBtn.setOnClickListener(v -> {
            // Анонимный вход
            Preferences.getInstance().setAnonim(anonim.isChecked());
            if (anonim.isChecked()) {
                ((TextView) findViewById(R.id.auth_error_helper)).setText("");
                local.startActivity(new Intent(local, MainScreenActivity.class));
                return;
            }
            if (!MyApp.isOnline(getApplicationContext())) {
                ShowToast.message(getBaseContext(), getBaseContext().getString(R.string.auth_not_available));
                return;
            }
            if (Auth.getInstance().auth(login.getText().toString(), password.getText().toString())) {
                local.startActivity(new Intent(local, MainScreenActivity.class));
            } else {
                TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
                authErrorHelper.setText(R.string.auth_password_error);
            }
        });
        logoutBtn.setOnClickListener(v -> {
            //TODO Добавить запрос подтверждения на выход.
            Preferences.getInstance().resetAuth();
            Preferences.getInstance().setAnonim(true);
            MyApp.logoff();
            fillCtrls();
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
        anonim.setOnClickListener(view -> {
            CheckBox checkBox = (CheckBox) view;
            login.setEnabled(!checkBox.isChecked());
            password.setEnabled(!checkBox.isChecked());
            enableLoginBtn();
        });
        cancelBtn.setOnClickListener(v -> finish());
    }
}
