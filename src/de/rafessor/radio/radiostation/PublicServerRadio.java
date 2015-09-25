package de.rafessor.radio.radiostation;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

import org.bukkit.entity.Player;

import de.johni0702.api.noteblock.player.SongPlayer;
import de.johni0702.api.noteblock.player.SongProvider;
import de.johni0702.api.noteblock.song.Song;
import de.rafessor.radio.plugin.RadioPlugin;

public class PublicServerRadio implements RadioStation, SongProvider {

	@Getter
	private String name;
	private SongPlayer songPlayer;
	private PlayList playlist;

	private List<Player> listeners = new LinkedList<Player>();

	public PublicServerRadio(String name) {
		this.name = name;
		this.playlist = new PlayList();
	}

	@Override
	public void startPlaying(RadioPlugin plugin) {
		songPlayer = new SongPlayer(plugin);
		songPlayer.start(this);
	}

	@Override
	public void addSongToPlaylist(Song song) {
		playlist.addSong(song);
	}

	@Override
	public void shufflePlaylist() {
		playlist.shuffle();
	}

	@Override
	public PlayList getPlayList() {
		return playlist;
	}

	@Override
	public Song nextSong() {
		return playlist.nextSong();
	}

	@Override
	public void addListener(Player player) {
		listeners.add(player);
		songPlayer.addListener(player);
	}

	@Override
	public void removeListener(Player player) {
		songPlayer.removeListener(player);
		listeners.remove(player);
	}
}
