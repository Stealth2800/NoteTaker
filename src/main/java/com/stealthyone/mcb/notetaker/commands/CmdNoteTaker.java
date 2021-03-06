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
package com.stealthyone.mcb.notetaker.commands;

import com.stealthyone.mcb.notetaker.NoteTaker;
import com.stealthyone.mcb.notetaker.backend.NoteManager;
import com.stealthyone.mcb.notetaker.backend.notes.Note;
import com.stealthyone.mcb.notetaker.messages.ErrorMessage;
import com.stealthyone.mcb.notetaker.messages.NoticeMessage;
import com.stealthyone.mcb.notetaker.messages.UsageMessage;
import com.stealthyone.mcb.notetaker.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.lib.updates.UpdateChecker;
import com.stealthyone.mcb.stbukkitlib.lib.utils.ArrayUtils;
import com.stealthyone.mcb.stbukkitlib.lib.utils.InputUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdNoteTaker implements CommandExecutor {

    private NoteTaker plugin;

    public CmdNoteTaker(NoteTaker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* Create note command */
                case "create":
                    cmdCreate(sender, command, label, args);
                    return true;

                /* Delete note command */
                case "delete":
                    cmdDelete(sender, command, label, args);
                    return true;

                /* Note help command */
                case "help":
                    plugin.getHelpManager().handleHelpCommand(sender, "notes", label, args, 1);
                    return true;

                /* Note info command */
                case "info":
                    cmdInfo(sender, command, label, args);
                    return true;

                /* List notes command */
                case "list":
                    cmdList(sender, command, label, args);
                    return true;

                /* Modify note command */
                case "modify":
                    cmdModify(sender, command, label, args);
                    return true;

                /* Force plugin to save */
                case "saveall":
                    cmdSaveAll(sender, command, label, args);
                    return true;

                /* Save location command */
                case "saveloc":
                    cmdSaveLoc(sender, command, label, args);
                    return true;

                /* Share note command */
                case "share":
                    cmdShare(sender, command, label, args);
                    return true;

                /* Unshare note command */
                case "unshare":
                    cmdUnshare(sender, command, label, args);
                    return true;

                /* Version command */
                case "version":
                    cmdVersion(sender, command, label, args);
                    return true;

                /* View note command */
                case "view":
                    cmdView(sender, command, label, args);
                    return true;
            }
        }
        plugin.getHelpManager().handleHelpCommand(sender, "notes", label, args, 0);
        return true;
    }

    /*
     * Create note command
     */
    private void cmdCreate(CommandSender sender, Command command, String label, String[] args) {
        //note create <message>
        if (!PermissionNode.NOTES_CREATE.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 2) {
            UsageMessage.NOTES_CREATE.sendTo(sender, label);
        } else {
            NoteManager noteManager = plugin.getNoteManager();
            noteManager.addNote(sender.getName(), noteManager.createNote(sender.getName(), ArrayUtils.stringArrayToString(args, 1)).getId());
            NoticeMessage.NOTE_CREATED.sendTo(sender);
        }
    }

    /*
     * Delete note command
     */
    private void cmdDelete(CommandSender sender, Command command, String label, String[] args) {
        //note delete <note number>
        if (!PermissionNode.NOTES_DELETE.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 2) {
            UsageMessage.NOTES_DELETE.sendTo(sender, label);
        } else {
            int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Note number");
                return;
            }

            NoteManager noteManager = plugin.getNoteManager();
            try {
                noteManager.getNotes(sender.getName()).get(number - 1).delete();
            } catch (IndexOutOfBoundsException ex) {
                ErrorMessage.INVALID_NOTE_NUMBER.sendTo(sender);
            }
        }
    }

    /*
     * Note info command
     */
    private void cmdInfo(CommandSender sender, Command command, String label, String[] args) {
        //note info [player] <note number>
        if (!PermissionNode.NOTES_INFO.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 2) {
            if (PermissionNode.NOTES_INFO_OTHER.isAllowed(sender)) {
                UsageMessage.NOTES_INFO_OTHER.sendTo(sender, label);
            } else {
                UsageMessage.NOTES_INFO.sendTo(sender, label);
            }
        } else {
            String senderName = sender.getName();
            String target = senderName;
            int number;
            if (args.length > 2) {
                target = args[1];
                number = 2;
            } else {
                number = 1;
            }

            try {
                number = Integer.parseInt(args[number]);
            } catch (NumberFormatException ex) {
                ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Note number");
                return;
            }

            if (!target.equals(senderName) && !PermissionNode.NOTES_INFO_OTHER.isAllowed(sender)) {
                ErrorMessage.NO_PERMISSION.sendTo(sender);
            } else {
                NoteManager noteManager = plugin.getNoteManager();
                Note note;
                try {
                    note = noteManager.getNotes(target).get(number - 1);
                } catch (IndexOutOfBoundsException ex) {
                    ErrorMessage.INVALID_NOTE_NUMBER.sendTo(sender);
                    return;
                }

                sender.sendMessage(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "Note: " + ChatColor.AQUA + note.getName() + ChatColor.DARK_GRAY + "=====");
                sender.sendMessage(ChatColor.GOLD + "ID: " + ChatColor.YELLOW + note.getId());
                sender.sendMessage(ChatColor.GOLD + "Creator: " + ChatColor.YELLOW + note.getCreator());
                sender.sendMessage(ChatColor.GOLD + "Members: " + ChatColor.YELLOW + note.getMembers().toString());
                sender.sendMessage(ChatColor.GOLD + "Editable by members: " + (note.canMembersEdit() ? ChatColor.GREEN + "TRUE" : ChatColor.RED + "FALSE"));
                sender.sendMessage(ChatColor.GOLD + "Message: " + ChatColor.YELLOW + note.getMessage());
            }
        }
    }

    /*
     * List notes command
     */
    private void cmdList(CommandSender sender, Command command, String label, String[] args) {
        //note list [player name] [page]
        if (!PermissionNode.NOTES_LIST.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            String senderName = sender.getName();
            String target = senderName;
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    target = args[1];
                    if (args.length > 2) {
                        try {
                            page = Integer.parseInt(args[2]);
                            if (page <= 0) {
                                ErrorMessage.INVALID_PAGE.sendTo(sender, Integer.toString(page));
                                return;
                            }
                        } catch (NumberFormatException ex2) {
                            ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Page number");
                            return;
                        }
                    }
                }
            }

            if (!target.equalsIgnoreCase(senderName) && !PermissionNode.NOTES_LIST_OTHER.isAllowed(sender)) {
                ErrorMessage.NO_PERMISSION.sendTo(sender);
            } else {
                if (!target.equals(senderName) && PermissionNode.NOTES_LIST_OTHER.isAllowed(sender))
                    UsageMessage.NOTES_LIST_OTHER.sendTo(sender, label);

                NoteManager noteManager = plugin.getNoteManager();
                List<Note> notes = noteManager.getNotes(target);
                List<String> messages = new ArrayList<String>();
                for (int i = 0; i < 8; i++) {
                    int finalNum = ((page - 1) * 8) + i;
                    Note curNote;
                    try {
                        curNote = notes.get(finalNum);
                        messages.add(ChatColor.GOLD + Integer.toString(finalNum + 1) + ") " + (curNote.getCreator().equalsIgnoreCase(senderName) ? ChatColor.YELLOW : ChatColor.RED) + curNote.getName());
                    } catch (IndexOutOfBoundsException ex) {
                        if (messages.size() == 0)
                            messages.add("" + ChatColor.RED + ChatColor.ITALIC + "Nothing here.");
                        break;
                    }
                }

                sender.sendMessage(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "Notes for: " + ChatColor.AQUA + target + ChatColor.DARK_GRAY + "=====");
                for (String message : messages)
                    sender.sendMessage(message);
            }
        }
    }

    /*
     * Modify note command
     */
    private void cmdModify(CommandSender sender, Command command, String label, String[] args) {
        //note modify <note number> <option> <value>
        if (!PermissionNode.NOTES_MODIFY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
            return;
        } else if (args.length < 4) {
            UsageMessage.NOTES_MODIFY.sendTo(sender, label);
        } else {
            Note note;
            int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Note number");
                return;
            }

            try {
                note = plugin.getNoteManager().getNotes(sender.getName()).get(number - 1);
            } catch (IndexOutOfBoundsException ex) {
                ErrorMessage.INVALID_NOTE_NUMBER.sendTo(sender);
                return;
            }

            switch (args[2].toLowerCase()) {
                /* Modify note name command */
                case "name":
                    cmdModify_Name(sender, command, label, args, note);
                    return;

                /* Modify note editable by members setting */
                case "memberedit":
                    cmdModify_MemberEdit(sender, command, label, args, note);
                    return;

                default:
                    break;
            }
        }
        plugin.getHelpManager().handleHelpCommand(sender, "notes.modification", label, args, 1);
    }

    /*
     * Modify note name command
     */
    private void cmdModify_Name(CommandSender sender, Command command, String label, String[] args, Note note) {
        if (!PermissionNode.NOTES_MODIFY_NAME.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (!note.getCreator().equalsIgnoreCase(sender.getName()) && !note.canMembersEdit()) {
            ErrorMessage.CANNOT_MODIFY_NOTE.sendTo(sender);
        } else {
            note.setName(args[3]);
            NoticeMessage.NOTE_SET_NAME.sendTo(sender, args[3]);
        }
    }

    /*
     * Modify note editable by members setting
     */
    private void cmdModify_MemberEdit(CommandSender sender, Command command, String label, String[] args, Note note) {
        if (!PermissionNode.NOTES_MODIFY_MEMBEREDIT.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (!note.getCreator().equalsIgnoreCase(sender.getName()) && !note.canMembersEdit()) {
            ErrorMessage.CANNOT_MODIFY_NOTE.sendTo(sender);
        } else {
            try {
                boolean newValue = InputUtils.getInputBoolean(args[3]);
                if (note.setMembersCanEdit(InputUtils.getInputBoolean(args[3]))) {
                    NoticeMessage.NOTE_SET_MEMBEREDIT.sendTo(sender, Boolean.toString(newValue));
                } else {
                    ErrorMessage.NOTE_VALUE_ALREADY_SET.sendTo(sender, Boolean.toString(newValue));
                }
            } catch (IllegalArgumentException ex) {
                ErrorMessage.OBJECT_MUST_BE_BOOLEAN.sendTo(sender, "Value");
            }
        }
    }

    /*
     * Save all files to the disk
     */
    private void cmdSaveAll(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.SAVEALL.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            plugin.getNoteManager().saveAll();
            NoticeMessage.PLUGIN_SAVED.sendTo(sender);
        }
    }

    /*
     * Save location command
     */
    private void cmdSaveLoc(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.NOTES_CREATE.isAllowed(sender) || !PermissionNode.NOTES_SAVELOC.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else {
            NoteManager noteManager = plugin.getNoteManager();
            Location location = ((Player) sender).getLocation();
            noteManager.addNote(sender.getName(), noteManager.createNote(sender.getName(), ChatColor.GOLD + "Saved location: " + ChatColor.RESET + location.getWorld().getName() + " " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()).getId());
            NoticeMessage.NOTE_SAVEDLOC.sendTo(sender);
        }
    }

    /*
     * Share note command
     */
    private void cmdShare(CommandSender sender, Command command, String label, String[] args) {
        //note share <note num> <player>
        if (!PermissionNode.NOTES_SHARING.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.NOTES_SHARE.sendTo(sender, label);
        } else {
            String target = args[2];
            if (target.equalsIgnoreCase(sender.getName())) {
                ErrorMessage.CANNOT_SHARE_NOTE_WITH_SELF.sendTo(sender);
                return;
            }

            int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Note number");
                return;
            }

            NoteManager noteManager = plugin.getNoteManager();
            try {
                if (noteManager.getNotes(sender.getName()).get(number - 1).addMember(target)) {
                    NoticeMessage.NOTE_SHARE_SENT.sendTo(sender, target);
                } else {
                    ErrorMessage.NOTE_ALREADY_SHARED.sendTo(sender, target);
                }
            } catch (IndexOutOfBoundsException ex) {
                ErrorMessage.INVALID_NOTE_NUMBER.sendTo(sender);
            }
        }
    }

    /*
     * Unshare note command
     */
    private void cmdUnshare(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.NOTES_SHARING.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.NOTES_SHARE.sendTo(sender, label);
        } else {
            String target = args[2];
            if (target.equalsIgnoreCase(sender.getName())) {
                ErrorMessage.CANNOT_SHARE_NOTE_WITH_SELF.sendTo(sender);
                return;
            }

            int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Note number");
                return;
            }

            NoteManager noteManager = plugin.getNoteManager();
            try {
                if (noteManager.getNotes(sender.getName()).get(number - 1).removeMember(target)) {
                    NoticeMessage.NOTE_UNSHARED.sendTo(sender, target);
                } else {
                    ErrorMessage.NOTE_NOT_SHARED.sendTo(sender, target);
                }
            } catch (IndexOutOfBoundsException ex) {
                ErrorMessage.INVALID_NOTE_NUMBER.sendTo(sender);
            }
        }
    }

    /*
     * Version command
     */
    private void cmdVersion(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "NoteTaker" + ChatColor.GOLD + " v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "Created by Stealth2800");
        sender.sendMessage(ChatColor.GOLD + "Website: " + ChatColor.AQUA + "http://stealthyone.com/bukkit");
        UpdateChecker updateChecker = plugin.getUpdateChecker();
        if (updateChecker.checkForUpdates()) {
            String curVer = plugin.getDescription().getVersion();
            String remVer = updateChecker.getNewVersion().replace("v", "");
            sender.sendMessage(ChatColor.RED + "A different version was found on BukkitDev! (Current: " + curVer + " | Remote: " + remVer + ")");
            sender.sendMessage(ChatColor.RED + "You can download it from " + updateChecker.getVersionLink());
        }
    }

    /*
     * View note command
     */
    private void cmdView(CommandSender sender, Command command, String label, String[] args) {
        //note view <note number>
        if (!PermissionNode.NOTES_VIEW.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 2) {
            UsageMessage.NOTES_VIEW.sendTo(sender, label);
        } else {
            int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                ErrorMessage.OBJECT_MUST_BE_INT.sendTo(sender, "Note number");
                return;
            }

            Note note;
            try {
                note = plugin.getNoteManager().getNotes(sender.getName()).get(number - 1);
            } catch (IndexOutOfBoundsException ex) {
                ErrorMessage.INVALID_NOTE_NUMBER.sendTo(sender);
                return;
            }

            sender.sendMessage(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "Note: " + ChatColor.AQUA + note.getName() + ChatColor.DARK_GRAY + "=====");
            sender.sendMessage(ChatColor.GOLD + " \"" + ChatColor.RESET + note.getMessage() + ChatColor.GOLD + "\"");
        }
    }

}