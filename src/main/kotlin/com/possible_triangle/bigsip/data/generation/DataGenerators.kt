package com.possible_triangle.bigsip.data.generation

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object DataGenerators {

    @SubscribeEvent
    fun registerGenerators(event: GatherDataEvent) {
        if(event.includeClient()) {
            event.generator.addProvider(ItemModels(event.generator, event.existingFileHelper))
        }
    }

}