package cz.strnad.stargate_dhd

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.databinding.DataBindingUtil
import cz.strnad.stargate_dhd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var scaleGesture: ScaleGestureDetector.SimpleOnScaleGestureListener
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //scaleGesture = ScaleGesture(_binding.dhd)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fullcreen -> {
                supportActionBar?.hide()
                enableFullscreenImmersiveMode {
                    supportActionBar?.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun ScaleGesture(view: View) = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        init {
            val detector = ScaleGestureDetector(view.context, this)
            view.doOnAttach { view.setOnTouchListener { _, event -> detector.onTouchEvent(event) } }
            view.doOnDetach { view.setOnTouchListener(null) }
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            view.scaleX *= detector.scaleFactor
            view.scaleY *= detector.scaleFactor
            view.invalidate()
            return true
        }
    }
}
