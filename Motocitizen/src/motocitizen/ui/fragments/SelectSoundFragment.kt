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
import android.widget.TableLayout
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.ui.rows.sound.SoundRow
import motocitizen.utils.bindView
import motocitizen.utils.gone
import motocitizen.utils.show

//todo pizdets
class SelectSoundFragment : Fragment() {

    private var notifications: SparseArray<Sound> = SparseArray()
    private val rootView: View by bindView(R.id.select_sound_fragment)
    private val ringtoneList: TableLayout by bindView(R.id.sound_select_table)
    private val selectSoundConfirmButton: Button by bindView(R.id.select_sound_save_button)
    private val selectSoundCancelButton: Button by bindView(R.id.select_sound_cancel_button)
    private var currentId = 0
    private var currentUri = Preferences.sound
    private var currentTitle = Preferences.soundTitle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.setContentView(R.layout.select_sound_fragment)
    }

    override fun onResume() {
        super.onResume()
        rootView.show()
        if (notifications.size() == 0) getSystemSounds()

        setUpListeners()
        drawList()
    }

    private fun setUpListeners() {
        selectSoundConfirmButton.setOnClickListener {
            if (currentTitle == "default system") {
                Preferences.setDefaultSoundAlarm()
            } else
                Preferences.setSound(currentTitle, currentUri)
            Preferences.initSound(activity)
            finish()
        }
        selectSoundCancelButton.setOnClickListener { finish() }
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

    private fun drawList() = (0 until notifications.size()).forEach { inflateRow(ringtoneList, notifications.keyAt(it)) }

    private fun inflateRow(viewGroup: ViewGroup, currentPosition: Int) {
        val tr = SoundRow(viewGroup.context, notifications.get(currentPosition).title)

        tr.tag = currentPosition
        tr.setOnClickListener {
            val tag = it.tag as Int
            if (currentId != 0) {
                ringtoneList.findViewWithTag<View>(currentId).setBackgroundColor(android.R.attr.colorBackground)
            }
            currentId = tag
            it.setBackgroundColor(Color.GRAY)
            notifications.get(tag).play()
            currentUri = notifications.get(tag).uri
            currentTitle = notifications.get(tag).title
        }

        viewGroup.addView(tr)
    }

    private fun finish() {
        rootView.gone()

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
