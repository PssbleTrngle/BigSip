package com.possible_triangle.bigsip.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.util.text.TranslationTextComponent

object AlcoholCommand {

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
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

    private fun getCurrent(ctx: CommandContext<CommandSource>): Int {
        val player = EntityArgument.getPlayer(ctx, "player")
        val value = player.getCapability(AlcoholHelper.ALCOHOL_LEVEL).map { it.current }.orElse(0)
        ctx.source.sendSuccess(TranslationTextComponent("command.bigsip.current", player.name, value), false)
        return value
    }

    private fun getPersistent(ctx: CommandContext<CommandSource>): Int {
        val player = EntityArgument.getPlayer(ctx, "player")
        val value = player.getCapability(AlcoholHelper.ALCOHOL_LEVEL).map { it.persistent }.orElse(0F)
        ctx.source.sendSuccess(TranslationTextComponent("command.bigsip.persistent", player.name, value), false)
        return value.toInt()
    }

}