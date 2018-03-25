package me.fzzy.dragonmath.equips;

import me.fzzy.dragonmath.util.Distance;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Ability {

    private ArrayList<Attribute> attributes;
    private Targeting targeting;
    private int distance;

    private long cooldown;
    private long lastTriggered;

    public boolean trigger(Player from, ArrayList<LivingEntity> targets) {
        if (System.currentTimeMillis() - lastTriggered > cooldown * (1000 / 20)) {
            for (Attribute attribute : attributes)
                attribute.trigger(from, targets);
            lastTriggered = System.currentTimeMillis();
            from.getWorld().playSound(Distance.getCenterLocation(from), Sound.ENTITY_FIREWORK_BLAST, 1, 1);
            return true;
        } else
            return false;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public int getDistance() {
        return distance;
    }

    public ArrayList<String> getAttributeStrings() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatColor.WHITE + "Cooldown: " + (cooldown / 20f) + " seconds");
        for (Attribute attribute : attributes) {
            if (attribute.hasHeal()) {
                list.add(ChatColor.YELLOW + "Heal:");
                list.add(ChatColor.YELLOW + Equip.SPACE + "Amount: " + attribute.getHealAmt());
                list.add(ChatColor.YELLOW + Equip.SPACE + "Target: " + attribute.getHealTarget().name().toLowerCase());
            }
            if (attribute.hasDamage()) {
                list.add(ChatColor.RED + "Damage:");
                list.add(ChatColor.RED + Equip.SPACE + "Amount: " + attribute.getDamageAmt());
                list.add(ChatColor.RED + Equip.SPACE + "Target: " + attribute.getDamageTarget().name().toLowerCase());
            }
            if (attribute.hasEffect()) {
                list.add(ChatColor.AQUA + "Effect:");
                list.add(ChatColor.AQUA + Equip.SPACE + "Type: " + attribute.getEffect().getType().getName().toLowerCase());
                list.add(ChatColor.AQUA + Equip.SPACE + "Target: " + attribute.getEffectTarget().name().toLowerCase());
            }
        }
        return list;
    }

    public String serialize() {
        StringBuilder serial = new StringBuilder();
        serial.append(targeting.name()).append("^^");
        serial.append(distance).append("^^");
        serial.append(cooldown).append("^^");
        serial.append(lastTriggered);
        for (Attribute attribute : attributes) {
            serial.append("^^").append(attribute.serialize());
        }
        return serial.toString();
    }

    public static Ability getFromSerial(String serial) {
        String[] data = serial.split("\\^\\^");

        Builder builder = Ability.builder()
                .targeting(Targeting.valueOf(data[0]))
                .distance(Integer.parseInt(data[1]))
                .cooldown(Long.parseLong(data[2]))
                .lastTriggered(Long.parseLong(data[3]));

        for (int i = 4; i < data.length; i++)
            builder.with(Attribute.getFromSerial(data[i]));

        return builder.build();
    }

    Ability(ArrayList<Attribute> attributes, Targeting targeting, int distance, long cooldown, long lastTriggered) {
        this.attributes = attributes;
        this.targeting = targeting;
        this.distance = distance;
        this.cooldown = cooldown;
        this.lastTriggered = lastTriggered;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        ArrayList<Attribute> attributes;
        Targeting targeting;
        int distance;
        long cooldown;
        long lastTriggered;

        public Builder() {
            this.attributes = new ArrayList<>();
            targeting = Targeting.AREA;
            distance = 5;
            cooldown = 0;
            lastTriggered = 0;
        }

        public Builder cooldown(long cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder lastTriggered(long when) {
            this.lastTriggered = when;
            return this;
        }

        public Builder targeting(Targeting targeting) {
            this.targeting = targeting;
            return this;
        }

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Builder with(Attribute attribute) {
            attributes.add(attribute);
            return this;
        }

        public Ability build() {
            return new Ability(attributes, targeting, distance, cooldown, lastTriggered);
        }

    }

}
