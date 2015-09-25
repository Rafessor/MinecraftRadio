package de.rafessor.old.radio;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.xxmicloxx.NoteBlockAPI.SongEndEvent;

import de.rafessor.api.Language;

public class RadioListener implements Listener {

	private Language language;
	private RadioManager manager;
	private OldRadio plugin;

	public RadioListener(RadioManager manager, OldRadio plugin)
			throws IOException {
		this.manager = manager;
		this.plugin = plugin;
		this.language = plugin.getLanguage();

		plugin.getPlugin().getServer().getPluginManager()
				.registerEvents(this, plugin.getPlugin());
	}

	@EventHandler
	public void onSongEnd(SongEndEvent e) {
		for (PublicRadio radio : manager.getRadios())
			if (e.getSongPlayer() == radio.getSongPlayer())
				radio.nextSong();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPermission("radio.own"))
			return;
		if (p.getItemInHand() != null
				&& p.getItemInHand().getType() == Material.JUKEBOX) {
			PublicRadio radio = manager.getRadio(p);
			if (e.getAction() == Action.LEFT_CLICK_AIR
					|| e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (radio == null)
					radio = manager.getRadios().get(0);
				else
					radio = null;
			} else if ((e.getAction() == Action.RIGHT_CLICK_AIR || e
					.getAction() == Action.RIGHT_CLICK_BLOCK) && radio != null) {
				int index = manager.getRadios().indexOf(radio);

				radio = manager.getRadios().get(
						(index + 1) % manager.getRadios().size());
			}
			manager.setRadio(p, radio);
			manager.updateRadioItem(p);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		Player p = e.getPlayer();

		FileConfiguration cfg = plugin.getPlugin().getConfig();
		if (cfg.contains("lang." + p.getName()))
			language.getLanguage().put(p, cfg.getString("lang." + p.getName()));

		if (p.hasPermission("radio.own")) {
			if (!p.getInventory().contains(Material.JUKEBOX))
				p.getInventory().addItem(new ItemStack(Material.JUKEBOX));
			manager.updateRadioItem(p);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		manager.setRadio(e.getPlayer(), null);
	}
}
