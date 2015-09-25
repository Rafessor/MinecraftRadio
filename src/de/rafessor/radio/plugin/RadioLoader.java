package de.rafessor.radio.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sound.midi.InvalidMidiDataException;

import lombok.AllArgsConstructor;
import de.rafessor.api.Language;
import de.rafessor.radio.radiostation.PublicServerRadio;

@AllArgsConstructor
public class RadioLoader {

	private RadioPlugin plugin;

	public List<PublicServerRadio> load() throws IOException,
			InvalidMidiDataException {
		List<PublicServerRadio> radios = new LinkedList<PublicServerRadio>();
		File folder = new File(plugin.getDataFolder(), "Radios");

		if (!folder.exists()) {
			folder.mkdirs();

			// Sadly i cannot use the resourceloader here because of the radio
			// station name system :/
			CodeSource src = Language.class.getProtectionDomain()
					.getCodeSource();
			if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					if (name.startsWith("resources/radiostation")
							&& name.endsWith(".nbs")) {
						String[] n = name.split("/");
						String fileName = n[n.length - 1];
						String folderName = n[n.length - 2];
						File radioFolder = new File(folder, folderName);
						radioFolder.mkdirs();
						File target = new File(radioFolder, fileName);
						Files.copy(zip, target.toPath());
					}
				}
			}
		}

		File[] directoryListing = folder.listFiles();

		for (File file : directoryListing) {
			String radioName = file.getName();
			File radioFolder = new File(folder, radioName);

			PublicServerRadio station = new PublicServerRadio(radioName);

			for (File songFile : radioFolder.listFiles()) {
				if (songFile.getName().endsWith(".midi"))
					station.addSongToPlaylist(plugin.getMidiParser().parseFile(
							songFile));
				else if (songFile.getName().endsWith(".nbs"))
					station.addSongToPlaylist(plugin.getNbsParser().parseFile(
							songFile));
			}

			radios.add(station);
		}

		return radios;
	}

}
