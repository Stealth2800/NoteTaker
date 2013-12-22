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
package com.stealthyone.mcb.notetaker;

import com.stealthyone.mcb.notetaker.backend.NoteManager;
import com.stealthyone.mcb.notetaker.commands.CmdNoteTaker;
import com.stealthyone.mcb.notetaker.config.ConfigHelper;
import com.stealthyone.mcb.stbukkitlib.lib.help.HelpAPI;
import com.stealthyone.mcb.stbukkitlib.lib.help.HelpManager;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageManager;
import com.stealthyone.mcb.stbukkitlib.lib.updates.UpdateChecker;
import com.stealthyone.mcb.stbukkitlib.lib.updating.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoteTaker extends JavaPlugin {

    public final static class Log {

        public static void debug(String msg) {
            if (ConfigHelper.DEBUG.get())
                instance.logger.log(Level.INFO, String.format("[%s DEBUG] %s", instance.getName(), msg));
        }

        public static void info(String msg) {
            instance.logger.log(Level.INFO, String.format("[%s] %s", instance.getName(), msg));
        }

        public static void warning(String msg) {
            instance.logger.log(Level.WARNING, String.format("[%s] %s", instance.getName(), msg));
        }

        public static void severe(String msg) {
            instance.logger.log(Level.SEVERE, String.format("[%s] %s", instance.getName(), msg));
        }

    }

    private static NoteTaker instance;
    {
        instance = this;
    }

    public static NoteTaker getInstance() {
        return instance;
    }

    private Logger logger;

    private MessageManager messageManager;
    private HelpManager helpManager;
    private UpdateChecker updateChecker;

    private NoteManager noteManager;

    @Override
    public void onLoad() {
        logger = Bukkit.getLogger();
        getDataFolder().mkdir();
    }

    @Override
    public void onEnable() {
        /* Setup config */
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);
        saveConfig();

        /* Setup important plugin components */
        noteManager = new NoteManager(this);

        messageManager = new MessageManager(this);
        helpManager = HelpAPI.registerHelp(this);

        /* Register commands */
        getCommand("notetaker").setExecutor(new CmdNoteTaker(this));

        updateChecker = UpdateChecker.scheduleForMe(this, 00000);
        Log.info("NoteTaker v" + getDescription().getVersion() + " by Stealth2800 enabled.");
    }

    @Override
    public void onDisable() {
        noteManager.saveAll();
        Log.info("NoteTaker v" + getDescription().getVersion() + " by Stealth2800 disabled.");
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}