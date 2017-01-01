package motocitizen.Activity;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import motocitizen.MyApp;
import motocitizen.user.Auth;
import motocitizen.main.R;
import motocitizen.utils.Preferences;

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
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

        login.setText(Preferences.getLogin());
        password.setText(Preferences.getPassword());
        anonim.setChecked(Preferences.isAnonim());
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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Анонимный вход
                Preferences.setAnonim(anonim.isChecked());
                if (anonim.isChecked()) {
                    ((TextView) findViewById(R.id.auth_error_helper)).setText("");
                    local.startActivity(new Intent(local, MainScreenActivity.class));
                    return;
                }
                if (!MyApp.isOnline(getApplicationContext())) {
                    showToast(R.string.auth_not_available);
                    return;
                }
                if (Auth.getInstance().auth(login.getText().toString(), password.getText().toString())) {
                    local.startActivity(new Intent(local, MainScreenActivity.class));
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
                MyApp.logoff();
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
