package com.possible_triangle.bigsip.data.generation

import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object DataGenerators {

    @SubscribeEvent
    fun registerGenerators(event: GatherDataEvent) {
        if (event.includeClient()) {
            event.generator.addProvider(BlockModels(event.generator, event.existingFileHelper))
            event.generator.addProvider(ItemModels(event.generator, event.existingFileHelper))
        }

        if (event.includeServer()) {
            RecipeBuilder.register(event.generator)
        }
    }

}