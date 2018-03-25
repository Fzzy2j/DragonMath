package me.fzzy.dragonmath;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.registry.WorldData;
import me.fzzy.dragonmath.dungeons.Dungeon;
import me.fzzy.dragonmath.dungeons.room.Room;
import me.fzzy.dragonmath.dungeons.room.RoomType;
import me.fzzy.dragonmath.dungeons.room.StandardRoom;
import me.fzzy.dragonmath.equips.*;
import me.fzzy.dragonmath.util.*;
import me.fzzy.dragonmath.util.exception.ImproperEndRoomException;
import me.fzzy.dragonmath.util.exception.InvalidRoomSizeException;
import me.fzzy.dragonmath.util.exception.MisalignedDoorwayException;
import me.fzzy.dragonmath.util.exception.MissingDoorwayException;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DragonMath extends JavaPlugin implements Listener {

    public static DragonMath instance;

    public static WorldEditPlugin we;

    public ArrayList<Dungeon> dungeons;

    @Override
    public void onEnable() {
        instance = this;

        PluginManager pm = Bukkit.getPluginManager();
        we = (WorldEditPlugin) pm.getPlugin("WorldEdit");
        if (we == null) {
            System.out.println("WorldEdit is required for this plugin to function!");
            pm.disablePlugin(this);
            return;
        }
        pm.registerEvents(new AbilityTriggerListener(), this);
        loadDungeons();
    }

    public void loadDungeons() {
        dungeons = new ArrayList<>();
        File dir = new File(this.getDataFolder().getAbsolutePath());
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                Yaml dungeonData = new Yaml(file.getAbsolutePath() + File.separator + "data");
                if (!dungeonData.contains("roomSize"))
                    continue;
                Dungeon dungeon = new Dungeon(file.getName(), dungeonData.getInteger("roomSize"));
                dungeons.add(dungeon);
            }
        }
    }

    private HashMap<UUID, Equip.Builder> builders;

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
        if (label.equalsIgnoreCase("equip")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("effecttypes")) {
                    for (PotionEffectType type : PotionEffectType.values()) {
                        if (type != null)
                            player.sendMessage(ChatColor.YELLOW + type.getName());
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("particletypes")) {
                    for (Particle type : Particle.values()) {
                        if (type != null)
                            player.sendMessage(ChatColor.YELLOW + type.name());
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("targettypes")) {
                    for (Target type : Target.values()) {
                        if (type != null)
                            player.sendMessage(ChatColor.YELLOW + type.name());
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    String name = args[1];
                    int level = Integer.parseInt(args[2]);
                    Equip e = Equip.builder(player.getInventory().getItemInMainHand(), ChatColor.translateAlternateColorCodes('&', name), level).build();
                    player.getInventory().setItemInMainHand(e.getItemStack());
                    return true;
                }
                if (args[0].equalsIgnoreCase("ability")) {
                    Equip e = Equip.getEquipFromItemStack(player.getInventory().getItemInMainHand());
                    player.sendMessage("1");
                    if (e != null) {
                        Ability ability = null;
                        player.sendMessage("2");
                        if (args[1].equalsIgnoreCase("left")) {
                            if (e.getLeftAbility() == null)
                                ability = Ability.builder().build();
                            else
                                ability = e.getLeftAbility();
                        } else if (args[1].equalsIgnoreCase("right")) {
                            if (e.getLeftAbility() == null)
                                ability = Ability.builder().build();
                            else
                                ability = e.getLeftAbility();
                        } else {
                            // not right or left
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("heal")) {
                            player.sendMessage("3");
                            int amt = Integer.parseInt(args[3]);
                            Target target = Target.valueOf(args[4].toUpperCase());
                            Particle particle = Particle.valueOf(args[5].toUpperCase());
                            int particleAmt = Integer.parseInt(args[6]);
                            Attribute attribute = Attribute.builder().heal(amt, target).particle(particle, particleAmt).build();
                            ability.addAttribute(attribute);
                        }
                        if (args[2].equalsIgnoreCase("damage")) {
                            int amt = Integer.parseInt(args[3]);
                            Target target = Target.valueOf(args[4].toUpperCase());
                            Particle particle = Particle.valueOf(args[5].toUpperCase());
                            int particleAmt = Integer.parseInt(args[6]);
                            Attribute attribute = Attribute.builder().damage(amt, target).particle(particle, particleAmt).build();
                            ability.addAttribute(attribute);
                        }
                        if (args[2].equalsIgnoreCase("effect")) {
                            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                            Target target = Target.valueOf(args[6].toUpperCase());
                            Particle particle = Particle.valueOf(args[7].toUpperCase());
                            int particleAmt = Integer.parseInt(args[8]);
                            Attribute attribute = Attribute.builder().effect(effect, target).particle(particle, particleAmt).build();
                            ability.addAttribute(attribute);
                        }
                    }
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can run this command!");
                return true;
            }
        }
        if (label.equalsIgnoreCase("dungeon")) {
            if (args[0].equalsIgnoreCase("generate")) {
                Player player = (Player) sender;
                getDungeon("tree").getNewSession(player, null, null, null);
                return true;
            }
            if (args[0].equalsIgnoreCase("equip")) {
                /*ItemStack i = new ItemStack(Material.BLAZE_ROD);
                ItemStack mace = new ItemStack(Material.IRON_SPADE);

                Attribute r1 = Attribute.builder()
                        .heal(10, Target.FRIENDLY)
                        .particle(Particle.HEART, 10)
                        .build();
                Ability right = Ability.builder()
                        .cooldown(10)
                        .targeting(Targeting.BEAM)
                        .distance(20)
                        .with(r1)
                        .build();

                Attribute l1 = Attribute.builder()
                        .damage(100, Target.ENEMY)
                        .particle(Particle.CLOUD, 10)
                        .build();
                Ability left = Ability.builder()
                        .cooldown(100)
                        .targeting(Targeting.BEAM)
                        .distance(50)
                        .with(l1)
                        .build();

                Equip e = Equip.builder(i, "Wand", 10)
                        .leftAbility(left)
                        .rightAbility(right)
                        .build();

                player.getInventory().addItem(e.getItemStack());

                //MACE

                Attribute macer1 = Attribute.builder()
                        .heal(10, Target.FRIENDLY)
                        .particle(Particle.HEART, 10)
                        .build();
                Attribute macer2 = Attribute.builder()
                        .damage(10, Target.ENEMY)
                        .particle(Particle.REDSTONE, 10)
                        .build();
                Ability maceright = Ability.builder()
                        .cooldown(10)
                        .targeting(Targeting.AREA)
                        .distance(20)
                        .with(macer1)
                        .with(macer2)
                        .build();

                mace.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                Equip maceequip = Equip.builder(mace, "Mace", 10)
                        .rightAbility(maceright)
                        .build();

                player.getInventory().addItem(maceequip.getItemStack());*/
            }
            for (Dungeon dungeon : dungeons) {
                if (args[0].equalsIgnoreCase(dungeon.getName())) {
                    if (args[1].equalsIgnoreCase("add")) {

                        // Add new room to dungeon
                        if (args[2].equalsIgnoreCase("room")) {
                            Player player = (Player) sender;
                            RoomType roomType = null;
                            if (args[3].equalsIgnoreCase("standard")) {
                                roomType = RoomType.STANDARD;
                            } else if (args[3].equalsIgnoreCase("end")) {
                                roomType = RoomType.END;
                            } else {
                                player.sendMessage(ChatColor.RED + "Room type doesnt exist! must be standard or end!");
                                return true;
                            }

                            Room room;
                            try {
                                room = dungeon.getNewRoom(player, roomType);
                            } catch (MisalignedDoorwayException e) {
                                player.sendMessage(ChatColor.RED + "Add room failed! Doorways are not aligned!");
                                return true;
                            } catch (MissingDoorwayException e) {
                                player.sendMessage(ChatColor.RED + "This room has no doorways marked!");
                                return true;
                            } catch (InvalidRoomSizeException e) {
                                player.sendMessage(ChatColor.RED + "Your clipboard size must match the size of the rooms in the dungeon!");
                                player.sendMessage(ChatColor.RED + "Dungeon " + dungeon.getName() + " requires rooms of size " + dungeon.getRoomSize());
                                return true;
                            } catch (ImproperEndRoomException e) {
                                player.sendMessage(ChatColor.RED + "End room must be a dead end!");
                                return true;
                            } catch (IncompleteRegionException e) {
                                player.sendMessage(ChatColor.RED + "No selection to save!");
                                return true;
                            }
                            if (room == null) {
                                player.sendMessage(ChatColor.RED + "Could not save room! (did you copy your selection?)");
                                return true;
                            }

                            //Success
                            player.sendMessage(ChatColor.AQUA + roomType.name() + " room saved with id: " + room.getId());
                            if (room instanceof StandardRoom) {
                                StandardRoom standard = (StandardRoom) room;
                                player.sendMessage(ChatColor.AQUA + "Layout: " + standard.getLayoutType());
                            }
                        }
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("delete")) {

                    }
                    break;
                }
            }
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("create")) {
                    String name = args[0].toLowerCase();
                    int roomSize;
                    try {
                        roomSize = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + args[2] + " is not a number!");
                        return true;
                    }

                    // Returns null if the dungeon exists, otherwise returns a newly created dungeon
                    Dungeon dungeon = getNewDungeon(name, roomSize);

                    if (dungeon == null) {
                        sender.sendMessage(ChatColor.RED + "Dungeon already exists!");
                        return true;
                    }
                    sender.sendMessage(ChatColor.AQUA + "New dungeon created with the name: " + dungeon.getName());
                    sender.sendMessage(ChatColor.YELLOW + "For it to function you must add the following rooms:");
                    sender.sendMessage(ChatColor.YELLOW + "1 Type T room");
                    sender.sendMessage(ChatColor.YELLOW + "1 Type elbow room");
                    sender.sendMessage(ChatColor.YELLOW + "1 Type hall room");
                    sender.sendMessage(ChatColor.YELLOW + "1 Type four room");
                    sender.sendMessage(ChatColor.YELLOW + "1 Type dead_end room");
                    sender.sendMessage(ChatColor.YELLOW + "1 End room");
                    sender.sendMessage(ChatColor.AQUA + "Use /dungeon help for commands");
                    return true;
                }
            }
        }
        Player player = (Player) sender;
        CommandListHelper.displayHelp(0, player);
        return false;
    }

    public Dungeon getDungeon(String name) {
        for (Dungeon dungeon : dungeons) {
            if (name.equalsIgnoreCase(dungeon.getName()))
                return dungeon;
        }
        return null;
    }

    public Dungeon getNewDungeon(String name, int roomSize) {
        for (Dungeon dungeon : dungeons) {
            if (name.equalsIgnoreCase(dungeon.getName()))
                return null;
        }
        Dungeon dungeon = new Dungeon(name, roomSize);
        dungeons.add(dungeon);
        return dungeon;
    }

}
