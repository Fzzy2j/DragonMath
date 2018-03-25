package me.fzzy.dragonmath.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Distance {

    public static ArrayList<Location> generateRose(Location loc, int size, double pattern, double density) {
        ArrayList<Location> list = new ArrayList<Location>();

        // loop over 360 degrees
        for (double d = 0; d < 360; d += density) {

            // covert degrees to radians
            double t = (Math.PI / 180) * d;

            // get out pattern
            double rm = size * Math.cos(pattern * t);

            // convert the polar ccordinates back to rectangular
            double x = Math.sin(d) * rm;
            double z = Math.cos(d) * rm;

            // get the location and add it to the list
            Location rose = new Location(loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z);
            list.add(rose);
        }
        return list;
    }

    public static Location getHighestPoint(Location loc) {
        int y = 256;

        // couldn't just use the for loop value as y, not sure why, it just
        // didn't work
        for (int i = 0; i < 256; i++) {
            Location newLoc = new Location(loc.getWorld(), loc.getX(), y, loc.getZ());
            if (newLoc.getBlock().getType().isSolid()) {
                return newLoc;
            }
            y--;
        }
        return null;
    }

    public static Location getTargetLocation(Player player, int distance) {
        Location loc = player.getEyeLocation();

        Location location = null;

        // rectangular coordinates
        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        // yaw and pitch
        double yaw = Math.toRadians(loc.getYaw() + 90);
        double pitch = Math.toRadians(loc.getPitch() + 90);

        // polar coordinates
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        // loop for the distance
        for (int i = 1; i <= distance; i++) {
            // add to polar coordinates then translate them to rectangular so
            // that bukkit understands what we're telling it
            Location loc1 = new Location(player.getWorld(), px + i * x, py + i * z, pz + i * y);
            // if not solid
            if (loc1.getBlock().getType().isSolid()) {
                location = loc1;
                break;
            }
            if (i == distance) {
                if (location == null) {
                    location = loc1;
                }
            }
        }
        return location;
    }

    public static ArrayList<LivingEntity> getLivingEntitiesBetweenPlayerAndBlockLookingAt(Player player, int distance) {
        Location loc = player.getEyeLocation();

        ArrayList<LivingEntity> entitylist = new ArrayList<>();

        // rectangular coordinates
        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        // yaw and pitch
        double yaw = Math.toRadians(loc.getYaw() + 90);
        double pitch = Math.toRadians(loc.getPitch() + 90);

        // polar coordinates
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        for (int i = 1; i <= distance; i++) {
            // increment polar coordinates then convert to rectangular
            Location loc1 = new Location(player.getWorld(), px + i * x, py + i * z, pz + i * y);
            // get entities in 3 block radius
            // add them to the list
            for (LivingEntity e : Distance.getLivingEntitiesInRadius(loc1, 3)) {
                if (!e.getUniqueId().equals(player.getUniqueId()))
                    entitylist.add(e);
            }
        }
        return entitylist;
    }

    public static Location getCenterLocation(LivingEntity e) {
        switch (e.getType()) {
            case PLAYER:
            case ZOMBIE:
            case SKELETON:
            case WITHER_SKELETON:
            case CREEPER:
            case PIG_ZOMBIE:
                return e.getLocation().clone().add(0, 1, 0);
            default:
                return e.getLocation().clone().add(0, 0.5, 0);
        }
    }

    public static ArrayList<Block> getBlocksBetweenPlayerAndBlockLookingAt(Player player, int distance) {
        Location loc = player.getEyeLocation();

        ArrayList<Block> blocklist = new ArrayList<Block>();

        // rectangular coordinates
        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        // yaw and pitch
        double yaw = Math.toRadians(loc.getYaw() + 90);
        double pitch = Math.toRadians(loc.getPitch() + 90);

        // polar coordinates
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        for (int i = 1; i <= distance; i++) {
            // increment polar coordinates then convert to rectangular
            Location loc1 = new Location(player.getWorld(), px + i * x, py + i * z, pz + i * y);
            // add the block at location to list
            blocklist.add(loc1.getBlock());
        }
        return blocklist;
    }

    // same thing as above but with locations instead of blocks
    public static ArrayList<Location> getLocationsBetweenPlayerAndBlockLookingAt(Player player, int distance) {
        Location loc = player.getEyeLocation();

        ArrayList<Location> loclist = new ArrayList<Location>();

        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        double yaw = Math.toRadians(loc.getYaw() + 90);
        double pitch = Math.toRadians(loc.getPitch() + 90);

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        for (float i = 1; i <= distance; i += 0.5) {
            Location loc1 = new Location(player.getWorld(), px + i * x, py + i * z, pz + i * y);
            loclist.add(loc1);
        }
        return loclist;
    }

    public static ArrayList<Location> getLocationsBetweenLocations(Location loc, Location loc2) {

        ArrayList<Location> loclist = new ArrayList<Location>();

        int distance = (int) Math.floor(loc.distance(loc2));

        loc = Distance.lookAt(loc, loc2);

        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        double yaw = Math.toRadians(loc.getYaw() + 90);
        double pitch = Math.toRadians(loc.getPitch() + 90);

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        for (int i = 1; i <= distance; i++) {
            Location loc1 = new Location(loc.getWorld(), px + i * x, py + i * z, pz + i * y);
            loclist.add(loc1);
        }
        return loclist;
    }

    public static Location lookAt(Location loc, Location lookat) {

        loc = loc.clone();

        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        if (dx != 0) {
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        loc.setPitch((float) -Math.atan(dy / dxz));

        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

    public static ArrayList<Player> getPlayersInRadius(Location loc, double radius) {

        ArrayList<Player> players = new ArrayList<Player>();

        double i1 = loc.getX();
        double j1 = loc.getY();
        double k1 = loc.getZ();

        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius * 2, radius * 2, radius * 2)) {
            if (e instanceof Player) {
                Player player = (Player) e;
                double i2 = getCenterLocation(player).getX() + 1;
                double j2 = getCenterLocation(player).getY() + 1;
                double k2 = getCenterLocation(player).getZ() + 1;

                double ad = (i2 - i1) * (i2 - i1) + (j2 - j1) * (j2 - j1) + (k2 - k1) * (k2 - k1);

                if (ad < radius * radius) {
                    players.add(player);
                }
            }
        }
        return players;
    }

    public static ArrayList<Player> getPlayersInRadius(Player p, double radius) {

        Location loc = p.getLocation();

        ArrayList<Player> players = new ArrayList<Player>();

        double i1 = loc.getX();
        double j1 = loc.getY();
        double k1 = loc.getZ();

        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius * 2, radius * 2, radius * 2)) {
            if (e instanceof Player) {
                Player player = (Player) e;
                if (player.getUniqueId() != p.getUniqueId()) {
                    double i2 = getCenterLocation(player).getX() + 1;
                    double j2 = getCenterLocation(player).getY() + 1;
                    double k2 = getCenterLocation(player).getZ() + 1;

                    double ad = (i2 - i1) * (i2 - i1) + (j2 - j1) * (j2 - j1) + (k2 - k1) * (k2 - k1);

                    if (ad < radius * radius) {
                        players.add(player);
                    }
                }
            }
        }
        return players;
    }

    public static ArrayList<LivingEntity> getLivingEntitiesInRadius(Location loc, double radius) {

        ArrayList<LivingEntity> entities = new ArrayList<>();

        double i1 = loc.getX();
        double j1 = loc.getY();
        double k1 = loc.getZ();

        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius * 2, radius * 2, radius * 2)) {
            if (e instanceof LivingEntity) {
                if (e.getWorld().equals(loc.getWorld())) {
                    double i2 = getCenterLocation((LivingEntity) e).getX();
                    double j2 = getCenterLocation((LivingEntity) e).getY();
                    double k2 = getCenterLocation((LivingEntity) e).getZ();

                    double ad = (i2 - i1) * (i2 - i1) + (j2 - j1) * (j2 - j1) + (k2 - k1) * (k2 - k1);

                    if (ad < radius * radius) {
                        entities.add((LivingEntity) e);
                    }
                }
            }
        }
        return entities;
    }

    public static ArrayList<LivingEntity> getLivingEntitiesInRadius(Entity entity, double radius) {

        Location loc = entity.getLocation();

        ArrayList<LivingEntity> entities = new ArrayList<>();

        double i1 = loc.getX();
        double j1 = loc.getY();
        double k1 = loc.getZ();

        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius * 2, radius * 2, radius * 2)) {
            if (e instanceof LivingEntity) {
                if (e.getUniqueId() != entity.getUniqueId()) {
                    if (e.getWorld().equals(loc.getWorld())) {
                        double i2 = getCenterLocation((LivingEntity) e).getX();
                        double j2 = getCenterLocation((LivingEntity) e).getY();
                        double k2 = getCenterLocation((LivingEntity) e).getZ();

                        double ad = (i2 - i1) * (i2 - i1) + (j2 - j1) * (j2 - j1) + (k2 - k1) * (k2 - k1);

                        if (ad < radius * radius) {
                            entities.add((LivingEntity) e);
                        }
                    }
                }
            }
        }
        return entities;
    }

    public static Entity getClosestEntity(Entity e, double radius) {

        double minimalDistance = Math.pow(radius, 2);
        double curDist;
        Entity closest = null;
        for (Entity p : e.getWorld().getEntities()) {
            if (!p.equals(e)) {
                curDist = e.getLocation().distanceSquared(p.getLocation());
                if (curDist < minimalDistance) {
                    minimalDistance = curDist;
                    closest = p;
                }
            }
        }
        return closest;
    }

    public static Entity getClosestEntity(Location loc, double radius) {

        double minimalDistance = Math.pow(radius, 2);
        double curDist;
        Entity closest = null;
        for (Entity p : loc.getWorld().getEntities()) {
            curDist = loc.distanceSquared(p.getLocation());
            if (curDist < minimalDistance) {
                minimalDistance = curDist;
                closest = p;
            }
        }
        return closest;
    }

    public static Player getClosestPlayer(Entity e, double radius) {

        double minimalDistance = Math.pow(radius, 2);
        double curDist;
        Player closest = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            curDist = e.getLocation().distanceSquared(p.getLocation());
            if (curDist < minimalDistance) {
                minimalDistance = curDist;
                closest = p;
            }
        }
        return closest;
    }

    public static Player getClosestPlayer(Location loc, double radius) {

        double minimalDistance = Math.pow(radius, 2);
        double curDist;
        Player closest = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            curDist = loc.distanceSquared(p.getLocation());
            if (curDist < minimalDistance) {
                minimalDistance = curDist;
                closest = p;
            }
        }
        return closest;
    }

}
