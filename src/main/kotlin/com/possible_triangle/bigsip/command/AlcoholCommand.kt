package com.possible_triangle.bigsip.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.TranslatableComponent
import kotlin.math.floor

object AlcoholCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("alcohol")
                .then(literal("get").then(
                    argument("player", EntityArgument.player())
                        .then(literal("current").executes { getCurrent(it) })
                        .then(literal("persistent").executes { getPersistent(it) })
                        .executes { getCurrent(it) }
                ))
        )
    }

    private fun getCurrent(ctx: CommandContext<CommandSourceStack>): Int {
        val player = EntityArgument.getPlayer(ctx, "player")
        val value = player.getCapability(AlcoholHelper.ALCOHOL_LEVEL).map { it.current }.orElse(0F)
        ctx.source.sendSuccess(TranslatableComponent("command.bigsip.current", player.name, value), false)
        return floor(value).toInt()
    }

    private fun getPersistent(ctx: CommandContext<CommandSourceStack>): Int {
        val player = EntityArgument.getPlayer(ctx, "player")
        val value = player.getCapability(AlcoholHelper.ALCOHOL_LEVEL).map { it.persistent }.orElse(0F)
        ctx.source.sendSuccess(TranslatableComponent("command.bigsip.persistent", player.name, value), false)
        return floor(value).toInt()
    }

}