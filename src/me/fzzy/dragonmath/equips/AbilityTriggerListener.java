package me.fzzy.dragonmath.equips;

import me.fzzy.dragonmath.util.Distance;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class AbilityTriggerListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isGliding()) {
            String speed = ((int) (event.getFrom().distance(event.getTo()) * 20 * 60)) + "";
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(speed + " blocks/minute"));
            if (player.getVelocity().length() < 0.8)
                player.setVelocity(player.getVelocity().multiply(new Vector(1.05, 1, 1.05)));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() != null) {
            Equip equip = Equip.getEquipFromItemStack(player.getInventory().getItemInMainHand());
            if (equip != null) {
                boolean beamSuccess = false;
                boolean areaSuccess = false;

                ArrayList<LivingEntity> beamList;
                ArrayList<LivingEntity> areaList;

                Ability ability = null;

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    if (equip.getRightAbility() != null)
                        ability = equip.getRightAbility();
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                    if (equip.getLeftAbility() != null)
                        ability = equip.getLeftAbility();

                if (ability != null) {
                    if (ability.getTargeting() == Targeting.AREA) {
                        areaList = areaTargeting(player, ability.getDistance());
                        if (ability.trigger(player, areaList))
                            areaSuccess = true;
                    }
                    if (ability.getTargeting() == Targeting.BEAM) {
                        beamList = beamTargeting(player, ability.getDistance());
                        if (ability.trigger(player, beamList))
                            beamSuccess = true;
                    }
                    if (beamSuccess || areaSuccess) {
                        equip.refresh();
                        event.setCancelled(true);
                    }

                    // line effect
                    if (beamSuccess) {
                        for (Location loc : Distance.getLocationsBetweenPlayerAndBlockLookingAt(player, ability.getDistance())) {
                            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 0);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<LivingEntity> areaTargeting(Player player, int area) {
        return Distance.getLivingEntitiesInRadius(player.getLocation(), area);
    }

    public ArrayList<LivingEntity> beamTargeting(Player player, int area) {
        return Distance.getLivingEntitiesBetweenPlayerAndBlockLookingAt(player, area);
    }

}
