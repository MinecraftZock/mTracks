package info.mx.tracks.rule

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException

class DisableAnimationsRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                // disable animations for test run
                changeAnimationStatus(enable = false)
                try {
                    base.evaluate()
                } finally {
                    // enable after test run
                    changeAnimationStatus(enable = true)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun changeAnimationStatus(enable:Boolean = true) {
        with(UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())){
            executeShellCommand("settings put global transition_animation_scale ${if(enable) 1 else 0}")
            executeShellCommand("settings put global window_animation_scale ${if(enable) 1 else 0}")
            executeShellCommand("settings put global animator_duration_scale ${if(enable) 1 else 0}")
        }
    }
}