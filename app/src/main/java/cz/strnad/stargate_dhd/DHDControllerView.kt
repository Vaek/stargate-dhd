package cz.strnad.stargate_dhd

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.children
import cz.strnad.stargate_dhd.databinding.ControllerLayoutBinding

class DHDControllerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var _binding: ControllerLayoutBinding =
        ControllerLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var isAddressValid: (String) -> Boolean = {
        it.length >= 6
    }

    private var address
        get() = _binding.address.text.toString()
        set(value) {
            _binding.address.text = value
        }

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
    }

    private fun setupDHDButton(button: Button) {
        button.setOnClickListener {
            if (button.text.isNullOrBlank()) {
                if (isAddressValid(address)) {
                    Toast.makeText(context, "Fire", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                    address = ""
                }
            } else {
                _binding.address.append(button.text)
            }
        }
    }

}
