/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
import net.ccbluex.liquidbounce.utils.ClientUtils.displayChatMessage
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.world.WorldSettings
import net.minecraft.init.Items
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

object KeyPearl : Module("KeyPearl", ModuleCategory.PLAYER) {

    private val mouse by BoolValue("Mouse", false)
    private val mouseButtonValue = ListValue("MouseButton", arrayOf("Left", "Right", "Middle", "MouseButton4", "MouseButton5"), "Middle") { mouse }
    private val keyName by TextValue("KeyName", "X") { !mouse }
    private val noEnderPearlsMessage by BoolValue("NoEnderPearlsMessage", true)

    private var wasMouseDown = false
    private var wasKeyDown = false

    private fun throwEnderPearl() {
        val pearlInHotbar = InventoryUtils.findItem(36, 44, Items.ender_pearl)

        if (pearlInHotbar == null) {
            if (noEnderPearlsMessage) {
                displayChatMessage("§6§lWarning: §aThere are no ender pearls in your hotbar.")
            }
            return
        }

        sendPackets(
            C09PacketHeldItemChange(pearlInHotbar - 36),
            C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem),
            C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem)
        )
    }


    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (mc.currentScreen != null || mc.playerController.currentGameType == WorldSettings.GameType.SPECTATOR
            || mc.playerController.currentGameType == WorldSettings.GameType.CREATIVE) return
			
		val isMouseDown = Mouse.isButtonDown(mouseButtonValue.values.indexOf(mouseButtonValue.get()))
		val isKeyDown = Keyboard.isKeyDown(Keyboard.getKeyIndex(keyName.uppercase()))

        if (mouse && !wasMouseDown && isMouseDown) {
            throwEnderPearl()
        } else if (!mouse && !wasKeyDown && isKeyDown) {
            throwEnderPearl()
        }

        wasMouseDown = isMouseDown
        wasKeyDown = isKeyDown
    }
}