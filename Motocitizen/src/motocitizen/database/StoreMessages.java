package motocitizen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StoreMessages {

    public static int getLast(int accidentId) {
        int            result;
        DbOpenHelper   dbOpenHelper = new DbOpenHelper();
        SQLiteDatabase db           = dbOpenHelper.getReadableDatabase();
        Cursor         cursor       = db.rawQuery("SELECT msg_id FROM messages WHERE acc_id=?", new String[]{String.valueOf(accidentId)});
        if (cursor.getCount() == 0) result = 0;
        else {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    public static void setLast(int accidentId, int messageId) {
        DbOpenHelper   dbOpenHelper  = new DbOpenHelper();
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
