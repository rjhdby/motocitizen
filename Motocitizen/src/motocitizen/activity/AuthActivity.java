package motocitizen.activity;

import android.app.Activity;
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
import motocitizen.router.Router;
import motocitizen.user.User;
import motocitizen.utils.Preferences;
import motocitizen.utils.ToastUtils;

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
            User.init();
            if (User.getInstance().isAuthorized()) {
                Router.goTo(this, Router.Target.MAIN);
            }
        } catch (Error e) {
            ToastUtils.show(this, e.getLocalizedMessage());
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

        boolean isAuthorized = User.getInstance().isAuthorized();
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
            roleView.setText(String.format(format, User.getInstance().getRoleName()));
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
                Router.goTo(local, Router.Target.MAIN);
                return;
            }
            if (!MyApp.isOnline(AuthActivity.this)) {
                ToastUtils.show(AuthActivity.this, AuthActivity.this.getString(R.string.auth_not_available));
                return;
            }
            if (auth()) {
                Router.goTo(local, Router.Target.MAIN);
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

    private boolean auth() {
        try {
            return User.getInstance().auth(login.getText().toString(), password.getText().toString());
        } catch (Error error) {
            error.printStackTrace();
            return false;
        }
    }
}
