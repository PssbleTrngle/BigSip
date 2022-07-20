package com.possible_triangle.bigsip.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import java.awt.Color

class DizzinessEffect : MobEffect(MobEffectCategory.HARMFUL, Color(0x8143BF).rgb) {

    override fun applyEffectTick(target: LivingEntity, amplifier: Int) {
        // TODO actual effect
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean {
        return true
    }
}