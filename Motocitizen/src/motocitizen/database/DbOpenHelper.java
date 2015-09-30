package motocitizen.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import motocitizen.MyApp;

public class DbOpenHelper extends SQLiteOpenHelper {

    /* constants */
    private static final int    VERSION  = 3;
    private static final String DATABASE = "motodtp";
    /* end constants */

    public DbOpenHelper() {
        super(MyApp.getAppContext(), DATABASE, null, VERSION);
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
