package motocitizen.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.ScrollView
import motocitizen.activity.AccidentDetailsActivity
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.content.message.Message
import motocitizen.database.StoreMessages
import motocitizen.main.R
import motocitizen.network.CoreRequest
import motocitizen.network.requests.SendMessageRequest
import motocitizen.rows.message.MessageRowFactory
import motocitizen.user.User
import motocitizen.utils.popups.MessagesPopup
import motocitizen.utils.show
import org.json.JSONException
import org.json.JSONObject
import java.util.*

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
                SendMessageRequest(inputField.text.toString(), accident.id, SendMessageCallback())
                inputField.setText("")
            }
        }
    }

    /**
     * Обновление сообщений в списке
     */
    private fun update() {
        messagesView.removeAllViews()

        if (accident.messages.isEmpty()) return

        var last = 0
        val group = ArrayList<Message>()
        accident.messages.forEach { message ->
            if (last != message.owner && last != 0) {
                addMessageRows(group)
                group.clear()
            }
            group.add(message)
            last = message.owner
        }
        addMessageRows(group)

        updateUnreadMessages(accident.id, last)
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

    private fun updateUnreadMessages(accidentId: Int, messageId: Int) {
        StoreMessages.setLast(accidentId, messageId)
    }

    private inner class SendMessageCallback : CoreRequest.RequestResultCallback {
        override fun call(response: JSONObject) {
            try {
                if (response.getJSONObject("e").has("c")) {
                    val text = response.getJSONObject("e").getString("t")
                    activity.runOnUiThread { show(activity, text) }
                }
            } catch (e: JSONException) {
                show(activity, "Неизвестная ошибка" + response.toString())
                e.printStackTrace()
            }

            Content.requestDetailsForAccident(accident, object : CoreRequest.RequestResultCallback {
                override fun call(response: JSONObject) {
                    activity.runOnUiThread {
                        (activity as AccidentDetailsActivity).update()
                        update()
                        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                    }
                }
            })
        }
    }

    private inner class MessageRowLongClickListener internal constructor(private val message: Message) : View.OnLongClickListener {

        override fun onLongClick(view: View): Boolean {
            val popupWindow: PopupWindow = MessagesPopup(activity, message.id, accident.id).getPopupWindow(activity)
            val viewLocation = IntArray(2)
            view.getLocationOnScreen(viewLocation)
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1])
            return true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident.id)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) return
        accident = Content.accidents[savedInstanceState.getInt(AccidentDetailsActivity.ACCIDENT_ID_KEY)]!!
    }
}
