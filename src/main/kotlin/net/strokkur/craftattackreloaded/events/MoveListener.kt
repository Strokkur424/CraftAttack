package net.strokkur.craftattackreloaded.events

import net.strokkur.craftattackreloaded.CraftAttackConfig
import net.strokkur.craftattackreloaded.CraftAttackPlugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.BoundingBox

class MoveListener: Listener {

    private val elytraKey: NamespacedKey = NamespacedKey(CraftAttackPlugin.plugin(), "flying-elytra")

    companion object {

        private var bound: BoundingBox = BoundingBox()
        private val inZone: MutableList<Player> = mutableListOf()

        fun reloadBound() {
            bound = BoundingBox.of(
                CraftAttackConfig.get().getFirstPos().block,
                CraftAttackConfig.get().getSecondPos().block
            )
        }
    }

    init {
        reloadBound()
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {

        val p = e.player

        if (bound.contains(p.location.toVector())) {
            giveElytra(p)
            return
        }

        if (bound.contains(p.location.toVector())
            || p.isGliding
            || p.inventory.chestplate == null
            || !p.inventory.chestplate!!.persistentDataContainer.has(elytraKey)
        ) {
            return
        }

        inZone.remove(p)
        p.inventory.chestplate = null
    }

    private fun giveElytra(p: Player) {

        if (inZone.contains(p) || p.inventory.chestplate != null) {
            return
        }

        val elytra = ItemStack(Material.ELYTRA)
        elytra.editMeta {
            it.addEnchant(Enchantment.BINDING_CURSE, 1, true)
            it.displayName(CraftAttackConfig.get().getElytraName())
            it.persistentDataContainer[elytraKey, PersistentDataType.BOOLEAN] = true
        }

        p.inventory.chestplate = elytra
        inZone.add(p)
    }

}