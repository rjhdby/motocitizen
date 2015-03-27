package motocitizen.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

    private Button btnAuthConfirm;
    private Button btnLogout;
    private EditText login;
    private EditText password;
    private CheckBox anonim;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_auth);

        context = this;

        login = (EditText)findViewById(R.id.mc_auth_login);
        password = (EditText)findViewById(R.id.mc_auth_password);
        anonim = (CheckBox)findViewById(R.id.mc_auth_anonim);

        btnAuthConfirm = (Button) findViewById(R.id.mc_auth_confirm_button);
        btnAuthConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //TODO Действительно авторизованы?
                if(Startup.prefsDef.getString("mc.name", "").length() > 0)
                    return;

                if (anonim.isChecked()) {
                    MCAccidents.auth.anonim = true;
                    Text.set(R.id.auth_error_helper, "");
                    Show.show(R.id.main_frame_settings);
                } else {
                    MCAccidents.auth.anonim = false;
                    Startup.prefsDef.edit().putString("mc.login", login.getText().toString()).commit();
                    Startup.prefsDef.edit().putString("mc.password", password.getText().toString()).commit();
                    MCAccidents.auth.auth();
                    MCAccidents.auth.setAccess(context);
                    MCInit.setupAccess(Startup.context, MCAccidents.auth);
                    MCInit.setupValues(MCAccidents.auth);
                    if (MCAccidents.auth.name.equals("")) {
                        //Text.set(R.id.value_mcaccidents_auth_name, Startup.prefsDef.getString("mc.name", ""));
                        //Text.set(R.id.auth_error_helper, "Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
                        EditText authErrorHelper = (EditText)findViewById(R.id.auth_error_helper);
                        authErrorHelper.setText("Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
                    } else {
                        Text.set(R.id.auth_error_helper, "");
                        //Show.show(R.id.main_frame_settings);
                        finish();
                    }
                }
            }
        });

        btnLogout = (Button) findViewById(R.id.logout_button);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Startup.prefsDef.edit().putString("mc.login", "").commit();
                Startup.prefsDef.edit().putString("mc.password", "").commit();
                Startup.prefsDef.edit().putString("mc.name", "").commit();
                //TODO Ни чего не забыл?
                fillCtrls();
            }
        });

        fillCtrls();
    }

    protected void fillCtrls() {

        login.setText(Startup.prefsDef.getString("mc.login", ""));
        password.setText(Startup.prefsDef.getString("mc.password", ""));

        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);

        //Авторизованы?
        if(Startup.prefsDef.getString("mc.name", "").length() > 0) {
            btnLogout.setVisibility(View.VISIBLE);
            anonim.setVisibility(View.INVISIBLE);
            accListYesterdayLine.setVisibility(View.INVISIBLE);
        } else {
            btnLogout.setVisibility(View.INVISIBLE);
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
//        }else if (v == btnLogout) {
//            Startup.prefsDef.edit().putString("mc.login", "").commit();
//            Startup.prefsDef.edit().putString("mc.password", "").commit();
//            Startup.prefsDef.edit().putString("mc.name", "").commit();
//            //TODO Ни чего не забыл?
//        }
//    }
}
