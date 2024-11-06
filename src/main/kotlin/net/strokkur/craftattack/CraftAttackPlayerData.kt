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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.util.*

class CraftAttackPlayerData {

    private var statuses: MutableMap<UUID, Data> = HashMap()
    private val file: File = CraftAttackPlugin.plugin().dataFolder.resolve("player_data.json")

    companion object {

        private var instance: CraftAttackPlayerData? = null;

        fun get(): CraftAttackPlayerData {
            if (instance == null) {
                instance = CraftAttackPlayerData()
            }

            return instance!!
        }
    }

    init {
        load()
    }

    fun save() {
        if (!file.exists()) {
            file.createNewFile()
        }

        Files.writeString(file.toPath(), GsonBuilder().setPrettyPrinting().create().toJson(statuses))
    }

    fun load() {
        if (file.exists()) {
            val json = Files.readString(file.toPath())
            CraftAttackPlugin.plugin().logger.info("Read the following json: $json")
            statuses = Gson().fromJson(json, object: TypeToken<MutableMap<UUID, Data>>() {})
        }
        else {
            statuses = HashMap()
        }
    }

    fun getStatus(uuid: UUID): Component? {
        return statuses[uuid]?.cStatus()
    }

    fun setStatus(uuid: UUID, component: String?) {
        if (statuses[uuid] == null) {
            statuses[uuid] = Data(component)
            return;
        }

        statuses[uuid]?.status = component
        save()
    }

    fun getDeaths(uuid: UUID): Int {
        return statuses[uuid]?.deaths ?: 0
    }

    fun addDeath(uuid: UUID) {
        if (statuses[uuid] == null) {
            statuses[uuid] = Data(null, 1)
            return;
        }

        statuses[uuid]?.deaths = statuses[uuid]?.deaths!! + 1
        save()
    }

    fun setPlayerListName(p: Player) {
        p.playerListName(getPlayerName(p))
    }

    fun getPlayerName(p: Player): Component {

        val deaths = getDeaths(p.uniqueId)

        if (statuses.containsKey(p.uniqueId) && statuses[p.uniqueId]?.status != null) {
            return CraftAttackConfig.get().getWithStatusName(
                statuses[p.uniqueId]!!.cStatus()!!,
                p.name,
                deaths
            )
        }

        return CraftAttackConfig.get().getNoStatusName(p.name, deaths)
    }

    private class Data(var status: String? = null, var deaths: Int = 0) {
        fun cStatus(): Component? {
            if (status == null) {
                return null
            }

            try {
                val out = CraftAttackConfig.get().miniMessage.deserialize(status!!)
                return out
            }
            catch (exception: Exception) {
                // Somehow this can lead to an error, so let's not do that
                return null
            }
        }
    }
}