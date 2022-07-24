package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.alcohol.IAlcoholLevel
import com.possible_triangle.bigsip.block.MaturingBarrelCT
import com.possible_triangle.bigsip.command.AlcoholCommand
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.modules.*
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
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
@Mod(BigSip.MOD_ID)
object BigSip {

    const val MOD_ID: String = "bigsip"
    val LOGGER = LogManager.getLogger()!!

    init {
        Registration.register(GrapesModule, JuiceModule, AlcoholModule, MaturingModule)

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.SERVER_SPEC)

        MOD_BUS.addListener { _: FMLClientSetupEvent ->
            ItemBlockRenderTypes.setRenderLayer(GrapesModule.GRAPE_CROP, RenderType.cutout())
            Registration.DRINKS.forEach {
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
            CreateRegistrate.connectedTextures<Block> { MaturingBarrelCT { it.`is`(MaturingModule.BARREL) } }.accept(barrel)
        }

        @SubscribeEvent
        fun registerConditions(event: FMLLoadCompleteEvent) {
            Module.registerAll()
        }

    }

}