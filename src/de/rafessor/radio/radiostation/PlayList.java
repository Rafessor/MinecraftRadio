package de.rafessor.radio.radiostation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import de.johni0702.api.noteblock.song.Song;

public class PlayList {

	private int currentlyPlaying;
	@Getter
	private List<Song> playlist = new LinkedList<Song>();

	public PlayList() {
		currentlyPlaying = -1;
	}

	public void shuffle() {
		Collections.shuffle(playlist);
	}

	public Song nextSong() {
		if (!playlist.contains(currentlyPlaying + 1))
			currentlyPlaying = 0;
		else
			currentlyPlaying = currentlyPlaying + 1;
		return playlist.get(currentlyPlaying);
	}

	public void addSong(Song song) {
		playlist.add(song);
	}

}
