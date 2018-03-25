package me.fzzy.dragonmath.equips;

import me.fzzy.dragonmath.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Equip {

    public static final String SPACE = "  ";

    private Ability leftHand;
    private Ability rightHand;

    private String name;
    private int level;
    private ItemStack item;

    public Ability getRightAbility() {
        return rightHand;
    }

    public Ability getLeftAbility() {
        return leftHand;
    }

    public void setRightAbility(Ability rightHand) {
        this.rightHand = rightHand;
    }

    public void setLeftAbility(Ability leftHand) {
        this.leftHand = leftHand;
    }

    public ItemStack getItemStack() {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + name);
        ArrayList<String> lore = new ArrayList<>();

        lore.add(ChatColor.GOLD + "Level: " + ChatColor.BOLD + level);
        lore.add("");

        if (rightHand != null) {
            lore.add(ChatColor.WHITE + "Right: " + ChatColor.AQUA + StringUtil.capitalizeFirst(rightHand.getTargeting().name()));
            for (String s : rightHand.getAttributeStrings()) {
                lore.add(SPACE + s);
            }
        }
        if (leftHand != null) {
            lore.add(ChatColor.WHITE + "Left: " + ChatColor.AQUA + StringUtil.capitalizeFirst(leftHand.getTargeting().name()));
            for (String s : leftHand.getAttributeStrings()) {
                lore.add(SPACE + s);
            }
        }

        meta.setLore(lore);
        meta.setLocalizedName(serialize());
        item.setItemMeta(meta);
        item.setAmount(1);
        return item;
    }

    public void refresh() {
        ItemMeta meta = item.getItemMeta();
        meta.setLocalizedName(serialize());
        item.setItemMeta(meta);
    }

    public String serialize() {
        StringBuilder serial = new StringBuilder();

        serial.append(level).append("%%");
        serial.append(name).append("%%");
        serial.append(item.getType().name()).append("%%");
        Iterator<Map.Entry<Enchantment, Integer>> iter = item.getEnchantments().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Enchantment, Integer> ench = iter.next();
            serial.append(ench.getKey().getName()).append(";;").append(ench.getValue());
            if (iter.hasNext())
                serial.append("^^");
        }

        if (rightHand != null)
            serial.append(rightHand.serialize());
        else
            serial.append(" ");
        serial.append("%%");
        if (leftHand != null)
            serial.append(leftHand.serialize());
        else
        serial.append(" ");
        return serial.toString();
    }

    public static Equip getEquipFromItemStack(ItemStack i) {
        try {
            ItemMeta meta = i.getItemMeta();
            String serial = meta.getLocalizedName();
            String[] data = serial.split("%%");
            int level = Integer.parseInt(data[0]);
            String name = data[1];
            Builder builder = Equip.builder(i, name, level);
            if (data[4].length() > 1)
                builder.rightAbility(Ability.getFromSerial(data[4]));
            if (data[5].length() > 1)
                builder.leftAbility(Ability.getFromSerial(data[5]));
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    public static Equip getEquipFromSerial(String serial) {
        try {
            String[] data = serial.split("%%");
            int level = Integer.parseInt(data[0]);
            String name = data[1];
            ItemStack item = new ItemStack(Material.valueOf(data[2]));
            String[] enchantNames = data[3].split("\\^\\^");
            for (String s : enchantNames) {
                String[] ench = s.split(";;");
                item.addEnchantment(Enchantment.getByName(ench[0]), Integer.parseInt(ench[1]));
            }
            Builder builder = Equip.builder(item, name, level);
            if (data[4].length() > 1)
                builder.rightAbility(Ability.getFromSerial(data[4]));
            if (data[5].length() > 1)
                builder.leftAbility(Ability.getFromSerial(data[5]));
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    Equip(Ability leftHand, Ability rightHand, String name, int level, ItemStack item) {
        this.leftHand = leftHand;
        this.rightHand = rightHand;
        this.name = name;
        this.level = level;
        this.item = item;
    }

    public static Builder builder(ItemStack item, String name, int level) {
        return new Builder(item, name, level);
    }

    public static final class Builder {
        Ability rightHand;
        Ability leftHand;
        String name;
        int level;
        ItemStack item;

        public Builder(ItemStack item, String name, int level) {
            this.item = item;
            this.name = name;
            this.level = level;
        }

        public Builder leftAbility(Ability ability) {
            this.leftHand = ability;
            return this;
        }

        public Builder rightAbility(Ability ability) {
            this.rightHand = ability;
            return this;
        }

        public Equip build() {
            return new Equip(leftHand, rightHand, name, level, item);
        }
    }

}
