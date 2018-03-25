package me.fzzy.dragonmath.dungeons;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import javafx.util.Pair;
import me.fzzy.dragonmath.DragonMath;
import me.fzzy.dragonmath.dungeons.room.*;
import me.fzzy.dragonmath.dungeons.session.DungeonSession;
import me.fzzy.dragonmath.dungeons.session.MazeGenerator;
import me.fzzy.dragonmath.equips.Equip;
import me.fzzy.dragonmath.util.*;
import me.fzzy.dragonmath.util.exception.ImproperEndRoomException;
import me.fzzy.dragonmath.util.exception.InvalidRoomSizeException;
import me.fzzy.dragonmath.util.exception.MisalignedDoorwayException;
import me.fzzy.dragonmath.util.exception.MissingDoorwayException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dungeon {

    private ArrayList<StandardRoom> rooms;
    private ArrayList<EndRoom> endRooms;

    private ArrayList<Equip> equips;

    private String name;

    private int roomSize;

    private Yaml data;
    private String path;

    public Dungeon(String name, int roomSize) {
        this.roomSize = roomSize;

        name = name.toLowerCase();
        this.name = name;

        path = DragonMath.instance.getDataFolder().getAbsolutePath() + File.separator + name + File.separator;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        data = new Yaml(path + "data");

        data.set("roomSize", roomSize);
        data.save();

        loadRooms();
        loadEquips();
    }

    public DungeonSession getNewSession(Player player1, Player player2, Player player3, Player player4) {
        return new DungeonSession(this, player1, player2, player3, player4);
    }

    public void loadEquips() {
        equips = new ArrayList<>();

        List<String> list = data.getStringList("equips");
        for (String s : list) {
            equips.add(Equip.getEquipFromSerial(s));
        }
    }

    public void addEquip(Equip equip) {
        data.addToStringList("equips", equip.serialize());
        data.save();
    }

    public void loadRooms() {
        rooms = new ArrayList<>();
        endRooms = new ArrayList<>();

        File standardRoomFile = new File(path + "rooms");
        File[] standardRoomList = standardRoomFile.listFiles();
        if (standardRoomList != null) {
            for (File standardFile : standardRoomList) {
                RoomSchematic schematic = new RoomSchematic(standardFile);
                if (schematic.exists()) {
                    if (standardFile.getName().startsWith("room_") && standardFile.getName().endsWith(".schematic")) {
                        int fileId = Integer.parseInt(standardFile.getName().substring(5, standardFile.getName().length() - 10));
                        try {
                            rooms.add(new StandardRoom(fileId, this, schematic));
                        } catch (InvalidRoomSizeException | MisalignedDoorwayException | MissingDoorwayException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        File endRoomFile = new File(path + "endRooms");
        File[] endRoomList = endRoomFile.listFiles();
        if (endRoomList != null) {
            for (File endFile : endRoomList) {
                RoomSchematic schematic = new RoomSchematic(endFile);
                if (schematic.exists()) {
                    if (endFile.getName().startsWith("room_") && endFile.getName().endsWith(".schematic")) {
                        int fileId = Integer.parseInt(endFile.getName().substring(5, endFile.getName().length() - 10));
                        try {
                            endRooms.add(new EndRoom(fileId, this, schematic));
                        } catch (InvalidRoomSizeException | MisalignedDoorwayException | MissingDoorwayException | ImproperEndRoomException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public StandardRoom getStandardRoom(int id) {
        for (StandardRoom room : rooms) {
            if (room.getId() == id)
                return room;
        }
        return null;
    }

    public ArrayList<StandardRoom> getRooms() {
        return rooms;
    }

    public EndRoom getEndRoom(int id) {
        for (EndRoom room : endRooms) {
            if (room.getId() == id)
                return room;
        }
        return null;
    }

    public Room getNewRoom(Player player, RoomType type) throws InvalidRoomSizeException, MisalignedDoorwayException, MissingDoorwayException, ImproperEndRoomException, IncompleteRegionException {
        int id = -1;

        Region region = DragonMath.we.getSession(player).getWorldSelection();
        if (region.getLength() != roomSize || region.getWidth() != roomSize) {
            throw new InvalidRoomSizeException("Attempted to save new room with a selection of wrong size!");
        }

        // Get paths and determine room id
        String path = "";
        if (type == RoomType.STANDARD)
            path = DragonMath.instance.getDataFolder().getAbsolutePath() + File.separator + name + File.separator + "rooms";
        if (type == RoomType.END)
            path = DragonMath.instance.getDataFolder().getAbsolutePath() + File.separator + name + File.separator + "endRooms";

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        // Make sure the id of the new room is one higher than any existing rooms
        File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            if (allFiles.length == 0)
                id = 0;
            for (int i = 0; i < allFiles.length; i++) {
                if (allFiles[i].isFile()) {

                    // Fish out the number in the file name
                    if (allFiles[i].getName().startsWith("room_") && allFiles[i].getName().endsWith(".schematic")) {
                        int fileId = Integer.parseInt(allFiles[i].getName().substring(5, allFiles[i].getName().length() - 10));

                        if (fileId >= id)
                            id = fileId + 1;
                    }
                }
            }
        } else
            id = 0;

        File file = new File(path + File.separator + "room_" + id + ".schematic");
        RoomSchematic schematic = new RoomSchematic(file);
        schematic.save(player, this);
        if (type == RoomType.STANDARD) {
            StandardRoom room;
            try {
                room = new StandardRoom(id, this, schematic);
            } catch (InvalidRoomSizeException | MisalignedDoorwayException | MissingDoorwayException e) {
                schematic.delete();
                throw e;
            }
            rooms.add(room);
            return room;
        }
        if (type == RoomType.END) {
            EndRoom room;
            try {
                room = new EndRoom(id, this, schematic);
            } catch (InvalidRoomSizeException | MisalignedDoorwayException | ImproperEndRoomException | MissingDoorwayException e) {
                schematic.delete();
                throw e;
            }
            endRooms.add(room);
            return room;
        }
        return null;
    }

    public boolean isDungeonReadyForPlay() {
        boolean ready = true;

        // At least 1 of each type of room must exist
        boolean t = false;
        boolean elbow = false;
        boolean hall = false;
        boolean four = false;
        boolean dead_end = false;
        for (StandardRoom room : rooms) {
            switch (room.getLayoutType()) {
                case T:
                    t = true;
                    break;
                case ELBOW:
                    elbow = true;
                    break;
                case HALL:
                    hall = true;
                    break;
                case FOUR:
                    four = true;
                    break;
                case DEAD_END:
                    dead_end = true;
                    break;
            }
        }
        if (!t || !elbow || !hall || !four || !dead_end)
            ready = false;

        return ready;
    }

}
