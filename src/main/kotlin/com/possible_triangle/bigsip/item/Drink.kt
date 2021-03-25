package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.Content
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.UseAction
import net.minecraft.potion.EffectInstance
import net.minecraft.util.ActionResult
import net.minecraft.util.DrinkHelper
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import toughasnails.api.potion.TANEffects
import toughasnails.api.thirst.ThirstHelper

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
open class Drink(
    private val thirst: Int,
    private val hydration: Float,
    private val poisonChance: Float = 0.0F,
    private val canAlwaysDrink: Boolean = false,
    uses: Int = 1,
) : Item(Properties().tab(Content.TAB).durability(uses)) {

    @SubscribeEvent
    fun onItemUseFinish(event: Finish) {
        if (!ThirstHelper.isThirstEnabled() || event.entityLiving !is PlayerEntity || event.entity.level.isClientSide()) return

        val player = event.entityLiving as PlayerEntity
        val thirst = ThirstHelper.getThirst(player)
        thirst.addThirst(this.thirst)
        thirst.addHydration(this.hydration)
        if (player.level.random.nextFloat() < this.poisonChance) {
            player.addEffect(EffectInstance(TANEffects.THIRST, 600))
        }
    }

    override fun getUseAnimation(p_77661_1_: ItemStack): UseAction {
        return UseAction.DRINK
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        return if (ThirstHelper.canDrink(player, canAlwaysDrink))
            DrinkHelper.useDrink(world, player, hand)
        else
            ActionResult.pass(player.getItemInHand(hand))
    }
}