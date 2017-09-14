package motocitizen.datasources.database

import android.content.ContentValues

object StoreMessages {

    fun getLast(accidentId: Int): Int {
        val cursor = Database.db.readableDatabase.rawQuery("SELECT msg_id FROM messages WHERE acc_id=?", arrayOf(accidentId.toString()))
        cursor.moveToFirst()
        val result = if (cursor.count == 0) 0 else cursor.getInt(0)
        cursor.close()
        return result
    }

    fun setLast(accidentId: Int, messageId: Int) {
        val contentValues = ContentValues()
        contentValues.put("msg_id", messageId)
        val affected = Database.db.readableDatabase.update("messages", contentValues, "acc_id=?", arrayOf(accidentId.toString()))
        if (affected == 0) {
            contentValues.put("acc_id", accidentId)
            Database.db.readableDatabase.insert("messages", null, contentValues)
        }
    }
}
