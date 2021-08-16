package cz.strnad.stargate_dhd

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.databinding.DataBindingUtil
import cz.strnad.stargate_dhd.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var scaleGesture: ScaleGestureDetector.SimpleOnScaleGestureListener
    private lateinit var _binding: ActivityMainBinding
    private val gateService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.4.1")
            .client(OkHttpClient.Builder().apply {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GateService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        _binding.controller.dialListener = {
            launch(Dispatchers.IO) {
                runCatching {
                    val chevronCount = it.count { it != GateDHDView.dialChar }
                    val showWave = it.contains(GateDHDView.dialChar)
                    gateService.light(
                        "1".repeat(chevronCount) +
                                "0".repeat(7 - chevronCount) +
                                if (showWave) "1" else "0"
                    )
                }.onFailure { it.printStackTrace() }
            }
        }
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
