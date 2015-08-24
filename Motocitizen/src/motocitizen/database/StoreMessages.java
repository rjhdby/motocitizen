package motocitizen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by U_60A9 on 24.08.2015.
 */
public class StoreMessages {
    private static DbOpenHelper dbOpenHelper;

    public StoreMessages(Context context) {
        dbOpenHelper = new DbOpenHelper(context);
    }

    public static int getLast(Context context, int accidentId) {
        int            result;
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db     = dbOpenHelper.getReadableDatabase();
        Cursor         cursor = db.rawQuery("SELECT msg_id FROM messages WHERE acc_id=?", new String[]{String.valueOf(accidentId)});
        if (cursor.getCount() == 0) result = 0;
        else {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    public static void setLast(Context context, int accidentId, int messageId) {
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db            = dbOpenHelper.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("msg_id", messageId);
        int affected = db.update("messages", contentValues, "acc_id=?", new String[]{String.valueOf(accidentId)});
        if (affected == 0) {
            contentValues.put("acc_id", accidentId);
            db.insert("messages", null, contentValues);
        }
        db.close();
    }
}
