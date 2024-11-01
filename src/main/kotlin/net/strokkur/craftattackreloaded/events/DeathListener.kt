package net.strokkur.craftattackreloaded.events

import net.strokkur.craftattackreloaded.CraftAttackPlayerData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathListener : Listener {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        CraftAttackPlayerData.get().addDeath(e.player.uniqueId)
        CraftAttackPlayerData.get().setPlayerListName(e.player)
    }

}