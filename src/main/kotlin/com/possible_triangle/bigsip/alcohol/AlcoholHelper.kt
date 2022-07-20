package com.possible_triangle.bigsip.alcohol

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
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

    val ALCOHOL_LEVEL = CapabilityManager.get(object : CapabilityToken<AlcoholLevel>() {})

    fun applyAlcohol(entity: LivingEntity, percentage: Int) {
        if (percentage > 0) with(entity) {

            val level = activeEffectsMap[Content.DIZZYNESS]?.amplifier?.plus(1) ?: 0
            val multiplier = 1F - level.times(0.2F)

            modifyLevel(entity) {
                it.current += round(percentage * 600 * multiplier).toInt()
                it.persistent += percentage * multiplier

                val applyLevel = it.current / 9000
                val resistance = min(12 * 20, it.persistent.div(6000).toInt())

                if (applyLevel > level) addEffect(MobEffectInstance(Content.DIZZYNESS,
                    20 * 15 - resistance,
                    applyLevel - 1))
            }

        }
    }

    fun modifyLevel(entity: LivingEntity, func: (AlcoholLevel) -> Unit) {
        entity.getCapability(ALCOHOL_LEVEL).ifPresent { func(it) }
    }

    @SubscribeEvent
    fun onAttachCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is Player) {
            val alcoholLevel = LazyOptional.of { AlcoholLevel() }
            event.addCapability(
                ResourceLocation(BigSip.MOD_ID, "alcohol_level"),
                object : ICapabilityProvider {
                    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
                        return if (cap == ALCOHOL_LEVEL) alcoholLevel.cast()
                        else LazyOptional.empty<T>()
                    }
                }
            )
        }
    }

    @SubscribeEvent
    fun playerSlept(event: PlayerSleepInBedEvent) {
        modifyLevel(event.player) {
            it.current = max(0, it.current - 600 * 300)
        }
    }

    @SubscribeEvent
    fun livingTick(event: TickEvent.PlayerTickEvent) {
        modifyLevel(event.player) {
            if (it.current > 0) it.current -= 1
        }
    }

}