package fr.isax.killpoints;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    private HashMap<UUID, Integer> points = new HashMap<>();

    @Override
    public void onEnable() {
        System.out.println("KillPoints activated on the server !");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("points").setExecutor(this);
        getCommand("shop").setExecutor(this);
    }

    @Override
    public void onDisable() {
        System.out.println("KillPoints desactivated on the server !");
        System.out.println("Goodbye !");
    }

    // 🔪 Kill = +10 points
    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            points.put(killer.getUniqueId(),
                    points.getOrDefault(killer.getUniqueId(), 0) + 10);
            killer.sendMessage("§a+10 points !");
        }
    }

    // 📊 Commandes
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("points")) {
            int pts = points.getOrDefault(p.getUniqueId(), 0);
            p.sendMessage("§eTu as " + pts + " points !");
        }

        if (cmd.getName().equalsIgnoreCase("shop")) {
            Inventory inv = Bukkit.createInventory(null, 9, "§6Shop Armure");

            inv.setItem(0, new ItemStack(Material.DIAMOND_HELMET));
            inv.setItem(1, new ItemStack(Material.DIAMOND_CHESTPLATE));
            inv.setItem(2, new ItemStack(Material.DIAMOND_LEGGINGS));
            inv.setItem(3, new ItemStack(Material.DIAMOND_BOOTS));

            p.openInventory(inv);
        }

        return true;
    }

    // 🛒 Achat
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("§6Shop Armure")) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item == null) return;

        int price = 0;

        switch (item.getType()) {
            case DIAMOND_HELMET: price = 100; break;
            case DIAMOND_CHESTPLATE: price = 200; break;
            case DIAMOND_LEGGINGS: price = 150; break;
            case DIAMOND_BOOTS: price = 80; break;
        }

        int pts = points.getOrDefault(p.getUniqueId(), 0);

        if (pts >= price) {
            points.put(p.getUniqueId(), pts - price);
            p.getInventory().addItem(new ItemStack(item.getType()));
            p.sendMessage("§aAchat réussi !");
        } else {
            p.sendMessage("§cPas assez de points !");
        }
    }
}