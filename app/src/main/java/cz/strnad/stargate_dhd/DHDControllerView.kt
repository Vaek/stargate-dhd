package cz.strnad.stargate_dhd

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.children
import cz.strnad.stargate_dhd.databinding.ControllerLayoutBinding

class DHDControllerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var _binding: ControllerLayoutBinding =
        ControllerLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var selectedPlanets = EARTH to ABYDOS
        set(value) {
            field = value
            _binding.addressHint.text = requiredAddress
        }
    private val requiredAddress
        get() = selectedPlanets.let { (from, to) ->
            to.address + from.homeSymbol
        }
    var isAddressValid: (String) -> Boolean = { address ->
        address.startsWith(requiredAddress)
    }

    var dialListener: (String) -> Unit = {}

    val isOpen get() = address.contains(GateDHDView.dialChar)

    private var address
        get() = _binding.address.text.toString()
        set(value) {
            _binding.address.text = value
            dialListener(value)
        }

    private val gateOpen by lazy {
        MediaPlayer.create(context, R.raw.gate_open)
    }
    private val gateClose by lazy {
        MediaPlayer.create(context, R.raw.gate_close)
    }
    private val dialFail by lazy {
        MediaPlayer.create(context, R.raw.dial_fail)
    }
    private val wormholeLoop by lazy {
        MediaPlayer.create(context, R.raw.wormhole_loop)
    }
    private val press
        get() = MediaPlayer.create(
            context,
            listOf(
                R.raw.press_1,
                R.raw.press_2,
                R.raw.press_3,
                R.raw.press_4,
                R.raw.press_5,
                R.raw.press_6,
                R.raw.press_7
            ).random()
        )

    init {
        _binding.address.setOnLongClickListener {
            address = ""
            true
        }
        context.obtainStyledAttributes(attrs, R.styleable.DHDControllerView).use {
            inflate(
                context,
                it.getResourceId(R.styleable.DHDControllerView_dhdLayout, 0),
                _binding.layout
            )
        }

        val children = _binding.layout.children.toMutableList()
        while (children.isNotEmpty()) {
            val child = children.removeAt(0)
            if (child is Button) {
                setupDHDButton(child)
            } else if (child is ViewGroup) {
                children.addAll(child.children)
            }
        }
        _binding.fromPlanet.apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, planets)
            setSelection(planets.indexOf(selectedPlanets.first))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedPlanets = planets[position] to selectedPlanets.second
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }
        _binding.toPlanet.apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, planets)
            setSelection(planets.indexOf(selectedPlanets.second))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedPlanets = selectedPlanets.first to planets[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }
    }

    private fun setupDHDButton(button: Button) {
        button.setOnClickListener {
            if (button.text.contains(GateDHDView.dialChar)) {
                when {
                    isOpen -> {
                        wormholeLoop.stop()
                        wormholeLoop.prepare()
                        gateClose.start()
                        address = ""
                    }
                    isAddressValid(address) -> {
                        gateOpen.start()
                        wormholeLoop.start()
                        wormholeLoop.isLooping = true
                        address += button.text
                    }
                    else -> {
                        dialFail.start()
                        address = ""
                    }
                }
            } else {
                press.start()
                address += button.text
            }
        }
    }

}
