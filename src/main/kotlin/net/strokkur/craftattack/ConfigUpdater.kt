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

import org.bukkit.configuration.file.YamlConfiguration

class ConfigUpdater(private val config: YamlConfiguration) {

    companion object {
        fun update(config: YamlConfiguration) {
            ConfigUpdater(config).updateConfig()
        }
    }

    private fun updateConfig() {

        val version: Int = config.getInt("version", 0)

        if (version < 1) {
            comment("elytra-name",
                "The name of the elytra which you get at the start platform.",
                "Set the platform with /ca setfirstpos and /ca setsecondpos."
            )

            comment("name-format", "Name formats for the tab-list and chat")
            comment("name-format.no-status", "Used if the player hadn't set their status yet")
            comment("name-format.with-status", "Used if the player has set their status")

            comment("chat-format", "The format for chat")

            set("version", 1, true)
            comment("DO NOT change this value. This is used to keep track of the current config version.")
        }

        if (version < 2) {
            set("status.max-len", 20)
            comment("status.max-len", "The amount of characters a status message can be at most. This setting ignores tags.")

            set("status.key-tag", true)
            set("status.lang-tag", true)
            set("status.font-tag", true)
            set("status.hover-tag", true)

            set("status.nbt-tag", false)
            set("status.click-tag", false)
            set("status.score-tag", false)
            set("status.insert-tag", false)
            set("status.newline-tag", false)
            set("status.selector-tag", false)

            set("version", 2, true)
        }

    }

    private fun set(key: String, data: Any, force: Boolean = false) {
        if (config.get(key) == null || force) {
            config.set(key, data)
        }
    }

    private fun remove(key: String, version: Int = 2) {
        comment(key, "Removed in version $version. Left for reference")
    }

    private fun comment(key: String, vararg lines: String) {
        config.setComments(key, lines.asList())
    }


}