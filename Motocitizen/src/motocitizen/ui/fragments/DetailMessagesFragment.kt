package motocitizen.ui.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.ScrollView
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.content.message.Message
import motocitizen.datasources.database.StoreMessages
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.SendMessageRequest
import motocitizen.main.R
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.ui.activity.AccidentDetailsActivity.Companion.ACCIDENT_ID_KEY
import motocitizen.ui.menus.MessageContextMenu
import motocitizen.ui.rows.message.MessageRowFactory
import motocitizen.user.User
import motocitizen.utils.hide
import motocitizen.utils.show
import motocitizen.utils.showToast
import org.json.JSONException

class DetailMessagesFragment() : Fragment() {
    private lateinit var rootView: View
    private lateinit var scrollView: ScrollView
    private lateinit var messagesView: ViewGroup
    private lateinit var formView: View
    private lateinit var sendButton: ImageButton
    private lateinit var inputField: EditText
    private lateinit var accident: Accident

    constructor(accident: Accident) : this() {
        this.accident = accident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_detail_messages, container, false)
        bindViews()
        setupNewMessageForm()

        update()//todo exterminatus

        return rootView
    }

    private fun bindViews() {
        scrollView = rootView.findViewById(R.id.activity__details_messages_scroll)
        messagesView = rootView.findViewById(R.id.details_messages_table)
        formView = rootView.findViewById(R.id.new_message_area)
        sendButton = rootView.findViewById(R.id.new_message_send)
        inputField = rootView.findViewById(R.id.new_message_text)
    }

    private fun setupNewMessageForm() {
        if (User.isReadOnly()) {
            formView.hide()
            return
        }
        formView.show()

        sendButton.setOnClickListener {
            val text = inputField.text.toString().replace("\\s".toRegex(), "")
            if (text.isNotEmpty()) {
                SendMessageRequest(inputField.text.toString(), accident.id, ::sendMessageCallback)
                inputField.setText("")
            }
        }
    }

    /**
     * Обновление сообщений в списке
     */
    private fun update() {
        messagesView.removeAllViews()

        val messages = Content.messagesForAccident(accident)
        if (messages.isEmpty()) return

        var last = 0
        val group = ArrayList<Message>()
        messages.forEach {
            if (last != it.owner && last != 0) {
                addMessageRows(group)
                group.clear()
            }
            group.add(it)
            last = it.owner
        }
        addMessageRows(group)

        updateUnreadMessages()
    }

    //todo pizdets
    private fun addMessageRows(list: List<Message>) {
        if (list.size == 1) {
            drawRow(MessageRowFactory.makeOne(activity, list.first()), list.first())
        } else {
            drawRow(MessageRowFactory.makeFirst(activity, list.first()), list.first())
            (1..list.size - 2).forEach { drawRow(MessageRowFactory.makeMiddle(activity, list[it]), list[it]) }
            drawRow(MessageRowFactory.makeLast(activity, list.last()), list.last())
        }
    }

    private fun drawRow(view: View, message: Message) {
        view.setOnLongClickListener(MessageRowLongClickListener(message))
        messagesView.addView(view)
    }

    private fun updateUnreadMessages() {
        if (Content.messagesForAccident(accident).isEmpty()) return
        StoreMessages.setLast(accident.id, accident.messagesCount)
    }

    private fun sendMessageCallback(response: ApiResponse) {
        try {
            if (response.hasError()) {
                val text = response.error.text
                activity.runOnUiThread { activity.showToast(text) }
            } else {
                accident.messagesCount++
            }
        } catch (e: JSONException) {
            activity.showToast("Неизвестная ошибка" + response.toString())
            e.printStackTrace()
        }

        Content.requestDetailsForAccident(accident, {
            activity.runOnUiThread {
                (activity as AccidentDetailsActivity).update()
                update()
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }
        })
    }

    private inner class MessageRowLongClickListener internal constructor(private val message: Message) : View.OnLongClickListener {

        override fun onLongClick(view: View): Boolean {
            val popupWindow: PopupWindow = MessageContextMenu(activity, message)
            val viewLocation = IntArray(2)
            view.getLocationOnScreen(viewLocation)
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1])
            return true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ACCIDENT_ID_KEY, accident.id)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) return
        accident = Content[savedInstanceState.getInt(ACCIDENT_ID_KEY)]!!
    }
}
