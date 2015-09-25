package de.rafessor.radio.radiostation;

import org.bukkit.entity.Player;

import de.johni0702.api.noteblock.song.Song;
import de.rafessor.radio.plugin.RadioPlugin;

public interface RadioStation {

	public String getName();

	public void startPlaying(RadioPlugin plugin);

	public void addSongToPlaylist(Song song);

	public void shufflePlaylist();

	public PlayList getPlayList();

	public void addListener(Player player);

	public void removeListener(Player player);

}
