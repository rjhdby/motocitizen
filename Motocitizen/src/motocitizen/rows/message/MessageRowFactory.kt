package motocitizen.rows.message

import android.content.Context
import motocitizen.content.message.Message

object MessageRowFactory {
    private fun make(context: Context, message: Message, type: MessageRow.Companion.Type): MessageRow =
            if (message.isOwner) OwnMessageRow(context, message, type) else CommonMessageRow(context, message, type)

    fun makeFirst(context: Context, message: Message): MessageRow = make(context, message, MessageRow.Companion.Type.FIRST)
    fun makeMiddle(context: Context, message: Message): MessageRow = make(context, message, MessageRow.Companion.Type.MIDDLE)
    fun makeLast(context: Context, message: Message): MessageRow = make(context, message, MessageRow.Companion.Type.LAST)

    fun makeOne(context: Context, message: Message): MessageRow = make(context, message, MessageRow.Companion.Type.ONE)
}