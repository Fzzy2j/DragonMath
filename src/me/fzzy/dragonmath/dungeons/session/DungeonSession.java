package me.fzzy.dragonmath.dungeons.session;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import javafx.util.Pair;
import me.fzzy.dragonmath.DragonMath;
import me.fzzy.dragonmath.dungeons.Dungeon;
import me.fzzy.dragonmath.dungeons.room.Room;
import me.fzzy.dragonmath.dungeons.room.StandardRoom;
import me.fzzy.dragonmath.dungeons.room.StandardRoomType;
import me.fzzy.dragonmath.util.Orientation;
import me.fzzy.dragonmath.util.VoidWorldGenerator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DungeonSession {

    private static ArrayList<DungeonSession> sessions = new ArrayList<>();

    private Dungeon dungeon;
    private MazeGenerator maze;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private World world;

    private int id;

    public DungeonSession(Dungeon dungeon, Player player1, Player player2, Player player3, Player player4) {
        this.dungeon = dungeon;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        ArrayList<Integer> ids = new ArrayList<>();
        for (DungeonSession session : sessions) {
            ids.add(session.getId());
        }

        long start = System.currentTimeMillis();

        sessions.add(this);

        deleteSession();

        DungeonSession session = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                WorldCreator creator = WorldCreator.name("session" + getId());

                MazeGenerator mg = new MazeGenerator(5, 5, 0, 0);
                maze = mg;
                Orientation[][] result = mg.generate();

                creator.generator(new VoidWorldGenerator(session));
                creator.generateStructures(false);
                world = AsyncWorld.create(creator);

                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doWeatherCycle", "false");
                world.setGameRuleValue("doFireTick", "false");
                world.setGameRuleValue("doMobSpawning", "false");
                Pair<Integer, Integer> lastRoom = mg.getStack().get(mg.getStack().size() - 1);

                player1.teleport(world.getSpawnLocation());

                new BukkitRunnable() {
                    int x = 0;
                    @Override
                    public void run() {
                        if (x == result.length - 1)
                            this.cancel();
                        new BukkitRunnable() {
                            int z = 0;
                            Orientation[] row = result[x];
                            final int finalX = x;

                            @Override
                            public void run() {
                                if (z == row.length - 1)
                                    this.cancel();
                                Location loc = new Location(world, (finalX * dungeon.getRoomSize()), 128, (z * dungeon.getRoomSize()));
                                Orientation ori = result[finalX][z];
                                StandardRoomType layout = StandardRoom.getLayoutType(ori);
                                ArrayList<StandardRoom> available = new ArrayList<>();
                                for (StandardRoom room : dungeon.getRooms()) {
                                    if (room.getLayoutType() == layout)
                                        available.add(room);
                                }
                                StandardRoom random = available.get(new Random().nextInt(available.size()));
                                Orientation match = random.getOrientation();
                                while (!match.equals(ori)) {
                                    match.rotateNinety();
                                }
                                EditSession es = random.paste(loc, match.getTotalRotated());
                                if (z == row.length - 1 && x == result.length - 1) {
                                    es.addNotifyTask(() -> {
                                        //Finished and ready
                                    });
                                }
                                match.resetRotation();
                                z++;
                            }
                        }.runTaskTimer(DragonMath.instance, 0L, 5L);
                        x++;
                    }
                }.runTaskTimer(DragonMath.instance, 0L, maze.getzSize() * 5L);
            }
        }.runTaskLater(DragonMath.instance, 40L);

        /*for (int x = 0; x < result.length; x++) {
            Orientation[] row = result[x];
            for (int z = 0; z < row.length; z++) {
                Location loc = new Location(world, (x * dungeon.getRoomSize()), 3, (z * dungeon.getRoomSize()));
                loc.getWorld().loadChunk(loc.getChunk());
                Orientation ori = result[x][z];
                StandardRoomType layout = StandardRoom.getLayoutType(ori);
                ArrayList<StandardRoom> available = new ArrayList<>();
                for (StandardRoom room : dungeon.getRooms()) {
                    if (room.getLayoutType() == layout)
                        available.add(room);
                }
                StandardRoom random = available.get(new Random().nextInt(available.size()));
                Orientation match = random.getOrientation();
                while (!match.equals(ori)) {
                    match.rotateNinety();
                }
                random.paste(loc, match.getTotalRotated());
                match.resetRotation();
            }
        }*/
    }

    public int getId() {
        return id;
    }

    public void deleteSession() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase("session" + getId())) {

                //TODO send players somewhere
                for (Player player : world.getPlayers()) {
                    player.teleport(new Location(Bukkit.getWorld("world"), 0, 10, 0));
                }
                Bukkit.unloadWorld(world, false);
                deleteDirectory(world.getWorldFolder());
                break;
            }
        }
    }

    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

}
