package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class Alcohol(
    thirst: Int,
    hydration: Float,
    private val percentage: Int,
    canAlwaysDrink: Boolean = false,
    uses: Int = 1,
) : Drink(thirst, hydration, percentage / 10F, canAlwaysDrink, uses) {

    override fun finishUsingItem(stack: ItemStack, world: World, entity: LivingEntity): ItemStack {
        AlcoholHelper.applyAlcohol(entity, percentage)
        return super.finishUsingItem(stack, world, entity)
    }

}