package com.possible_triangle.bigsip.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.potion.Effect
import net.minecraft.potion.EffectType
import java.awt.Color

class DrunkEffect : Effect(EffectType.NEUTRAL, Color(0x8143BF).rgb) {

    override fun applyEffectTick(target: LivingEntity, amplifier: Int) {

    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean {
        return true
    }
}