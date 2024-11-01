package net.strokkur.craftattackreloaded

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player

object CraftAttackCommands {

    fun craftAttack(): LiteralCommandNode<CommandSourceStack> {

        return Commands.literal("craftattack")
            .requires { it.sender.hasPermission("craftattack.admin") }
            .then(Commands.literal("setfirstpos").executes { ctx ->

                if (ctx.source.sender !is Player) {
                    ctx.source.sender.sendRichMessage("<red><bold>[!]</bold> You can't do that!")
                    return@executes Command.SINGLE_SUCCESS
                }

                val p: Player = ctx.source.sender as Player
                CraftAttackConfig.get().setFirstPos(p)
                ctx.source.sender.sendRichMessage(
                    "<green><bold>[!]</bold> Successfully set the first pos to <aqua><x> <y> <z>",
                    Placeholder.unparsed("x", p.location.blockX().toString()),
                    Placeholder.unparsed("y", p.location.blockY().toString()),
                    Placeholder.unparsed("z", p.location.blockZ().toString()),
                )

                return@executes Command.SINGLE_SUCCESS
            })
            .then(Commands.literal("setsecondpos").executes { ctx ->

                if (ctx.source.sender !is Player) {
                    ctx.source.sender.sendRichMessage("<red><bold>[!]</bold> You can't do that!")
                    return@executes Command.SINGLE_SUCCESS
                }

                val p: Player = ctx.source.sender as Player
                CraftAttackConfig.get().setSecondPos(p)
                ctx.source.sender.sendRichMessage(
                    "<green><bold>[!]</bold> Successfully set the second pos to <aqua><x> <y> <z>",
                    Placeholder.unparsed("x", p.location.blockX().toString()),
                    Placeholder.unparsed("y", p.location.blockY().toString()),
                    Placeholder.unparsed("z", p.location.blockZ().toString()),
                )

                return@executes Command.SINGLE_SUCCESS
            })
            .then(Commands.literal("reload").executes { ctx ->

                CraftAttackConfig.get().reload()
                ctx.source.sender.sendRichMessage("<green><bold>[!]</bold> Successfully reloaded the config!")

                return@executes Command.SINGLE_SUCCESS

            })
            .build()
    }

    fun status(): LiteralCommandNode<CommandSourceStack> {

        return Commands.literal("status")
            .then(Commands.argument("format", StringArgumentType.greedyString())
                .executes { ctx ->
                    if (ctx.source.sender !is Player) {
                        ctx.source.sender.sendRichMessage("<red><bold>[!]</bold> You can't do that!")
                        return@executes Command.SINGLE_SUCCESS
                    }

                    val player = ctx.source.sender as Player
                    val format = miniMessage().deserialize(ctx.getArgument("format", String::class.java))

                    CraftAttackPlayerData.get().setStatus(player.uniqueId, format)

                    player.sendMessage(
                        miniMessage().deserialize(
                            "<bold>[!]</bold> Your status is now set to <status>!",
                            Placeholder.component("status", format)
                        )
                    )

                    CraftAttackPlayerData.get().setPlayerListName(player)
                    return@executes Command.SINGLE_SUCCESS
                }
            )
            .executes { ctx ->
                ctx.source.sender.sendRichMessage("<green><bold>[!]</bold> Successfully removed your status!")
                return@executes Command.SINGLE_SUCCESS
            }

            .build()
    }


}