package motocitizen.ui.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import motocitizen.ui.activity.AccidentDetailsActivity.ACCIDENT_ID_KEY
import motocitizen.ui.popups.MessagesPopup
import motocitizen.ui.rows.message.MessageRowFactory
import motocitizen.user.User
import motocitizen.utils.show
import org.json.JSONException

class DetailMessagesFragment() : Fragment() {
    private val ROOT_LAYOUT = R.layout.fragment_detail_messages
    private val MESSAGES_VIEW = R.id.details_messages_table
    private val FORM_VIEW = R.id.new_message_area
    private val INPUT_FIELD = R.id.new_message_text
    private val SEND_BUTTON = R.id.new_message_send
    private val SCROLL_VIEW = R.id.activity__details_messages_scroll

    private lateinit var rootView: View
    private lateinit var scrollView: ScrollView
    private lateinit var messagesView: ViewGroup
    private lateinit var accident: Accident

    constructor(accident: Accident) : this() {
        this.accident = accident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(ROOT_LAYOUT, container, false)
        messagesView = rootView.findViewById(MESSAGES_VIEW) as ViewGroup
        scrollView = rootView.findViewById(SCROLL_VIEW) as ScrollView

        setupNewMessageForm()

        update()//todo exterminatus

        return rootView
    }

    private fun setupNewMessageForm() {
        val formView = rootView.findViewById(FORM_VIEW)

        if (!User.isStandard) {
            formView.visibility = View.INVISIBLE
            return
        }
        formView.visibility = View.VISIBLE

        val inputField = rootView.findViewById(INPUT_FIELD) as EditText

        rootView.findViewById(SEND_BUTTON).setOnClickListener { _ ->
            val text = inputField.text.toString().replace("\\s".toRegex(), "")
            if (text.isNotEmpty()) {
                SendMessageRequest(inputField.text.toString(), accident.id, { result -> sendMessageCallback(result) })
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
        messages.forEach { message ->
            if (last != message.owner && last != 0) {
                addMessageRows(group)
                group.clear()
            }
            group.add(message)
            last = message.owner
        }
        addMessageRows(group)

        updateUnreadMessages()
    }

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
        StoreMessages.setLast(accident.id, accident.messagesCount())
    }

    private fun sendMessageCallback(response: ApiResponse) {
        try {
            if (response.hasError()) {
                val text = response.error.text
                activity.runOnUiThread { show(activity, text) }
            }
        } catch (e: JSONException) {
            show(activity, "Неизвестная ошибка" + response.toString())
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
            val popupWindow: PopupWindow = MessagesPopup(activity, message.id)
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
        accident = Content.accident(savedInstanceState.getInt(ACCIDENT_ID_KEY))
    }
}
