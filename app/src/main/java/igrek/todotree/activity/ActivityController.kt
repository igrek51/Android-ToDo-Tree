package igrek.todotree.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import dagger.Lazy
import igrek.todotree.dagger.DaggerIoc
import igrek.todotree.info.logger.LoggerFactory
import igrek.todotree.intent.PersistenceCommand
import igrek.todotree.system.WindowManagerService
import javax.inject.Inject

class ActivityController {
    @Inject
    lateinit var windowManagerService: Lazy<WindowManagerService>

    @Inject
    lateinit var activity: Lazy<Activity>

    private val logger = LoggerFactory.logger

    init {
        DaggerIoc.factoryComponent.inject(this)
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        // resize event
        val screenWidthDp = newConfig.screenWidthDp
        val screenHeightDp = newConfig.screenHeightDp
        val orientationName = getOrientationName(newConfig.orientation)
        logger.debug("Screen resized: " + screenWidthDp + "dp x " + screenHeightDp + "dp - " + orientationName)
    }

    private fun getOrientationName(orientation: Int): String {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "landscape"
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "portrait"
        }
        return orientation.toString()
    }

    fun quit() {
        windowManagerService.get().keepScreenOn(false)
        activity.get().finish()
    }

    fun onStart() {
        logger.debug("starting activity...")
    }

    fun onStop() {
        logger.debug("stopping activity...")
        PersistenceCommand().optionSave()
    }

    fun onDestroy() {
        logger.info("activity has been destroyed")
    }

    fun minimize() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.get().startActivity(startMain)
    }

}
