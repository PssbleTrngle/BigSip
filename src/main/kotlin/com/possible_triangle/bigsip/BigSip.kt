package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.alcohol.IAlcoholLevel
import com.possible_triangle.bigsip.command.AlcoholCommand
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.conditions.ConfigRecipeCondition
import com.possible_triangle.bigsip.modules.*
import com.possible_triangle.bigsip.network.Networking
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.registries.ObjectHolderRegistry
import org.apache.logging.log4j.LogManager

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
@Mod(BigSip.MOD_ID)
object BigSip {

    const val MOD_ID: String = "bigsip"
    val LOGGER = LogManager.getLogger()!!

    init {
        Registration.register(GrapesModule, JuiceModule, AlcoholModule, MaturingModule, FluidCompatModule, StructureModule)
        Networking.register()

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.SERVER_SPEC)

        ObjectHolderRegistry.addHandler {
            ModModule.forEach { it.registerPonders() }
        }

    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        AlcoholCommand.register(event.dispatcher)
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    object ModEvent {

        @SubscribeEvent
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            event.register(IAlcoholLevel::class.java)
        }

        @SubscribeEvent
        fun registerContentConditions(event: FMLLoadCompleteEvent) {
            ModModule.addConditions()
        }

        @SubscribeEvent
        fun setup(event: FMLCommonSetupEvent) {
            StructureModule.registerVillageHouses()
        }

        @SubscribeEvent
        fun clientSetup(event: FMLClientSetupEvent) {
            ItemBlockRenderTypes.setRenderLayer(GrapesModule.GRAPE_CROP, RenderType.cutout())
            Registration.DRINKS.forEach {
                ItemProperties.register(it, ResourceLocation(MOD_ID, "level")) { stack, _, _, _ ->
                    stack.damageValue.toFloat()
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        fun registerRecipeConditions(event: RegistryEvent<RecipeSerializer<*>>) {
            CraftingHelper.register(ConfigRecipeCondition.Serializer)
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun registerCTM(event: RegistryEvent<Block>) {
            ModModule.forEach { it.registerCTM() }
        }

    }

}