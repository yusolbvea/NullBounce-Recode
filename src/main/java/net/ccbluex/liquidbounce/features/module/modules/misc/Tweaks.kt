/*
 * Good module
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.base.Category
import net.ccbluex.liquidbounce.features.module.base.Module
import net.ccbluex.liquidbounce.utils.movement.MovementUtils.updateControls

object Tweaks : Module("Tweaks", Category.MISC) {

    private val noClickDelay by boolean("NoClickDelay", true)
    private val noBlockingDelay by boolean("NoBlockHitDelay", false)
    private val exitGuiDelay by boolean("NoExitGuiDelay", true)

    private var prevGui = false

    val onMotion = handler<MotionEvent> {
        val player = mc.thePlayer
        if (player == null) return@handler

        // Remove left click cooldown
        if (noClickDelay) {
            mc.leftClickCounter = 0
        }

        // Remove block hit delay (specifically for 1.8.9 Sword mechanics)
        if (noBlockingDelay) {
            mc.playerController.blockHitDelay = 0
        }

        // Handle GUI exit delay simulation
        if (mc.currentScreen == null && exitGuiDelay) {
            if (prevGui) {
                updateControls()
            }
            prevGui = false
        } else {
            prevGui = true
        }
    }
}
