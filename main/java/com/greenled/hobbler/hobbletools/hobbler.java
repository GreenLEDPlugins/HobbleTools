package com.greenled.hobbler.hobbletools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class hobbler implements Listener, CommandExecutor {
    static Hobbletools Plugin;

    public hobbler(Hobbletools hobbletools) {
        Plugin = hobbletools;


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("hobble")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.consolerun")));
            } else if (sender.hasPermission("hobble.use")) {
                Player player = (Player) sender;
                player.getInventory().addItem(getHobbleDevice());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.giveitem")));
                return true;

            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.noperms")));
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInventory(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        ItemStack inHand = p.getInventory().getItemInMainHand();
        if (e.getRightClicked().getType().equals(EntityType.PLAYER) && inHand.isSimilar(getHobbleDevice())) {
            ItemMeta itemMeta = inHand.getItemMeta();
            if (itemMeta instanceof Damageable) {
                ((Damageable) itemMeta).setDamage(getHobbleDevice().getType().getMaxDurability() / Plugin.getConfig().getInt("uses"));
                if (((Damageable) itemMeta).getDamage() >= getHobbleDevice().getType().getMaxDurability()) {
                    p.getInventory().removeItem(getHobbleDevice());
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK,1,0);
                }
            }
            inHand.setItemMeta(itemMeta);
            //p.getInventory().removeItem(getHobbleDevice());
            String name = e.getRightClicked().getName();
            Player freeze = Bukkit.getPlayerExact(name);
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 0);
            freeze.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, Plugin.getConfig().getInt("amplifier-value")));
            freeze.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.hobblesuccess")));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.confirmation")));
            return;
        }
        //p.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.error")));
        return;
    }

    public ItemStack getHobbleDevice() {
        ItemStack hobbleDevice = new ItemStack(Material.SHEARS/*matchMaterial(this.Plugin.getConfig().getString("item"))*/);
        ItemMeta meta = hobbleDevice.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_FIRE, 10, true);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.itemname")));
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.line1")));
        lore.add(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.line2")));
        lore.add(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.line3")));
        meta.setLore(lore);
        hobbleDevice.setItemMeta(meta);
        return hobbleDevice;
    }

}