package com.possible_triangle.bigsip.alcohol

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object AlcoholHelper {

    val ALCOHOL_LEVEL = CapabilityManager.get(object : CapabilityToken<IAlcoholLevel>() {})!!

    fun applyAlcohol(entity: LivingEntity, percentage: Int) {
        if (percentage > 0) with(entity) {

            val level = activeEffectsMap[Content.DIZZYNESS]?.amplifier?.plus(1) ?: 0
            val multiplier = 1F - level.times(0.2F)

            modifyLevel(entity) {
                current += round(percentage * 600 * multiplier).toInt()
                persistent += percentage * multiplier

                val applyLevel = current / 9000
                val resistance = min(12 * 20, persistent.div(6000).toInt())

                if (applyLevel > level) addEffect(MobEffectInstance(Content.DIZZYNESS,
                    20 * 15 - resistance,
                    applyLevel - 1))
            }

        }
    }

    private fun modifyLevel(entity: LivingEntity, func: IAlcoholLevel.() -> Unit) {
        entity.getCapability(ALCOHOL_LEVEL).ifPresent { func(it) }
    }

    @SubscribeEvent
    fun onAttachCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is Player) {
            val alcoholLevel = EntityAlcoholLevel()
            event.addCapability(
                ResourceLocation(BigSip.MOD_ID, "alcohol_level"),
                alcoholLevel
            )
        }
    }

    @SubscribeEvent
    fun playerSlept(event: PlayerSleepInBedEvent) {
        modifyLevel(event.player) {
            current = max(0, current - 600 * 300)
        }
    }

    @SubscribeEvent
    fun livingTick(event: TickEvent.PlayerTickEvent) {
        modifyLevel(event.player) {
            if (current > 0) current -= 1
        }
    }

}