package com.possible_triangle.bigsip.alcohol

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.modules.AlcoholModule
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
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

data class AlcoholEffect(val at: Float, val effect: () -> MobEffect, val chance: Float = 1F)

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object AlcoholHelper {

    private const val ALCOHOL_LEVEL_PER_PERCENT = 0.04F

    val ALCOHOL_LEVEL = CapabilityManager.get(object : CapabilityToken<IAlcoholLevel>() {})!!

    private val EFFECTS = arrayListOf<AlcoholEffect>()
    fun addEffect(effect: AlcoholEffect) {
        EFFECTS.add(effect)
    }

    init {
        addEffect(AlcoholEffect(0.6F, { AlcoholModule.DIZZYNESS }))
        addEffect(AlcoholEffect(1.0F, { MobEffects.CONFUSION }))
    }

    fun applyAlcohol(entity: LivingEntity, percentage: Int) {
        if (!Configs.SERVER.ENABLE_ALCOHOL.get()) return

        if (percentage > 0) with(entity) {

            modifyLevel(entity) {
                current += percentage * ALCOHOL_LEVEL_PER_PERCENT
                persistent += percentage

                val resistance = min(12 * 20, persistent.div(6000).toInt())

                val effects = EFFECTS.filter {
                    it.at <= current && it.chance >= entity.random.nextFloat()
                }

                effects.forEach {
                    val effect = MobEffectInstance(it.effect(), 20 * 15 - resistance, 0)
                    addEffect(effect)
                }
            }

        }
    }

    private fun modifyLevel(entity: LivingEntity, func: IAlcoholLevel.() -> Unit) {
        if (!Configs.SERVER.ENABLE_ALCOHOL.get()) return
        entity.getCapability(ALCOHOL_LEVEL).ifPresent { func(it) }

    }

    @SubscribeEvent
    fun onAttachCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is Player) {
            val alcoholLevel = EntityAlcoholLevel()
            event.addCapability(
                ResourceLocation(BigSip.MOD_ID, "alcohol_level"), alcoholLevel
            )
        }
    }

    @SubscribeEvent
    fun playerSlept(event: PlayerSleepInBedEvent) {
        modifyLevel(event.player) {
            current = max(0F, current - 8F)
        }
    }

    @SubscribeEvent
    fun livingTick(event: TickEvent.PlayerTickEvent) {
        if (event.player.level.gameTime % 120 != 0L) return
        modifyLevel(event.player) {
            current = max(0F, current - 0.01F)
        }
    }

}