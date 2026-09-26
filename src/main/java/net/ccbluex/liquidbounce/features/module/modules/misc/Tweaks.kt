/*
 * NullBounce Client, Based on LibreBounce (Bro this looks so bad ;-;)
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

        // You can no longer need to use NoClickDelay mods when using NullBounce!
        if (noClickDelay) {
            mc.leftClickCounter = 0
        }

        // I didn't know this was an existing thing, might be useful?
        if (noBlockingDelay) {
            mc.playerController.blockHitDelay = 0
        }

        // I did not know about this one too...
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
