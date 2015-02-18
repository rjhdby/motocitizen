package motocitizen.utils;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class Show {
	public static int last, lastParent;
	
	public static void show(int parentId, int childId) {
		ViewGroup parent = (ViewGroup) ((Activity) Startup.context).findViewById(parentId);
		lastParent = parentId;
		for (int i = 0; i < parent.getChildCount(); i++) {
			View view = parent.getChildAt(i);
			if (view.getVisibility() == View.VISIBLE){
				last = view.getId();
			}
			if (view.getId() == childId) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public static void show(int childId) {
		int main = ((Activity) Startup.context).findViewById(R.id.main_frame).getId();
		show(main, childId);
	}
	
	public static void showLast(){
		show(lastParent, last);
	}
}
