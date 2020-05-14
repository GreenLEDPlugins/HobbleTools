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
import org.bukkit.inventory.EquipmentSlot;
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
            if (!(sender instanceof Player)) { //if not player
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.consolerun")));
            } else if (sender.hasPermission("hobble.use")) { // if player
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
    public void onInventory(PlayerInteractEntityEvent e) { // if player right clicks an entity
        if (e.getHand() == EquipmentSlot.OFF_HAND) {//had issues with double results so this solves that
            return; // off hand packet, ignore.
        }
        Player p = e.getPlayer();
        ItemStack inHand = p.getInventory().getItemInMainHand();
        if (e.getRightClicked().getType().equals(EntityType.PLAYER) && inHand.containsEnchantment(Enchantment.ARROW_FIRE) && inHand.getType().equals(getHobbleDevice().getType())) {
            ItemMeta itemMeta = inHand.getItemMeta();
            if (itemMeta instanceof Damageable) {
                int maxvalue = getHobbleDevice().getType().getMaxDurability(); //the max durability
                int currentvalue = ((Damageable) itemMeta).getDamage(); //the current durability
                int usemodifier = maxvalue / Plugin.getConfig().getInt("uses"); //how much to reduce by each time based on config
                ((Damageable) itemMeta).setDamage(currentvalue + usemodifier); // setting the amount of durability left
                currentvalue = ((Damageable) itemMeta).getDamage();
                if (currentvalue >= maxvalue - 10) { //due to rounding errors that I dont want to fix... I just take 10 off the max :)
                    p.getInventory().removeItem(inHand);//removes the item if it breaks
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0); //sound doesnt really work bc anvil sound is louder
                }
            }
            inHand.setItemMeta(itemMeta);
            String name = e.getRightClicked().getName();
            Player slowness = Bukkit.getPlayerExact(name); //targets the player that was clicked
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 5, 0); //play anvil break sound with volume 5
            //adds slowness effect vv
            slowness.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, Plugin.getConfig().getInt("amplifier-value")));
            //sends message to player clicked based on config vv
            slowness.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.hobblesuccess")));
            //sends message to sender/user based on config vv
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("messages.confirmation")));
        }
    }
    //this method creates the hobbling device. [WiP] Soon able to change what item
    public ItemStack getHobbleDevice() {
        ItemStack hobbleDevice = new ItemStack(Material.SHEARS/*matchMaterial(this.Plugin.getConfig().getString("item"))*/);
        ItemMeta meta = hobbleDevice.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_FIRE, 10, true); //adds enchantment to make unique like you
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.itemname")));
        //Lore is stored in an array so that's here vv
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.line1")));
        lore.add(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.line2")));
        lore.add(ChatColor.translateAlternateColorCodes('&', Plugin.getConfig().getString("lore.line3")));
        meta.setLore(lore);
        hobbleDevice.setItemMeta(meta);
        return hobbleDevice;
    }

}