package net.strokkur.craftattackreloaded

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.strokkur.craftattackreloaded.events.ChatListener
import net.strokkur.craftattackreloaded.events.DeathListener
import net.strokkur.craftattackreloaded.events.JoinListener
import net.strokkur.craftattackreloaded.events.MoveListener
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class CraftAttackPlugin: JavaPlugin() {

    companion object {
        private var plugin: CraftAttackPlugin? = null

        fun plugin(): CraftAttackPlugin {
            if (plugin == null) {
                throw RuntimeException("Tried to access plugin instance without plugin being initialised")
            }

            return plugin!!
        }
    }

    override fun onLoad() {
        plugin = this

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        this.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()

            commands.register(CraftAttackCommands.status())
            commands.register(CraftAttackCommands.craftAttack())
        }

    }

    override fun onEnable() {

        listener(DeathListener())
        listener(JoinListener())
        listener(MoveListener())
        listener(ChatListener())

    }

    override fun onDisable() {
        CraftAttackPlayerData.get().save()
    }

    private fun listener(e: Listener) {
        Bukkit.getPluginManager().registerEvents(e, this)
    }
}
