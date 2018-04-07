package motocitizen.dictionary

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import motocitizen.main.R

enum class Type(override val code: String, override val text: String, private val mapIcon: Int): Dictionary<String> {
    BREAK("b", "Поломка", R.drawable.break_icon),
    SOLO("m", "Один участник", R.drawable.accident),
    MOTO_MOTO("mm", "Мот/мот", R.drawable.accident),
    MOTO_AUTO("ma", "Мот/авто", R.drawable.accident),
    MOTO_MAN("mp", "Наезд на пешехода", R.drawable.accident),
    OTHER("o", "Прочее", R.drawable.other),
    STEAL("s", "Угон", R.drawable.other),
    USER("user", "Вы", R.drawable.osm_moto_icon);

    val icon: BitmapDescriptor
        get() = BitmapDescriptorFactory.fromResource(this.mapIcon)

    fun isAccident() = this in arrayOf(MOTO_AUTO, SOLO, MOTO_MOTO, MOTO_MAN)
}
