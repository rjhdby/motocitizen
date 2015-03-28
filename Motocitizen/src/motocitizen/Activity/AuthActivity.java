package motocitizen.Activity;

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

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCInit;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Show;
import motocitizen.utils.Text;

/**
 * Created by pavel on 26.03.15.
 */
public class AuthActivity extends ActionBarActivity/* implements View.OnClickListener*/{

    //private Button btnAuthConfirm;
    private Button actionBtn;
    private EditText login;
    private EditText password;
    private CheckBox anonim;

    public static Context context;

    private void enableActionBtn() {
        Boolean logPasReady = login.getText().toString().length() > 0 && password.getText().toString().length() > 0;
        actionBtn.setEnabled(anonim.isChecked() || logPasReady);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_auth);

        context = this;

        login = (EditText)findViewById(R.id.mc_auth_login);
        login.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableActionBtn();
            }
        });

        password = (EditText)findViewById(R.id.mc_auth_password);
        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableActionBtn();
            }
        });

        anonim = (CheckBox)findViewById(R.id.mc_auth_anonim);
        anonim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox)view;
                login.setEnabled(!checkBox.isChecked());
                password.setEnabled(!checkBox.isChecked());
                enableActionBtn();
            }
        });

        actionBtn = (Button) findViewById(R.id.action_button);
        actionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Startup.prefs.getString("mc.name", "").length() > 0) {
                    //TODO Добавить запрос подтверждения на выход.
                    Startup.prefs.edit().putString("mc.login", "").commit();
                    Startup.prefs.edit().putString("mc.password", "").commit();
                    Startup.prefs.edit().putString("mc.name", "").commit();
                    //TODO Ни чего не забыл?
                    fillCtrls();
                    return;
                }

                if (anonim.isChecked()) {
                    MCAccidents.auth.anonim = true;
                    Text.set(R.id.auth_error_helper, "");
                    //Show.show(R.id.main_frame_settings);
                    finish();
                } else {
                    MCAccidents.auth.anonim = false;
                    if(MCAccidents.auth.auth(login.getText().toString(), password.getText().toString())) {
                        MCAccidents.auth.setAccess(context);
                        MCInit.setupAccess(Startup.context, MCAccidents.auth);
                        MCInit.setupValues(MCAccidents.auth);
                        Text.set(R.id.auth_error_helper, "");
                        //Show.show(R.id.main_frame_settings);
                        finish();
                    } else {
                        TextView authErrorHelper = (TextView)findViewById(R.id.auth_error_helper);
                        authErrorHelper.setText("Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
                    }
                }
            }
        });
        fillCtrls();
    }

    protected void fillCtrls() {

        login.setText(Startup.prefs.getString("mc.login", ""));
        password.setText(Startup.prefs.getString("mc.password", ""));

        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);

        //Авторизованы?
        if(Startup.prefs.getString("mc.name", "").length() > 0) {
            actionBtn.setText(getString(R.string.logout_button));
            anonim.setVisibility(View.INVISIBLE);
            accListYesterdayLine.setVisibility(View.INVISIBLE);
        } else {
            actionBtn.setText(getString(R.string.login_button));
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

//    public void onClick(View v) {
//        if (v == btnAuthConfirm) {
//            if (MCAccidents.auth.anonim) {
//                Text.set(R.id.auth_error_helper, "");
//                Show.show(R.id.main_frame_settings);
//            } else {
//                Startup.prefsDef.edit().putString("mc.login", Text.get(R.id.mc_auth_login)).commit();
//                Startup.prefsDef.edit().putString("mc.password", Text.get(R.id.mc_auth_password)).commit();
//                MCAccidents.auth.auth();
//                MCAccidents.auth.setAccess(v.getContext());
//                MCInit.setupAccess(v.getContext(), MCAccidents.auth);
//                MCInit.setupValues(MCAccidents.auth);
//                if (MCAccidents.auth.name.equals("")) {
//                    Text.set(R.id.value_mcaccidents_auth_name, Startup.prefsDef.getString("mc.name", ""));
//                    Text.set(R.id.auth_error_helper, "Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
//                } else {
//                    Text.set(R.id.auth_error_helper, "");
//                    Show.show(R.id.main_frame_settings);
//                }
//            }
//        }else if (v == actionBtn) {
//            Startup.prefsDef.edit().putString("mc.login", "").commit();
//            Startup.prefsDef.edit().putString("mc.password", "").commit();
//            Startup.prefsDef.edit().putString("mc.name", "").commit();
//            //TODO Ни чего не забыл?
//        }
//    }
}
