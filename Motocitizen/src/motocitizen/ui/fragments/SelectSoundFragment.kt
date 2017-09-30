package motocitizen.ui.fragments

import android.app.Fragment
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.ui.rows.sound.SoundRow

class SelectSoundFragment : Fragment() {
    private val ROOT_LAYOUT = R.layout.select_sound_fragment
    private val ROOT_VIEW = R.id.select_sound_fragment
    private val CONTENT_VIEW = R.id.sound_select_table
    private val SAVE_BUTTON = R.id.select_sound_save_button
    private val CANCEL_BUTTON = R.id.select_sound_cancel_button

    private var notifications: SparseArray<Sound> = SparseArray()
    private lateinit var ringtoneList: ViewGroup
    private var currentId = 0
    private var currentUri = Preferences.sound
    private var currentTitle = Preferences.soundTitle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.setContentView(ROOT_LAYOUT)
    }

    override fun onResume() {
        super.onResume()
        activity.findViewById(ROOT_VIEW).visibility = View.VISIBLE
        if (notifications.size() == 0) getSystemSounds()

        ringtoneList = activity.findViewById(CONTENT_VIEW) as ViewGroup
        val selectSoundConfirmButton = activity.findViewById(SAVE_BUTTON) as Button
        val selectSoundCancelButton = activity.findViewById(CANCEL_BUTTON) as Button

        selectSoundConfirmButton.setOnClickListener { _ ->
            if (currentTitle == "default system") {
                Preferences.setDefaultSoundAlarm()
            } else
                Preferences.setSound(currentTitle, currentUri!!)
            Preferences.initSound(activity)
            finish()
        }
        selectSoundCancelButton.setOnClickListener { finish() }

        drawList()
    }

    private fun getSystemSounds() {
        val rm = RingtoneManager(activity)
        rm.setType(RingtoneManager.TYPE_NOTIFICATION)
        notifications = SparseArray()
        val cursor = rm.cursor
        if (cursor.count == 0 && !cursor.moveToFirst()) return
        while (!cursor.isAfterLast && cursor.moveToNext()) {
            val currentPosition = cursor.position
            notifications.put(currentPosition, Sound(rm.getRingtoneUri(currentPosition), rm.getRingtone(currentPosition)))
        }
    }

    private fun drawList() {
        (0 until notifications.size()).forEach { i -> inflateRow(ringtoneList, notifications.keyAt(i)) }
    }

    private fun inflateRow(viewGroup: ViewGroup, currentPosition: Int) {
        val tr = SoundRow(viewGroup.context, notifications.get(currentPosition).title)

        tr.tag = currentPosition
        tr.setOnClickListener { view ->
            val tag = view.tag as Int
            if (currentId != 0) {

                ringtoneList.findViewWithTag(currentId).setBackgroundColor(android.R.attr.colorBackground)
            }
            currentId = tag
            view.setBackgroundColor(Color.GRAY)
            notifications.get(tag).play()
            currentUri = notifications.get(tag).uri
            currentTitle = notifications.get(tag).title
        }

        viewGroup.addView(tr)
    }

    private fun finish() {
        activity.findViewById(R.id.select_sound_fragment).visibility = View.GONE
        fragmentManager.beginTransaction().remove(this).replace(android.R.id.content, SettingsFragment()).commit()
    }

    private inner class Sound internal constructor(internal val uri: Uri, private val ringtone: Ringtone) {

        val title: String
            get() = ringtone.getTitle(activity)

        fun play() {
            ringtone.play()
        }
    }
}
