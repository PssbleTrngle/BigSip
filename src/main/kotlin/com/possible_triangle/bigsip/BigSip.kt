package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import com.possible_triangle.bigsip.command.AlcoholCommand
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.item.ItemModelsProperties
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
@Mod(BigSip.MOD_ID)
object BigSip {

    const val MOD_ID: String = "bigsip"
    val LOGGER = LogManager.getLogger()!!

    init {
        Content.register()

        MOD_BUS.addListener { _: FMLCommonSetupEvent ->
            AlcoholHelper.init()
        }

        MOD_BUS.addListener { _: FMLClientSetupEvent ->

            Content.ITEMS.getEntries().mapNotNull { it.get() }.forEach {
                ItemModelsProperties.register(it, ResourceLocation(MOD_ID, "level")) { stack, _, _ ->
                    stack.damageValue.toFloat()
                }
            }

            RenderTypeLookup.setRenderLayer(Content.GRAPE_CROP, RenderType.cutout())

        }

    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        AlcoholCommand.register(event.dispatcher)
    }

}