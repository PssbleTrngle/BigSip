package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import com.possible_triangle.bigsip.compat.ModCompat
import com.possible_triangle.bigsip.compat.TANCompat.canDrink
import com.possible_triangle.bigsip.compat.TANCompat.handleThirst
import com.possible_triangle.bigsip.config.Configs
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
open class Drink(
    val getFluid: () -> Fluid,
    val thirst: Int,
    val hydration: Float,
    val poisonChance: Float = 0.0F,
    val canAlwaysDrink: Boolean = false,
    container: Item? = Items.GLASS_BOTTLE,
    val uses: Int = 1,
) : Item(createProperties(uses, container)) {

    private companion object {
        fun createProperties(uses: Int, container: Item?): Properties {
            return with(Content.Properties) {
                if (container != null) craftRemainder(container)
                if (uses > 1) durability(uses).setNoRepair()
                else stacksTo(16)
                this
            }
        }
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.DRINK
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val canDrink = ModCompat.runIfLoaded(ModCompat.Mod.TAN) {
            canDrink(player)
        } ?: true
        return if (canDrink)
            ItemUtils.startUsingInstantly(world, player, hand)
        else
            InteractionResultHolder.pass(player.getItemInHand(hand))
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 32
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return Configs.SERVER.ENCHANTABLE_DRINKS.get()
    }

    override fun finishUsingItem(stack: ItemStack, world: Level, entity: LivingEntity): ItemStack {
        if (entity !is Player) return stack

        if (entity is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(entity, stack)
        }

        ModCompat.runIfLoaded(ModCompat.Mod.TAN) {
            handleThirst(entity)
        }

        entity.awardStat(Stats.ITEM_USED[this])

        return if (!entity.isCreative) {

            val empty = if (stack.isDamageableItem) {
                BigSip.LOGGER.info("Damageable ${stack.maxDamage}")
                stack.damageValue = stack.damageValue + 1
                stack.damageValue >= stack.maxDamage
            } else {
                BigSip.LOGGER.info("Stackable")
                stack.shrink(1)
                stack.isEmpty
            }

            val newStack = if (empty) {
                if (stack.hasContainerItem()) stack.containerItem else ItemStack.EMPTY
            } else stack

            BigSip.LOGGER.info("Drunk ${stack.item.registryName}")
            BigSip.LOGGER.info("Damage: ${stack.damageValue}")
            BigSip.LOGGER.info("Count: ${stack.count}")
            BigSip.LOGGER.info("Empty: ${stack.isEmpty}")

            newStack

        } else stack
    }
}