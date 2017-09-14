package motocitizen.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import motocitizen.main.R

class BusinessCardActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewMain = inflater.inflate(R.layout.fragment_business_card, container, false)
        val imageViewQrForum = viewMain.findViewById(R.id.imageViewQrForum) as ImageView
        imageViewQrForum.setImageResource(R.drawable.qr_forum)
        return viewMain
    }
}
