/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.base.Category
import net.ccbluex.liquidbounce.features.module.base.Module
import net.ccbluex.liquidbounce.utils.client.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.rotation
import net.ccbluex.liquidbounce.utils.movement.MovementUtils.serverOnGround
import net.ccbluex.liquidbounce.utils.rotation.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

object FastBow : Module("FastBow", Category.COMBAT) {

    private val mode by choices("Mode", arrayOf("Vanilla", "Verus", "Vulcan"), "Vanilla")
    private val packets by int("Packets", 20, 3..20)
    private val delay by int("PacketDelay", 0, 0..300, suffix = "ms")
    private val noAir by boolean("NotWhileInAir", false)

    private val msTimer = MSTimer()

    val onUpdate = handler<UpdateEvent> {
        mc.thePlayer?.run {
            if (!isUsingItem)
                return@handler

            val currentItem = inventory.getCurrentItem()

            if (currentItem != null && currentItem.item is ItemBow) {
                if (noAir && !serverOnGround)
                    return@handler

                if (!msTimer.hasTimePassed(delay))
                    return@handler

                sendPacket(
                    C08PacketPlayerBlockPlacement(
                        BlockPos.ORIGIN,
                        255,
                        currentEquippedItem,
                        0F,
                        0F,
                        0F
                    )
                )

                val (yaw, pitch) = currentRotation ?: rotation

                when (mode) {
                    "Vanilla" -> {
                        repeat(packets) {
                            sendPacket(C05PacketPlayerLook(yaw, pitch, serverOnGround))
                        }
                    }

                    "Verus" -> {
                        repeat(packets) {
                            sendPacket(
                                C04PacketPlayerPosition(
                                    posX,
                                    posY,
                                    posZ,
                                    serverOnGround
                                )
                            )
                        }
                    }

                    "Vulcan" -> {
                        repeat(packets) {
                            sendPacket(
                                C06PacketPlayerPosLook(
                                    posX,
                                    posY,
                                    posZ,
                                    yaw,
                                    pitch,
                                    serverOnGround
                                )
                            )
                        }
                    }
                }

                sendPacket(C07PacketPlayerDigging(RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                itemInUseCount = currentItem.maxItemUseDuration - 1

                msTimer.reset()
            }
        }
    }
}
