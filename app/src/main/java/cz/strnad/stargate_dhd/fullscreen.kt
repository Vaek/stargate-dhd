package cz.strnad.stargate_dhd

import android.app.Activity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController

/**
 * Method that will enable fullscreen with immersive mode.
 * https://developer.android.com/training/system-ui/immersive#immersive
 */
fun Activity.enableFullscreenImmersiveMode(onCanceled: () -> Unit = {}) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        window.insetsController?.run {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            addOnControllableInsetsChangedListener { _, _ ->
                onCanceled()
            }
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        @Suppress("DEPRECATION")
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                onCanceled()
            }
        }
    }
}
