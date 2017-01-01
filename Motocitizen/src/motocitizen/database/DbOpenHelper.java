package motocitizen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    /* constants */
    private static final int    VERSION  = 3;
    private static final String DATABASE = "motodtp";
    /* end constants */

    private static class Holder {
        private static DbOpenHelper instance;
    }

    public static DbOpenHelper getInstance() {
        return Holder.instance;
    }

    public static void init(Context context) {
        Holder.instance = new DbOpenHelper(context);
    }

    private DbOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSchema(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createSchema(db);
    }

    private void createSchema(SQLiteDatabase db) {
        String createTableMessages  = "CREATE TABLE IF NOT EXISTS messages (acc_id int, msg_id int)";
        String createTableFavorites = "CREATE TABLE IF NOT EXISTS favorites (acc_id int)";
        db.execSQL(createTableMessages);
        db.execSQL(createTableFavorites);
    }
}
