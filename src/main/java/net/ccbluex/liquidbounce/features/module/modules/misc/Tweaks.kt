/*
 * NullBounce Client, Based on LibreBounce (Bro this looks so bad ;-;)
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.base.Category
import net.ccbluex.liquidbounce.features.module.base.Module
import net.minecraft.client.settings.GameSettings

object Tweaks : Module("Tweaks", Category.MISC) {

    private val noClickDelay by boolean("NoClickDelay", true)
    private val noBlockingDelay by boolean("NoBlockHitDelay", false)
    private val saveMoveKeys by boolean("SaveMoveKeys", true)

    private var prevGui = false

    val onMotion = handler<MotionEvent> {
        if (mc.thePlayer == null) return@handler

        // You can no longer need to use NoClickDelay mods when using NullBounce!
        if (noClickDelay) {
            mc.leftClickCounter = 0
        }

        // I didn't know this was an existing thing, might be useful?
        if (noBlockingDelay) {
            mc.playerController.blockHitDelay = 0
        }

        // This will allow you to walk again if you previously pressed movement keys and opened a container.
        if (mc.currentScreen == null && saveMoveKeys) {
            if (prevGui) {
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
                mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
                mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
                mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint)
            }
            prevGui = false
        } else {
            prevGui = true
        }
    }
}
