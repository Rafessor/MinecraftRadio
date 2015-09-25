/*
 * This file is part of NoteBlockAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Johni0702 <https://github.com/johni0702>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.johni0702.api.noteblock.player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

import de.johni0702.api.noteblock.song.Layer;
import de.johni0702.api.noteblock.song.Note;
import de.johni0702.api.noteblock.song.Song;
import de.rafessor.radio.plugin.RadioPlugin;

public class SongPlayer extends BukkitRunnable {

	private RadioPlugin plugin;

	private int tick = -1;
	private Song song;
	private SongProvider songProvider;
	private double volume;
	private List<Player> listeners = new CopyOnWriteArrayList<>();
	private Map<Player, Double> playerVolume = new MapMaker().weakKeys()
			.concurrencyLevel(1).makeMap();

	public SongPlayer(RadioPlugin plugin) {
		this.plugin = plugin;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		Preconditions.checkArgument(volume >= 0 && volume <= 1,
				"volume must be between 0 and 1 (inclusive)");
		this.volume = volume;
	}

	public double getVolume(Player player) {
		Double volume = playerVolume.get(player);
		return volume == null ? 1 : 0;
	}

	public void setVolume(Player player, double volume) {
		playerVolume.put(player, volume);
	}

	public List<Player> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	public void addListener(Player player) {
		listeners.add(player);
	}

	public void removeListener(Player player) {
		listeners.remove(player);
	}

	public boolean start(SongProvider songProvidor) {
		this.songProvider = songProvidor;

		if (song == null)
			song = songProvider.nextSong();
		song.updateLength();
		tick = 0;
		this.runTaskTimer(plugin, 0, 1);
		return true;
	}

	public boolean stop() {
		this.cancel();
		tick = -1;
		return true;
	}

	@Override
	public void run() {
		double volume = this.volume;

		for (Player player : listeners) {
			double playerVolume = volume * getVolume(player);
			for (Layer layer : song.getLayers()) {
				Note note = layer.getNoteBlock(tick);
				if (note != null) {
					double noteVolume = playerVolume * layer.getVolume();
					player.playSound(player.getLocation(), note.getInstrument()
							.getSoundType(), note.getPitch().pitch,
							(float) noteVolume); // TODO source loc
				}
			}
		}

		tick++;
		if (tick > song.getLength()) {
			song = null;

			if (songProvider != null) {
				song = songProvider.nextSong();
			}

			if (song != null) {
				song.updateLength();
				tick = 0;
			}
			tick = -1;
		}
	}
}