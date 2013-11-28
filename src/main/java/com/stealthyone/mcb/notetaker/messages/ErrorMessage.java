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

import com.stealthyone.mcb.notetaker.NoteTaker;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageReferencer;
import org.bukkit.command.CommandSender;

public enum ErrorMessage implements MessageReferencer {

    CANNOT_SHARE_NOTE_WITH_SELF,
    CANNOT_UNSHARE_NOTE_FROM_SELF,
    INVALID_PAGE,
    INVALID_NOTE_NUMBER,
    MUST_BE_PLAYER,
    NO_PERMISSION,
    NOTE_ALREADY_SHARED,
    NOTE_NOT_SHARED,
    OBJECT_MUST_BE_INT;

    private String path;

    private ErrorMessage() {
        this.path = "errors." + toString().toLowerCase();
    }

    @Override
    public String getMessagePath() {
        return path;
    }

    @Override
    public void sendTo(CommandSender sender) {
        sender.sendMessage(NoteTaker.getInstance().getMessageManager().getMessage(this));
    }

    @Override
    public void sendTo(CommandSender sender, String... replacements) {
        sender.sendMessage(NoteTaker.getInstance().getMessageManager().getMessage(this, replacements));
    }

}