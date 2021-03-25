package com.possible_triangle.bigsip.alcohol

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.potion.EffectInstance
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
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
object AlcoholHelper : ICapabilityProvider, INBTSerializable<CompoundNBT> {

    @CapabilityInject(AlcoholLevel::class)
    lateinit var ALCOHOL_LEVEL: Capability<AlcoholLevel>

    fun init() {
        CapabilityManager.INSTANCE.register(AlcoholLevel::class.java, AlcoholLevel) { AlcoholLevel() }
    }

    fun applyAlcohol(entity: LivingEntity, percentage: Int) {
        if (percentage > 0) with(entity) {

            val level = activeEffectsMap[Content.DRUNK]?.amplifier?.plus(1) ?: 0
            val multiplier = level.times(0.2F) + 1F

            modifyLevel(entity) {
                it.current += round(percentage * 20 * multiplier).toInt()
                it.persistent += percentage * multiplier

                val applyLevel = it.current / 300
                val resistance = min(12 * 20, it.persistent.div(6000).toInt())

                if (applyLevel > level) addEffect(EffectInstance(Content.DRUNK, 20 * 15 - resistance, applyLevel - 1))
            }

        }
    }

    fun modifyLevel(entity: LivingEntity, func: (AlcoholLevel) -> Unit) {
        entity.getCapability(ALCOHOL_LEVEL).ifPresent { func(it) }
    }

    @SubscribeEvent
    fun onAttachCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is PlayerEntity) {
            event.addCapability(ResourceLocation(BigSip.MOD_ID, "alcohol_level"), AlcoholHelper)
        }
    }

    @SubscribeEvent
    fun playerSlept(event: PlayerSleepInBedEvent) {
        modifyLevel(event.player) {
            it.current = max(0, it.current - 20 * 300)
        }
    }

    @SubscribeEvent
    fun livingTick(event: TickEvent.PlayerTickEvent) {
        modifyLevel(event.player) {
            if (it.current > 0) it.current -= 1
        }
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return if (cap === ALCOHOL_LEVEL) LazyOptional.of { this.ALCOHOL_LEVEL }.cast() else LazyOptional.empty()
    }

    override fun serializeNBT(): CompoundNBT {
        return ALCOHOL_LEVEL.storage.writeNBT(ALCOHOL_LEVEL, AlcoholLevel(), null) as CompoundNBT
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        return ALCOHOL_LEVEL.storage.readNBT(ALCOHOL_LEVEL, AlcoholLevel(), null, nbt)
    }
}