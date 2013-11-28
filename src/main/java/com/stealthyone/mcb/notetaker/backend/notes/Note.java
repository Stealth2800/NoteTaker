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
package com.stealthyone.mcb.notetaker.backend.notes;

import com.stealthyone.mcb.notetaker.messages.NoticeMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class Note {

    private ConfigurationSection config;

    public Note(ConfigurationSection config) {
        this.config = config;
    }

    public String getId() {
        return config.getName();
    }

    public String getName() {
        return config.getString("name", "" + ChatColor.RED + ChatColor.ITALIC + "Untitled");
    }

    public String getMessage() {
        return config.getString("message", "" + ChatColor.RED + ChatColor.ITALIC + "No message found");
    }

    public String getCreator() {
        return config.getString("creator", "" + ChatColor.RED + ChatColor.ITALIC + "Anonymous");
    }

    public String getCreatorRaw() {
        return config.getString("creator");
    }

    public List<String> getMembers() {
        return config.getStringList("members");
    }

    public boolean addMember(String memberName) {
        memberName = memberName.toLowerCase();
        List<String> curMembers = getMembers();
        if (!curMembers.contains(memberName)) {
            curMembers.add(memberName);
            Player player = Bukkit.getPlayer(memberName);
            if (player != null) {
                NoticeMessage.NOTE_SHARE_RECEIVED.sendTo(player, getCreator());
            }
            config.set("members", curMembers);
            return true;
        }
        return false;
    }

    public boolean removeMember(String memberName) {
        memberName = memberName.toLowerCase();
        List<String> curMembers = getMembers();
        if (curMembers.contains(memberName)) {
            curMembers.remove(memberName);
            config.set("members", curMembers);
            return true;
        }
        return false;
    }

    public boolean canMembersEdit() {
        return config.getBoolean("editableByMembers", false);
    }

    public void setMembersCanEdit(boolean newValue) {
        config.set("editableByMembers", newValue);
    }

    public boolean doesPlayerHaveAccess(String playerName) {
        return getCreator().equalsIgnoreCase(playerName) || getMembers().contains(playerName.toLowerCase());
    }

}