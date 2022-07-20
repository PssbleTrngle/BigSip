package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level

class Alcohol(
    thirst: Int,
    hydration: Float,
    private val percentage: Int,
    canAlwaysDrink: Boolean = false,
    container: Item? = Items.GLASS_BOTTLE,
    uses: Int = 1,
) : Drink(thirst, hydration, percentage / 10F, canAlwaysDrink, container, uses) {

    override fun finishUsingItem(stack: ItemStack, world: Level, entity: LivingEntity): ItemStack {
        AlcoholHelper.applyAlcohol(entity, percentage)
        return super.finishUsingItem(stack, world, entity)
    }

}