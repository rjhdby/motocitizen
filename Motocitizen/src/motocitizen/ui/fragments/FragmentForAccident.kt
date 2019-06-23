package motocitizen.ui.fragments

import android.app.Fragment
import motocitizen.content.accident.Accident

abstract class FragmentForAccident : Fragment() {
    abstract fun setAccident(accident: Accident)
}