package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.alcohol.IAlcoholLevel
import com.possible_triangle.bigsip.block.MaturingBarrelCT
import com.possible_triangle.bigsip.command.AlcoholCommand
import com.simibubi.create.foundation.data.CreateRegistrate
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
@Mod(BigSip.MOD_ID)
object BigSip {

    const val MOD_ID: String = "bigsip"
    val LOGGER = LogManager.getLogger()!!

    init {
        Content.register()

        MOD_BUS.addListener { _: FMLClientSetupEvent ->
            ItemBlockRenderTypes.setRenderLayer(Content.GRAPE_CROP, RenderType.cutout())
            Content.DRINKS.forEach {
                ItemProperties.register(it, ResourceLocation(MOD_ID, "level")) { stack, _, _, _ ->
                    stack.damageValue.toFloat()
                }
            }
        }

        MOD_BUS.addListener { event: RegisterCapabilitiesEvent ->
            event.register(IAlcoholLevel::class.java)
        }

    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        AlcoholCommand.register(event.dispatcher)
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    object ModEvent {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun registerCTM(event: RegistryEvent.Register<Block>) {
            val barrel = event.registry.getValue(ResourceLocation(MOD_ID, "maturing_barrel"))
            CreateRegistrate.connectedTextures<Block> { MaturingBarrelCT { it.`is`(Content.BARREL) } }.accept(barrel)
        }

    }

}