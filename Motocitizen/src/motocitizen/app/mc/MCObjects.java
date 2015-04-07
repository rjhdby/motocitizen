package motocitizen.app.mc;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import motocitizen.main.R;
import motocitizen.startup.Startup;

public class MCObjects {
    //public static final Button authButton = (Button) findView(R.id.app_mcaccidents_register_button);
    //public static final Button authConfirmButton = (Button) findView(R.id.mc_auth_confirm_button);
    //public static final Button authCancelButton = (Button) findView(R.id.mc_auth_cancel_button);
    // zz
    // public static final Button newMessageButton = (Button) findView(R.id.mc_new_message_send);
    public static final ImageButton dialButton = (ImageButton) findView(R.id.dial_button);
    public static final ImageButton createAccButton = (ImageButton) findView(R.id.mc_add_point_button);
    public static final CheckBox authAnonimCheckBox = (CheckBox) findView(R.id.mc_auth_anonim);
    public static final Button firstLoginButton = (Button) findView(R.id.first_auth_login);
    public static final Button anonimButton = (Button) findView(R.id.first_auth_anonim);
    public static final Button selectSoundButton = (Button) findView(R.id.mc_notif_sound_button);
    public static final Button selectSoundConfirmButton = (Button) findView(R.id.select_sound_save_button);
    public static final Button selectSoundCancelButton = (Button) findView(R.id.select_sound_cancel_button);
    public static final EditText inputCommentField = (EditText) findView(R.id.mc_new_message_text);

    /*
     * Описание группы основных закладок
     */
    public static final RadioGroup mainTabsGroup = (RadioGroup) findView(R.id.main_tabs_group);
    public static final RadioButton tabAccidentsButton = (RadioButton) findView(R.id.tab_accidents_button);
    public static final RadioButton tabMapButton = (RadioButton) findView(R.id.tab_map_button);
    public static final View accListView = findView(R.id.mc_acc_list);
    public static final View mapContainer = findView(R.id.map_container);

    /*
     * Объекты в активности создания события
     */
    public static final Button mcCreateFineAddressButton = (Button) findView(R.id.mc_create_fine_address_button);
    public static final Button mcCreateFineAddressConfirm = (Button) findView(R.id.mc_create_fine_address_confirm);
    public static final View mcCreateMap = findView(R.id.mc_create_map);
    public static final ImageView mcCreateMapPointer = (ImageView) findView(R.id.mc_create_map_pointer);
    /*
     * Объекты в меню settings
     */
    //public static final EditText valueAppMcaccidentsDistance = (EditText) findView(R.id.value_app_mcaccidents_distance);
    //public static final EditText valueAppMcaccidentsDistanceAlarm = (EditText) findView(R.id.value_app_mcaccidents_distance_alarm);

    private static View findView(int id) {
        return ((Activity) Startup.context).findViewById(id);
    }
}
