package motocitizen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int    VERSION  = 1;
    private static final String DATABASE = "motodtp";

    public DbOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableMessages  = "CREATE TABLE IF NOT EXISTS messages (acc_id int, msg_id int)";
        String createTableFavorites = "CREATE TABLE IF NOT EXISTS favorites (acc_id int)";
        db.execSQL(createTableMessages);
        db.execSQL(createTableFavorites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
