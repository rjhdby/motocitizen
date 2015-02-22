package motocitizen.app.mc.create;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCNomination;
import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Show;
import motocitizen.utils.Text;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MCCreateAcc {
	private static final Activity act = (Activity) Startup.context;
	private static final Button back = (Button) act.findViewById(R.id.mc_create_back);
	private static final Button confirm = (Button) act.findViewById(R.id.mc_create_create);
	private static final Button cancel = (Button) act.findViewById(R.id.mc_create_cancel);
	private static final View includeArea = (View) act.findViewById(R.id.mc_create_main_frame);
	private static final View globalView = (View) act.findViewById(R.id.mc_create_main);
	private static final EditText details = (EditText) act.findViewById(R.id.mc_create_final_text);
	private static final int TYPE = R.id.mc_create_type_frame;
	private static final int FINAL = R.id.mc_create_final_frame;
	private static final int ACC = R.id.mc_create_acc_frame;
	private static final int PEOPLE = R.id.mc_create_people_frame;
	
	private static Date date = new Date();
	private static String globalText = "";
	private static String medText = "mc_m_na";
	private static String ownerText = Startup.prefs.getString("mc.login", "");
	private static String addressText = MCNomination.getAddress();
	private static String timeText = Const.timeFormat.format((date).getTime());
	private static String type, med;
	private static String previsionBack;
	private static Location location = MCNomination.location;
	private static int CURRENT = TYPE;
	private static boolean isAcc = false;
	

	public MCCreateAcc() {
		previsionBack = Startup.prefs.getString("backButton", "");
		Startup.prefs.edit().putString("backButton", "motocitizen.app.mc.create.MCCreateMessage").commit();
		writeGlobal();
		setListeners();
	}

	private static void writeGlobal() {
		if (!medText.equals("mc_m_na")) {
			Text.set(globalView, R.id.mc_create_what, globalText + ". " + medText);
		} else {
			Text.set(globalView, R.id.mc_create_what, globalText);
		}
		Text.set(globalView, R.id.mc_create_who, ownerText);
		Text.set(globalView, R.id.mc_create_where, addressText);
		Text.set(globalView, R.id.mc_create_when, timeText);
	}

	private void setListeners() {
		int[] buttons = { R.id.mc_create_acc_ma_button, R.id.mc_create_acc_solo_button, R.id.mc_create_acc_mm_button, R.id.mc_create_acc_mp_button,
				R.id.mc_create_people_death_button, R.id.mc_create_people_hard_button, R.id.mc_create_people_light_button,
				R.id.mc_create_people_ok_button, R.id.mc_create_type_acc_button, R.id.mc_create_type_break_button, R.id.mc_create_type_steal_button,
				R.id.mc_create_type_other_button, R.id.mc_create_people_na_button };
		for (int i : buttons) {
			Button button = new Button(act);
			button = (Button) act.findViewById(i);
			button.setOnClickListener(listener);
		}
		back.setOnClickListener(backListener);
		cancel.setOnClickListener(cancelListener);
		confirm.setOnClickListener(confirmListener);
	}

	private OnClickListener confirmListener = new Button.OnClickListener() {
		public void onClick(View v) {
			JSONObject json = new JSONCall("mcaccidents", "createAcc").request(createPOST());
			if (json.has("result")) {
				String result = "error";
				try {
					result = json.getString("result");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (!result.equals("OK")) {
					Log.d("CREATE ACC ERROR", json.toString());
				}
			}
			exit();
			MCAccidents.refresh();
		}
	};

	private OnClickListener backListener = new Button.OnClickListener() {
		public void onClick(View v) {
			backButton();
		}
	};

	private OnClickListener cancelListener = new Button.OnClickListener() {
		public void onClick(View v) {
			exit();
		}
	};

	private static void exit() {
		Startup.prefs.edit().putString("backButton", previsionBack).commit();
		Show.show(R.id.main_frame_applications);
		Keyboard.hide(details);
	}

	public static void backButton() {
		confirm.setEnabled(false);
		switch (CURRENT) {
		case TYPE:
			exit();
			break;
		case FINAL:
			if (isAcc) {
				med = "mc_m_na";
				medText = "";
				confirm.setEnabled(true);
				show(PEOPLE);
			} else {
				show(TYPE);
				globalText = "";
			}
			break;
		case ACC:
			show(TYPE);
			isAcc = false;
			globalText = "";
			break;
		case PEOPLE:
			show(ACC);
			medText = "";
			globalText = "ДТП";
			med = "mc_m_na";
			break;
		}
		writeGlobal();
	}

	private OnClickListener listener = new Button.OnClickListener() {
		public void onClick(View v) {
			back.setEnabled(true);
			confirm.setEnabled(true);
			med = "mc_m_na";
			switch (v.getId()) {
			case R.id.mc_create_type_acc_button:
				show(ACC);
				isAcc = true;
				confirm.setEnabled(false);
				globalText = "ДТП";
				break;
			case R.id.mc_create_type_break_button:
				globalText = "Поломка";
				type = "acc_b";
				show(FINAL);
				break;
			case R.id.mc_create_type_steal_button:
				globalText = "Угон";
				type = "acc_s";
				show(FINAL);
				break;
			case R.id.mc_create_type_other_button:
				globalText = "Прочее";
				type = "acc_o";
				show(FINAL);
				break;
			case R.id.mc_create_acc_ma_button:
				globalText = "ДТП мот/авто";
				type = "acc_m_a";
				show(PEOPLE);
				break;
			case R.id.mc_create_acc_solo_button:
				globalText = "ДТП один участник";
				type = "acc_m";
				show(PEOPLE);
				break;
			case R.id.mc_create_acc_mm_button:
				globalText = "ДТП мот/мот";
				type = "acc_m_m";
				show(PEOPLE);
				break;
			case R.id.mc_create_acc_mp_button:
				globalText = "Наезд на пешехода";
				type = "acc_m_p";
				show(PEOPLE);
				break;
			case R.id.mc_create_people_ok_button:
				medText = "Без травм.";
				med = "mc_m_wo";
				show(FINAL);
				break;
			case R.id.mc_create_people_light_button:
				medText = "Ушибы.";
				med = "mc_m_l";
				show(FINAL);
				break;
			case R.id.mc_create_people_hard_button:
				medText = "Тяжелый.";
				med = "mc_m_h";
				show(FINAL);
				break;
			case R.id.mc_create_people_death_button:
				medText = "Летальный.";
				med = "mc_m_d";
				show(FINAL);
				break;
			case R.id.mc_create_people_na_button:
				medText = "";
				med = "mc_m_na";
				show(FINAL);
				break;
			}
			writeGlobal();
		}
	};

	private static void show(int page) {
		CURRENT = page;
		Show.show(includeArea.getId(), page);
		if (page == FINAL) {
			details.requestFocus();
			Keyboard.show(details);
		} else {
			Keyboard.hide(details);
		}
	}

	private static Map<String, String> createPOST() {
		Map<String, String> POST = new HashMap<String, String>();
		POST.put("owner_id", String.valueOf(MCAccidents.auth.id));
		POST.put("type", type);
		POST.put("med", med);
		POST.put("status", "acc_status_act");
		POST.put("lat", String.valueOf(location.getLatitude()));
		POST.put("lon", String.valueOf(location.getLongitude()));
		POST.put("created", Const.dateFormat.format(date));
		POST.put("address", addressText);
		POST.put("descr", Text.get(details.getId()) + "");
		POST.put("login", MCAccidents.auth.getLogin());
		POST.put("passhash", MCAccidents.auth.makePassHash());
		POST.put("calledMethod", "createAcc");
		return POST;
	}
}
