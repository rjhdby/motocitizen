package motocitizen.ui.activity;

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

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.user.User;
import motocitizen.datasources.preferences.Preferences;
import motocitizen.utils.ToastUtils;

public class AuthActivity extends AppCompatActivity {

    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;
    private Button loginVK;

    private EditText login;
    private EditText password;
    private CheckBox anonymous;

    private void enableLoginBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        loginBtn.setEnabled(anonymous.isChecked() || logPasReady);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(getApplicationContext(), "Пользователь успешно авторизовался", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Произошла ошибка авторизации (например, пользователь запретил авторизацию)", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
        bindViews();
        setUpListeners();

        if (User.INSTANCE.isAuthorized()) {
            Router.INSTANCE.goTo(this, Router.Target.MAIN);
        }

        vkWakeUpSession();

        ((TextView) findViewById(R.id.accListYesterdayLine)).setMovementMethod(LinkMovementMethod.getInstance());
        fillCtrls();
    }

    private void bindViews() {
        login = (EditText) findViewById(R.id.auth_login);
        password = (EditText) findViewById(R.id.auth_password);
        anonymous = (CheckBox) findViewById(R.id.auth_anonim);
        cancelBtn = (Button) findViewById(R.id.cancel_button);
        logoutBtn = (Button) findViewById(R.id.logout_button);
        loginBtn = (Button) findViewById(R.id.login_button);
        loginVK = (Button) findViewById(R.id.vk);
    }

    private void setUpListeners() {
        loginVK.setOnClickListener(v -> VKSdk.login(AuthActivity.this, VKScope.PAGES));
        findViewById(R.id.vk333).setOnClickListener(v -> vkAuth());
        loginBtn.setOnClickListener(v -> loginButtonPressed());
        logoutBtn.setOnClickListener(v -> logOutButtonPressed());
        login.addTextChangedListener(textWatcher());
        password.addTextChangedListener(textWatcher());
        anonymous.setOnClickListener(v -> anonymousCheckBoxPressed());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void loginButtonPressed() {
        // Анонимный вход
        Preferences.INSTANCE.setAnonymous(anonymous.isChecked());
        if (anonymous.isChecked()) {
            ((TextView) findViewById(R.id.auth_error_helper)).setText("");
            Router.INSTANCE.goTo(AuthActivity.this, Router.Target.MAIN);
            return;
        }
        if (!MyApp.isOnline(AuthActivity.this)) {
            ToastUtils.show(AuthActivity.this, AuthActivity.this.getString(R.string.auth_not_available));
            return;
        }
        auth();
    }

    private void logOutButtonPressed() {
        //TODO Добавить запрос подтверждения на выход.
        Preferences.INSTANCE.resetAuth();
        Preferences.INSTANCE.setAnonymous(true);
        MyApp.logoff();
        fillCtrls();
    }

    private void anonymousCheckBoxPressed() {
        login.setEnabled(!anonymous.isChecked());
        password.setEnabled(!anonymous.isChecked());
        enableLoginBtn();
    }

    private void vkWakeUpSession() {
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                switch (res) {
                    case LoggedOut:
                        //showLogin();
                        break;
                    case LoggedIn:
                        Router.INSTANCE.goTo(AuthActivity.this, Router.Target.MAIN);
                        //showLogout();
                        break;
                    case Pending:
                        break;
                    case Unknown:
                        break;
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });
    }

    private void vkAuth() {
        VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
    }

    private void fillCtrls() {
        login.setText(Preferences.INSTANCE.getLogin());
        password.setText(Preferences.INSTANCE.getPassword());
        anonymous.setChecked(Preferences.INSTANCE.getAnonymous());
        View     accListYesterdayLine = findViewById(R.id.accListYesterdayLine);
        TextView roleView             = (TextView) findViewById(R.id.role);

        boolean isAuthorized = User.INSTANCE.isAuthorized();
        loginBtn.setEnabled(!isAuthorized);
        logoutBtn.setEnabled(isAuthorized);
        anonymous.setEnabled(!isAuthorized);
        accListYesterdayLine.setVisibility(isAuthorized ? View.GONE : View.VISIBLE);
        roleView.setVisibility(isAuthorized ? View.VISIBLE : View.GONE);
        login.setEnabled(!isAuthorized && !anonymous.isChecked());
        password.setEnabled(!isAuthorized && !anonymous.isChecked());
        //Авторизованы?
        if (isAuthorized) {
            String format = getString(R.string.auth_role);
            roleView.setText(String.format(format, User.INSTANCE.getRoleName()));
        } else {
            enableLoginBtn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        };
    }

    private void auth() {
        User.INSTANCE.auth(login.getText().toString(), password.getText().toString(), authCallback());
    }

    private Function1<JSONObject, Unit> authCallback() {
        return result -> {
            if (User.INSTANCE.isAuthorized()) {
                Router.INSTANCE.goTo(AuthActivity.this, Router.Target.MAIN);
            } else {
                showAuthError();
            }
            return Unit.INSTANCE;
        };
    }

    private void showAuthError() {
        AuthActivity.this.runOnUiThread(() -> {
            TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
            authErrorHelper.setText(R.string.auth_password_error);
        });
    }
}
