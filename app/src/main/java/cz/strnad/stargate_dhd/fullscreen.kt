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
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
            
            var prevHidden = false
            addOnControllableInsetsChangedListener { controller, typeMask ->
                val visible = window.decorView.rootWindowInsets.isVisible(WindowInsets.Type.statusBars() and WindowInsets.Type.navigationBars())
                

                if (visible && prevHidden) {
                    onCanceled()
                } else {
                    prevHidden = true
                }
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
