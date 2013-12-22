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
package com.stealthyone.mcb.notetaker.backend;

import com.stealthyone.mcb.notetaker.NoteTaker;
import com.stealthyone.mcb.notetaker.NoteTaker.Log;
import com.stealthyone.mcb.notetaker.backend.notes.Note;
import com.stealthyone.mcb.notetaker.config.ConfigHelper;
import com.stealthyone.mcb.stbukkitlib.lib.autosaving.Autosavable;
import com.stealthyone.mcb.stbukkitlib.lib.autosaving.AutosavingAPI;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import com.stealthyone.mcb.stbukkitlib.lib.utils.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NoteManager implements Autosavable {

    private NoteTaker plugin;
    private YamlFileManager noteFile;
    private YamlFileManager playerFile;

    private Map<String, Note> loadedNotes = new HashMap<String, Note>();
    private Map<String, List<String>> noteIndex = new HashMap<String, List<String>>(); //Player name, list of note IDs

    public NoteManager(NoteTaker plugin) {
        this.plugin = plugin;
        noteFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "notes.yml");
        playerFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "players.yml");
        Log.info("Loaded " + reloadNotes() + " notes from notes.yml");
        indexPlayers();

        int saveTime = ConfigHelper.AUTOSAVE_INTERVAL.get();
        if (saveTime >= 1) {
            AutosavingAPI.registerAutosavable(plugin, "noteManager", this, saveTime * 60);
        } else {
            Log.warning("Autosaving disabled. It is recommended that you enable it to prevent data loss.");
        }
    }

    @Override
    public void saveAll() {
        FileConfiguration playerConfig = playerFile.getConfig();
        for (Entry<String, List<String>> entry : noteIndex.entrySet()) {
            playerConfig.set(entry.getKey() + ".notes", entry.getValue());
        }
        playerFile.saveFile();
        noteFile.saveFile();
    }

    public YamlFileManager getNoteFile() {
        return noteFile;
    }

    public int reloadNotes() {
        loadedNotes.clear();
        FileConfiguration config = noteFile.getConfig();
        List<String> notesToPurge = new ArrayList<String>();
        for (String id : config.getKeys(false)) {
            Note note = new Note(config.getConfigurationSection(id));
            if (note.getCreatorRaw() == null && note.getMembers().size() == 0) {
                notesToPurge.add(id);
            } else {
                loadNote(id);
            }
        }

        if (notesToPurge.size() > 0 && ConfigHelper.PURGE_NOTES.get()) {
            for (String id : notesToPurge) {
                config.set(id, null);
            }
            Log.info("Purged " + notesToPurge.size() + " dead notes");
        }
        return loadedNotes.size();
    }

    public void indexPlayers() {
        noteIndex.clear();
        ConfigurationSection config = playerFile.getConfig();
        for (String playerName : config.getKeys(false)) {
            List<String> notes = config.getStringList(playerName + ".notes");
            noteIndex.put(playerName, notes);
        }
    }

    public Note loadNote(String id) {
        ConfigurationSection config = noteFile.getConfig().getConfigurationSection(id);
        if (config != null) {
            Note note = new Note(config);
            loadedNotes.put(id, note);
            return note;
        }
        return null;
    }

    public Note createNote(String creator, String message) {
        String id = getNextNoteId();
        ConfigurationSection config = noteFile.getConfig().createSection(id);
        config.set("creator", creator);
        config.set("message", message);
        return loadNote(id);
    }

    public String getNextNoteId() {
        String id = RandomUtils.getRandomString(8, true);
        while (noteFile.getConfig().getConfigurationSection(id) != null) {
            id = RandomUtils.getRandomString(8, true);
        }
        return id;
    }

    public Note getNote(String id) {
        return loadedNotes.get(id);
    }

    public List<Note> getNotes(String playerName) {
        Log.debug("getNotes for " + playerName);
        playerName = playerName.toLowerCase();
        List<String> noteIds = noteIndex.get(playerName);
        List<Note> returnList = new ArrayList<Note>();
        List<String> notesToRemove = new ArrayList<String>();
        if (noteIds != null) {
            Log.debug("Note IDs not null");
            for (String id : noteIds) {
                Log.debug("id: " + id);
                Note note = getNote(id);
                if (note == null || !note.doesPlayerHaveAccess(playerName)) {
                    Log.debug("removing note");
                    notesToRemove.add(id);
                } else {
                    Log.debug("Adding note");
                    returnList.add(note);
                }
            }

            for (String id : notesToRemove) {
                Log.debug("Removing " + id);
                noteIds.remove(id);
                noteIndex.put(playerName, noteIds);
            }
        }
        return returnList;
    }

    public void addNote(String playerName, String noteId) {
        playerName = playerName.toLowerCase();
        List<String> notes = noteIndex.get(playerName);
        if (notes == null)
            notes = new ArrayList<String>();
        notes.add(0, noteId);
        noteIndex.put(playerName, notes);
    }

    public void removeNote(String playerName, String noteId) {
        playerName = playerName.toLowerCase();
        List<String> notes = noteIndex.get(playerName);
        if (notes == null)
            notes = new ArrayList<String>();
        notes.remove(noteId);
        noteIndex.put(playerName, notes);
    }

}