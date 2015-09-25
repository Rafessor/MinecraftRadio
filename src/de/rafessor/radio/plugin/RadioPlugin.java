package de.rafessor.radio.plugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;

import lombok.Getter;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.johni0702.api.noteblock.parser.MidiParser;
import de.johni0702.api.noteblock.parser.NbsParser;
import de.johni0702.api.noteblock.song.Song;
import de.rafessor.api.Language;
import de.rafessor.api.ResourceLoader;
import de.rafessor.old.radio.OldRadio;
import de.rafessor.radio.radiostation.RadioStation;

public class RadioPlugin extends JavaPlugin implements CommandExecutor,
		Listener {

	@Getter
	private String version;

	@Getter
	private Language language;
	@Getter
	private ResourceLoader resourceLoader;

	@Getter
	private MidiParser midiParser; // TODO
	@Getter
	private NbsParser nbsParser; // TODO

	@Getter
	private List<RadioStation> radioStations = new LinkedList<RadioStation>();

	@Getter
	private OldRadio old;

	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		this.resourceLoader = new ResourceLoader(this);

		try {
			this.language = new Language(this);

			File info = new File(getDataFolder(), "info.txt");
			if (info.exists())
				info.delete();
			resourceLoader.load("info", ".txt", getDataFolder());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		if (true) {
			old = new OldRadio(this);
		} else {
			getServer().getPluginManager().registerEvents(this, this);

			this.version = "v2.0";

			new AutoUpdater(this, version).updateCheck();

			this.nbsParser = new NbsParser();
			this.midiParser = new MidiParser();

			RadioLoader loader = new RadioLoader(this);
			try {
				for (RadioStation station : loader.load()) {
					radioStations.add(station);
					station.startPlaying(this);
				}
			} catch (IOException | InvalidMidiDataException e) {
				e.printStackTrace();
			}

			getCommand("radio").setExecutor(this);

			System.out.println("--=<>=---==---===<(O)>===---==---=<>=--");
			System.out.println("       Radio " + version + " plugin loaded");
			System.out.println("          Made by Rafessor:");
			System.out.println("    https://twitter.com/TheRafessor");
			System.out.println("--=<>=---==---===<(O)>===---==---=<>=--");

			for (RadioStation station : radioStations) {
				System.out.println("Radio: " + station.getName());
				for (Song song : station.getPlayList().getPlaylist())
					System.out.println(" - " + song.getName());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (RadioStation station : radioStations)
			if (station.getName().contains("Noteblock"))
				station.addListener(e.getPlayer());
	}

}
