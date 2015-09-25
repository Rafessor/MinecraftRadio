package de.rafessor.old.radio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.xxmicloxx.NoteBlockAPI.Song;

import de.rafessor.api.Language;

public class RadioManager {

	private Language language;

	@Getter
	@Setter
	private List<PublicRadio> radios = new ArrayList<>();
	private WeakHashMap<Player, PublicRadio> radio = new WeakHashMap<>();

	public RadioManager(OldRadio plugin) throws IOException {
		this.language = plugin.getLanguage();

		new RadioListener(this, plugin);
		new RadioLoader(plugin, this);
	}

	public Song getSong(int i, List<Song> playlist) {
		return playlist.get(i);
	}

	public PublicRadio getRadio(Player p) {
		return radio.get(p);
	}

	public void setRadio(Player p, PublicRadio radio) {
		PublicRadio old = getRadio(p);
		if (old != null)
			old.removeListener(p);
		this.radio.put(p, radio);
		if (radio != null)
			radio.addListener(p);
	}

	public void updateRadioItem(Player player) {
		if (!player.getInventory().contains(Material.JUKEBOX))
			player.getInventory().addItem(new ItemStack(Material.JUKEBOX));

		ItemStack item = player.getInventory().getItem(
				player.getInventory().first(Material.JUKEBOX));
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add("§a" + language.get(player, "radiolore1"));
		lore.add("§a" + language.get(player, "radiolore2"));
		meta.setLore(lore);

		if (radio.containsKey(player) && radio.get(player) != null) {
			PublicRadio rad = radio.get(player);
			meta.setDisplayName("§2" + language.get(player, "radioname")
					+ " §a|§2 " + rad.getName() + " §a|§2 "
					+ rad.getSongPlaying().getTitle());
		} else
			meta.setDisplayName("§4" + language.get(player, "radioname")
					+ ": §c" + language.get(player, "disabled"));

		item.setItemMeta(meta);
	}

}
