/*
 * Bukkit plugin: NoteTaker
 * Copyright (C) 2013 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/bukkit>
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
package com.stealthyone.mcb.notetaker.permissions;

import org.bukkit.command.CommandSender;

public enum PermissionNode {

    NOTES_CREATE,
    NOTES_DELETE,
    NOTES_INFO,
    NOTES_INFO_OTHER,
    NOTES_LIST,
    NOTES_LIST_OTHER,
    NOTES_MODIFY,
    NOTES_MODIFY_MEMBEREDIT,
    NOTES_MODIFY_NAME,
    NOTES_SHARING,
    NOTES_SAVELOC,
    NOTES_VIEW,

    SAVEALL;

    private String permission;

    private PermissionNode() {
        permission = "notetaker." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

}