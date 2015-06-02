package motocitizen.app.general.user;

import android.content.Context;

import java.util.Arrays;

import motocitizen.app.general.AccidentsGeneral;
import motocitizen.main.R;

public class Role {
    private static final String[] ReadOnly = new String[]{"readonly", "banned", "standart", "moderator", "admin", "developer"};
    private static final String[] Standart = new String[]{"standart", "moderator", "admin", "developer"};
    private static final String[] Moderator = new String[]{"moderator", "admin", "developer"};
    private static final String[] Admin = new String[]{"admin", "developer"};
    private static final String[] Developer = new String[]{"developer"};

    private static String getRole() {
        String role = AccidentsGeneral.auth.getRole();
        if (role == null) {
            return "";
        }
        return role;
    }

    public static boolean isRO() {
        return Arrays.asList(ReadOnly).contains(getRole());
    }

    public static boolean isStandart() {
        return Arrays.asList(Standart).contains(getRole());
    }

    public static boolean isModerator() {
        return Arrays.asList(Moderator).contains(getRole());
    }

    public static boolean isAdmin() {
        return Arrays.asList(Admin).contains(getRole());
    }

    public static boolean isDeveloper() {
        return Arrays.asList(Developer).contains(getRole());
    }

    public static String getName(Context context) {
        //Порядок важен.
        if(isAdmin())
            return context.getString(R.string.role_admin);
        else if(isModerator())
            return context.getString(R.string.role_moderator);
        else if(isStandart())
            return context.getString(R.string.role_user);
        else if(isDeveloper())
            return context.getString(R.string.role_developer);
        else
            return context.getString(R.string.role_read_only);
    }
}
