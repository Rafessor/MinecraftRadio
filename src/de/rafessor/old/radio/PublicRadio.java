package de.rafessor.old.radio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;

public class PublicRadio {

	private OldRadio plugin;

	@Getter
	private List<Player> listeners = new ArrayList<>();
	@Getter
	@Setter
	private String name;
	private List<Song> playlist = new ArrayList<>();
	@Getter
	private Song songPlaying;
	@Getter
	private SongPlayer songPlayer;

	public PublicRadio(OldRadio plugin, String name) {
		this.plugin = plugin;
		this.name = name;

		reloadSongs();
		nextSong();
	}

	void addListener(Player p) {
		listeners.add(p);
		songPlayer.addPlayer(p);
	}

	void removeListener(Player p) {
		NoteBlockPlayerMain.stopPlaying(p);
		listeners.remove(p);
		songPlayer.removePlayer(p);
	}

	public void nextSong() {
		if (songPlaying == null
				|| playlist.size() < playlist.indexOf(songPlaying) + 2)
			songPlaying = playlist.get(0);
		else
			songPlaying = playlist.get(playlist.lastIndexOf(songPlaying) + 1);

		songPlayer.destroy();
		songPlayer = new RadioSongPlayer(songPlaying);

		for (Player p : listeners) {
			songPlayer.addPlayer(p);
			plugin.getRadioManager().updateRadioItem(p);
		}

		songPlayer.setPlaying(true);
	}

	public void reloadSongs() {
		if (songPlayer != null)
			songPlayer.destroy();

		playlist.clear();

		File folder = new File(plugin.getPlugin().getDataFolder(), "Radios");
		File dir = new File(folder, name);
		File[] directoryListing = dir.listFiles();

		if (directoryListing != null)
			for (File child : directoryListing)
				if (child.getAbsolutePath().endsWith(".nbs"))
					playlist.add(NBSDecoder.parse(child));

		songPlaying = playlist.get(0);
		songPlayer = new RadioSongPlayer(songPlaying);

		for (Player p : listeners)
			songPlayer.addPlayer(p);

		songPlayer.setPlaying(true);
	}
}
