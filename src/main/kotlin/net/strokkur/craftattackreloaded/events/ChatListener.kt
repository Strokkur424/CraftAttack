package net.strokkur.craftattackreloaded.events

import io.papermc.paper.event.player.AsyncChatEvent
import net.strokkur.craftattackreloaded.CraftAttackConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener {

    @EventHandler
    fun onChat(e: AsyncChatEvent) {

        e.renderer { source, _, message, _ ->
            return@renderer CraftAttackConfig.get().getChatFormat(
                source, message
            );
        }

    }

}