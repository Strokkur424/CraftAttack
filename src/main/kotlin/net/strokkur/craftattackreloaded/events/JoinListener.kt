package net.strokkur.craftattackreloaded.events

import net.strokkur.craftattackreloaded.CraftAttackPlayerData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener: Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        CraftAttackPlayerData.get().setPlayerListName(e.player)
    }


}