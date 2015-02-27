package motocitizen.utils;

import motocitizen.startup.Startup;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {
	private final static InputMethodManager imm = (InputMethodManager) Startup.context.getSystemService(Context.INPUT_METHOD_SERVICE);
	private static View last;
	
	public static void hide(View v) {
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void show(View v) {
		if(last != null){
			imm.hideSoftInputFromWindow(last.getWindowToken(), 0);
		}
		last = v;
		imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public static void hide(){
		if(last != null){
			imm.hideSoftInputFromWindow(last.getWindowToken(), 0);
		}
	}
}
