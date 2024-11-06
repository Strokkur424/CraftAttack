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
package net.strokkur.craftattack.events

import net.strokkur.craftattack.CraftAttackConfig
import net.strokkur.craftattack.CraftAttackPlugin
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

        private var isPosSet: Boolean = false

        fun reloadBound() {
            reloadBound(CraftAttackConfig.get())
        }

        fun reloadBound(craftAttackConfig: CraftAttackConfig) {
            if (!craftAttackConfig.isPosSet()) {
                isPosSet = false
                return
            }

            bound = BoundingBox.of(
                craftAttackConfig.getFirstPos().block,
                craftAttackConfig.getSecondPos().block
            )
            isPosSet = true
        }
    }

    init {
        reloadBound()
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {

        if (!isPosSet) {
            return
        }

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