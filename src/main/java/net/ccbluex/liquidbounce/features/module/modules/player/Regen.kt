/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.base.Category
import net.ccbluex.liquidbounce.features.module.base.Module
import net.ccbluex.liquidbounce.utils.client.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.isMoving
import net.ccbluex.liquidbounce.utils.movement.MovementUtils.serverOnGround
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.potion.Potion

object Regen : Module("Regen", Category.PLAYER) {

    private val mode by choices("Mode", arrayOf("Vanilla", "Spartan", "Verus", "OldGrim"), "Vanilla")
    private val speed by int("PacketSpeed", 100, 1..150) { mode == "Vanilla" || "Verus" || "OldGrim" }

    private val delay by int("PacketDelay", 0, 0..10000, suffix = "ms")
    private val healthToRegen by int("Health", 20, 0..20)

    private val noAir by boolean("NotWhileInAir", false)
    private val potionEffect by boolean("OnRegenerationPot", false)

    private val timer = MSTimer()

    private var resetTimer = false

    val onUpdate = handler<UpdateEvent> {
        if (resetTimer) mc.timer.timerSpeed = 1F
        else resetTimer = false

        val player = mc.thePlayer ?: return@handler

        if (
            !mc.playerController.gameIsSurvivalOrAdventure()
            || noAir && !serverOnGround
            || !player.isEntityAlive
            || player.health >= healthToRegen
            || (potionEffect && !player.isPotionActive(Potion.regeneration))
            || !timer.hasTimePassed(delay)
        ) return@handler

        // tuff?

        when (mode) {
            "Vanilla" -> {
                repeat(speed) {
                    sendPacket(C03PacketPlayer(serverOnGround))
                }
            }

            "Spartan" -> {
                if (!player.isMoving && serverOnGround) {
                    repeat(9) {
                        sendPacket(C03PacketPlayer(serverOnGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }
            }

            "Verus" -> {
                repeat(speed) {
                    sendPacket(
                        C04PacketPlayerPosition(
                            player.posX,
                            player.posY,
                            player.posZ,
                            serverOnGround
                        )
                    )
                }
            }

            "OldGrim" -> {
                repeat(speed) {
                    sendPacket(
                        C06PacketPlayerPosLook(
                            player.posX,
                            player.posY,
                            player.posZ,
                            player.rotationYaw,
                            player.rotationPitch,
                            serverOnGround
                        )
                    )
                }
            }
        }

        timer.reset()
    }

    override val tag
        get() = mode
}
