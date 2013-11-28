/*
 *                           NoteTaker
 * Copyright (C) 2013 Stealth2800 <stealth2800@stealthyone.com>
 *              Website: <http://stealthyone.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stealthyone.mcb.notetaker.messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum UsageMessage {

    NOTES_CREATE("{TAG}/{LABEL} create <message>"),
    NOTES_INFO("{TAG}/{LABEL} info <note number>"),
    NOTES_INFO_OTHER("{TAG}/{LABEL} info [player] <note number>"),
    NOTES_LIST_OTHER("&4Additional usage: &c/{LABEL} list [player]"),
    NOTES_SHARE("{TAG}/{LABEL} share <note number> <player>");

    private String message;

    private UsageMessage(String message) {
        this.message = message;
    }

    public void sendTo(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', message).replace("{LABEL}", label).replace("{TAG}", ChatColor.DARK_RED + "USAGE: "));
    }

    public void sendTo(CommandSender sender, String label, String... replacements) {
        sender.sendMessage(ChatColor.RED + String.format(ChatColor.translateAlternateColorCodes('&', message), replacements).replace("{LABEL}", label).replace("{TAG}", ChatColor.DARK_RED + "USAGE: "));
    }

}