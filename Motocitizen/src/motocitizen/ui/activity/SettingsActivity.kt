package motocitizen.ui.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import motocitizen.main.R
import motocitizen.ui.fragments.SettingsFragment

class SettingsActivity : Activity() {
    private val ROOT_LAYOUT = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ROOT_LAYOUT)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = false

    class PlaceholderFragment : Fragment() {
        private val SETTINGS_FRAGMENT = R.layout.fragment_settings

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(SETTINGS_FRAGMENT, container, false)
        }
    }
}
