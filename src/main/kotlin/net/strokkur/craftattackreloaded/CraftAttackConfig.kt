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
package net.strokkur.craftattackreloaded

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.strokkur.craftattackreloaded.events.MoveListener
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException

class CraftAttackConfig {

    private val plugin: CraftAttackPlugin = CraftAttackPlugin.plugin()
    private val file: File = plugin.dataFolder.resolve("general.yml")
    private var cfg: YamlConfiguration

    companion object {
        private var instance: CraftAttackConfig? = null

        fun get(): CraftAttackConfig {
            if (instance == null) {
                instance = CraftAttackConfig()
            }

            return instance!!
        }
    }

    init {
        if (!file.exists()) {
            plugin.saveResource("general.yml", true)
        }

        cfg = YamlConfiguration.loadConfiguration(file)
    }

    private fun save() {
        try {
            cfg.save(file)
        }
        catch (e: IOException) {
            CraftAttackPlugin.plugin().logger.severe("Could not save general.yml: ${e.message}")
        }
    }

    fun reload() {
        cfg = YamlConfiguration.loadConfiguration(file)

        MoveListener.reloadBound()
    }

    fun setFirstPos(player: Player) {
        cfg.set("elytra-position.spawn-world", player.location.world.name)
        cfg.set("elytra-position.pos-1.x", player.location.x)
        cfg.set("elytra-position.pos-1.y", player.location.y)
        cfg.set("elytra-position.pos-1.z", player.location.z)

        save()
        MoveListener.reloadBound()
    }

    fun setSecondPos(player: Player) {
        cfg.set("elytra-position.spawn-world", player.location.world.name)
        cfg.set("elytra-position.pos-2.x", player.location.x)
        cfg.set("elytra-position.pos-2.y", player.location.y)
        cfg.set("elytra-position.pos-2.z", player.location.z)

        save()
        MoveListener.reloadBound()
    }

    fun getFirstPos(): Location {
        return Location(
            Bukkit.getWorld(cfg.getString("elytra-position.spawn-world", "world")!!),
            cfg.getDouble("elytra-position.pos-1.x", 0.0),
            cfg.getDouble("elytra-position.pos-1.y", 0.0),
            cfg.getDouble("elytra-position.pos-1.z", 0.0)
        )
    }

    fun getSecondPos(): Location {
        return Location(
            Bukkit.getWorld(cfg.getString("elytra-position.spawn-world", "world")!!),
            cfg.getDouble("elytra-position.pos-2.x", 0.0),
            cfg.getDouble("elytra-position.pos-2.y", 0.0),
            cfg.getDouble("elytra-position.pos-2.z", 0.0)
        )
    }

    fun getElytraName(): Component {
        return miniMessage().deserialize(
            cfg.getString("elytra-name", "Spawn Elytra")!!
        )
    }

    fun getNoStatusName(playername: String, deaths: Int): Component {
        return miniMessage().deserialize(
            cfg.getString("name-format.no-status", "<playername> <yellow><deaths>")!!,
            Placeholder.unparsed("playername", playername),
            Placeholder.unparsed("deaths", deaths.toString())
        )
    }

    fun getWithStatusName(status: Component, playername: String, deaths: Int): Component {
        return miniMessage().deserialize(
            cfg.getString("name-format.with-status", "<dark_gray>[<white><status></white>]</dark_gray> <playername> <yellow><deaths>")!!,
            Placeholder.component("status", status),
            Placeholder.unparsed("playername", playername),
            Placeholder.unparsed("deaths", deaths.toString())
        )
    }

    fun getChatFormat(player: Player, message: Component): Component {
        val status = CraftAttackPlayerData.get().getStatus(player.uniqueId)

        if (status == null) {
            return miniMessage().deserialize(
                cfg.getString("chat-format.no-status", "<playername>: <message>")!!,
                Placeholder.unparsed("playername", player.name),
                Placeholder.component("message", message)
            )
        }

        return miniMessage().deserialize(
            cfg.getString("chat-format.with-status", "<playername>: <message>")!!,
            Placeholder.unparsed("playername", player.name),
            Placeholder.component("status", status),
            Placeholder.component("message", message)
        )
    }
}