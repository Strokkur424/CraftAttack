/*
 * This file is part of CraftAttackReloaded, licensed under the MIT License.
 *
 * Copyright (c) 2024 Strokkur24
 * Copyright (c) 2024 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.strokkur.craftattack

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.strokkur.craftattack.events.ChatListener
import net.strokkur.craftattack.events.DeathListener
import net.strokkur.craftattack.events.JoinListener
import net.strokkur.craftattack.events.MoveListener
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
            commands.register(CraftAttackCommands.craftAttack(), listOf("ca"))
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
