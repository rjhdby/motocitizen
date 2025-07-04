package motocitizen.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.accident.AccidentBuilder
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.CreateAccidentRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.ui.activity.CreateAccActivity.Frames.*
import motocitizen.ui.dialogs.create.EmptyAddressDialog
import motocitizen.ui.frames.FrameInterface
import motocitizen.ui.frames.create.*
import motocitizen.utils.bindView
import motocitizen.utils.dateTimeString
import motocitizen.utils.showToast

class CreateAccActivity : FragmentActivity() {
    companion object {
        private val ROOT_LAYOUT = R.layout.create_point
    }

    private val typeFrame: FrameInterface by lazy { TypeFrame(this, this::selectTypeCallback) }
    private val subTypeFrame: FrameInterface by lazy { SubTypeFrame(this, this::selectSubTypeCallback) }
    private val damageFrame: FrameInterface by lazy { DamageFrame(this, this::selectDamageCallback) }
    private val locationFrame: FrameInterface by lazy { LocationFrame(this, this::selectLocationCallback) }
    private val descriptionFrame: FrameInterface by lazy { DescriptionFrame(this, builder, this::selectDescriptionCallback) }

    private val whatField: TextView by bindView(R.id.create_what)
    private val whoField: TextView by bindView(R.id.create_who)
    private val whereField: TextView by bindView(R.id.create_where)
    private val whenField: TextView by bindView(R.id.create_when)
    private val forStat: CheckBox by bindView(R.id.forStat)

    private val backButton: Button by bindView(R.id.BACK)

    private val builder = AccidentBuilder()
    private var current = MAP

    private val prevFrame: Frames
        get() = when (current) {
            MAP         -> MAP
            TYPE        -> MAP
            SUB_TYPE    -> TYPE
            DAMAGE      -> SUB_TYPE
            DESCRIPTION -> if (builder.build().isAccident()) DAMAGE else TYPE
        }

    internal enum class Frames {
        MAP,
        TYPE,
        SUB_TYPE,
        DAMAGE,
        DESCRIPTION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ROOT_LAYOUT)

        changeFrameTo(MAP)

        locationFrame.show() //NOTE: do not use changeFrameTo() !!!

        setupListeners()
        refreshDescription()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.CANCEL).setOnClickListener { finish() }
        backButton.setOnClickListener { backButtonPressed() }
    }

    override fun onKeyUp(keycode: Int, e: KeyEvent): Boolean {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            backButtonPressed()
            return true
        }
        return super.onKeyUp(keycode, e)
    }

    private fun backButtonPressed() {
        if (current == MAP) finish()
        changeFrameTo(prevFrame)
        refreshDescription()
    }

    private fun changeFrameTo(frame: Frames) {
        hideCurrentFrame()
        current = frame
        getFrame(current).show()
        backButton.isEnabled = current != MAP
        refreshDescription()
    }

    private fun refreshDescription() {
        val medicine = if (builder.medicine === Medicine.UNKNOWN) "" else ". ${builder.medicine.text}"

        whatField.text = buildString {
            append(builder.type.text)
            append(medicine)
        }
        whoField.text = Preferences.login
        whereField.text = builder.location.address
        whenField.text = builder.time.dateTimeString()
    }

    private fun hideCurrentFrame() = getFrame(current).hide()

    private fun getFrame(frame: Frames): FrameInterface = when (frame) {
        MAP         -> locationFrame
        TYPE        -> typeFrame
        SUB_TYPE    -> subTypeFrame
        DAMAGE      -> damageFrame
        DESCRIPTION -> descriptionFrame
    }

    private fun selectDescriptionCallback() {
        //        disableConfirm(); //todo
        CreateAccidentRequest(builder.build(),
                              this::createAccidentCallback,
                              forStat.isChecked).call() //todo remove flag argument
    }

    private fun selectLocationCallback(latLng: LatLng) {
        builder.location = AccidentLocation(MyLocationManager.getAddress(latLng), latLng)
        changeFrameTo(TYPE)
        EmptyAddressDialog(this, builder.location.address, this::addressDialogCallback)
    }

    private fun selectTypeCallback(type: Type) {
        builder.type = type
        changeFrameTo(if (type.isAccident()) SUB_TYPE else DESCRIPTION)
    }

    private fun selectSubTypeCallback(type: Type) {
        builder.type = type
        changeFrameTo(DAMAGE)
    }

    private fun selectDamageCallback(medicine: Medicine) {
        builder.medicine = medicine
        changeFrameTo(DESCRIPTION)
    }

    private fun addressDialogCallback(address: String) {
        if (address.isEmpty()) return
        builder.location = AccidentLocation(address, builder.location.coordinates)
        refreshDescription()
    }

    private fun createAccidentCallback(response: ApiResponse) {
        if (response.resultObject.has("id")) {
            finish()
        } else {
            runOnUiThread {
                showToast(makeErrorMessage(response.error.text))
                //enableConfirm();//todo
            }
        }
    }

    private fun makeErrorMessage(source: String): String = when (source) {
        "AUTH ERROR"            -> "Вы не авторизованы"
        "NO RIGHTS", "READONLY" -> "Недостаточно прав"
        "PROBABLY SPAM"         -> "Нельзя создавать события так часто"
        else                    -> source
    }
}
