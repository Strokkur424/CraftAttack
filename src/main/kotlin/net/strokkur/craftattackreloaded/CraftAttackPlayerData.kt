package net.strokkur.craftattackreloaded

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.util.*

class CraftAttackPlayerData {

    private val statuses: MutableMap<UUID, Data>
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

        if (file.exists()) {
            val json = Files.readString(file.toPath())
            CraftAttackPlugin.plugin().logger.info("Read the following json: $json")
            statuses = Gson().fromJson(json, object: TypeToken<MutableMap<UUID, Data>>() {})
        }
        else {
            statuses = HashMap()
        }
    }

    fun save() {

        if (!file.exists()) {
            file.createNewFile()
        }

        Files.writeString(file.toPath(), Gson().toJson(statuses))
    }

    fun getStatus(uuid: UUID): Component? {
        return statuses[uuid]?.cStatus()
    }

    fun setStatus(uuid: UUID, component: Component?) {
        val deserialized: String? = if (component == null) null else miniMessage().serialize(component)

        if (statuses[uuid] == null) {
            statuses[uuid] = Data(deserialized)
            return;
        }

        statuses[uuid]?.status = deserialized
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
                return null;
            }

            return miniMessage().deserialize(status!!);
        }
    }
}