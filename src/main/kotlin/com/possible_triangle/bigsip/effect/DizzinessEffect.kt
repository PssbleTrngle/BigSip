package com.possible_triangle.bigsip.effect

import com.possible_triangle.bigsip.modules.AlcoholModule
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.MovementInputUpdateEvent
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import java.awt.Color
import kotlin.math.cos

class DizzinessEffect : MobEffect(MobEffectCategory.HARMFUL, Color(0x8143BF).rgb) {

    init {
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ATTRIBUTE_ID, -0.1, Operation.MULTIPLY_TOTAL)
        addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), ATTRIBUTE_ID, 0.025, Operation.ADDITION)
        addAttributeModifier(Attributes.ATTACK_SPEED, ATTRIBUTE_ID, -0.05, Operation.MULTIPLY_TOTAL)
    }

    override fun applyEffectTick(target: LivingEntity, amplifier: Int) {
        // TODO actual effect
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean {
        return true
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
    companion object {

        private const val ATTRIBUTE_ID = "6c841002-41c3-4733-ae90-56c814e5e2e7"

        @SubscribeEvent
        fun tickPlayer(event: MovementInputUpdateEvent) {
            if (!event.player.hasEffect(AlcoholModule.DIZZYNESS)) return
            if (!event.input.hasForwardImpulse()) return

            val perTicks = if (event.player.isSprinting) 7F else 15F
            val seconds = event.player.level.gameTime / perTicks
            val factor = cos(seconds * Math.PI).toFloat()
            val maxOffset = if(event.player.isShiftKeyDown) 0.3F else 0.6F
            val offset = event.player.random.nextFloat() * maxOffset

            event.input.leftImpulse += offset * factor
        }
    }

}