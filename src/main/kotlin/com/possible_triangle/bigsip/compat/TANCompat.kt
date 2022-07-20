package com.possible_triangle.bigsip.compat

import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.item.Drink
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player
import toughasnails.api.potion.TANEffects
import toughasnails.api.thirst.ThirstHelper

object TANCompat {

    fun Drink.handleThirst(entity: Player) {
        val thirst = ThirstHelper.getThirst(entity)
        thirst.addThirst(this.thirst)
        thirst.addHydration(this.hydration)
        if (entity.level.random.nextFloat() < this.poisonChance) {
            entity.addEffect(MobEffectInstance(TANEffects.THIRST, 600))
        }
    }

    fun Drink.canDrink(player: Player): Boolean {
        return ThirstHelper.canDrink(player, canAlwaysDrink || Configs.SERVER.CAN_ALWAYS_DRINK_ALCOHOL.get())
    }

}