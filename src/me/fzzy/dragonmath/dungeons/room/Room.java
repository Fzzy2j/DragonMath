package me.fzzy.dragonmath.dungeons.room;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.fzzy.dragonmath.DragonMath;
import me.fzzy.dragonmath.dungeons.Dungeon;
import me.fzzy.dragonmath.util.*;
import me.fzzy.dragonmath.util.exception.InvalidRoomSizeException;
import me.fzzy.dragonmath.util.exception.MisalignedDoorwayException;
import me.fzzy.dragonmath.util.exception.MissingDoorwayException;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class Room {

    protected Dungeon dungeon;
    protected int size;

    protected int id;

    protected int doorwayOffset;
    protected RoomSchematic schematic;
    protected Orientation orientation;
    protected RoomType type;

    Room(int id, RoomType type, Dungeon dungeon, RoomSchematic schematic) throws InvalidRoomSizeException, MisalignedDoorwayException, MissingDoorwayException {
        this.type = type;
        this.id = id;
        this.dungeon = dungeon;
        this.schematic = schematic;

        Clipboard clipboard = schematic.getClipboard(new BukkitWorld(Bukkit.getWorlds().get(0)).getWorldData());
        if (clipboard.getDimensions().getBlockX() != dungeon.getRoomSize() || clipboard.getDimensions().getBlockZ() != dungeon.getRoomSize()) {
            throw new InvalidRoomSizeException("Attempted to instantiate room with id " + id + " with a schematic of wrong size!");
        }

        int doorwayHeight = -1;
        Vector v1 = clipboard.getRegion().getMinimumPoint();
        Vector v2 = clipboard.getRegion().getMaximumPoint();
        int point1 = (int) Math.floor((dungeon.getRoomSize() - 1) / 2d);
        int point2 = (int) Math.ceil((dungeon.getRoomSize() - 1) / 2d);
        boolean hasNorth = false;
        boolean hasEast = false;
        boolean hasSouth = false;
        boolean hasWest = false;
        int highZ = Math.max(v1.getBlockZ(), v2.getBlockZ());
        int highX = Math.max(v1.getBlockX(), v2.getBlockX());
        int highY = Math.max(v1.getBlockY(), v2.getBlockY());
        int lowZ = Math.min(v1.getBlockZ(), v2.getBlockZ());
        int lowX = Math.min(v1.getBlockX(), v2.getBlockX());
        int lowY = Math.min(v1.getBlockY(), v2.getBlockY());
        for (int y = lowY; y <= highY; y++) {
            BaseBlock[] north = {
                    clipboard.getBlock(new Vector(point1 + lowX, y, lowZ)),
                    clipboard.getBlock(new Vector(point2 + lowX, y, lowZ))
            };
            BaseBlock[] east = {
                    clipboard.getBlock(new Vector(highX, y, point1 + lowZ)),
                    clipboard.getBlock(new Vector(highX, y, point2 + lowZ))
            };
            BaseBlock[] south = {
                    clipboard.getBlock(new Vector(point1 + lowX, y, highZ)),
                    clipboard.getBlock(new Vector(point2 + lowX, y, highZ))
            };
            BaseBlock[] west = {
                    clipboard.getBlock(new Vector(lowX, y, point1 + lowZ)),
                    clipboard.getBlock(new Vector(lowX, y, point2 + lowZ))
            };

            for (BaseBlock norths : north) {
                if (norths.getId() == 63) {
                    if (norths.getNbtData() != null) {
                        if (norths.getNbtData().getString("Text2").contains("[Doorway]")) {
                            hasNorth = true;
                            if (doorwayHeight == -1)
                                doorwayHeight = y;
                            if (y != doorwayHeight)
                                throw new MisalignedDoorwayException("North doorway is not aligned!");
                        }
                    }
                }
            }
            for (BaseBlock easts : east) {
                if (easts.getId() == 63) {
                    if (easts.getNbtData() != null) {
                        if (easts.getNbtData().getString("Text2").contains("[Doorway]")) {
                            hasEast = true;
                            if (doorwayHeight == -1)
                                doorwayHeight = y;
                            if (y != doorwayHeight)
                                throw new MisalignedDoorwayException("East doorway is not aligned!");
                        }
                    }
                }
            }
            for (BaseBlock souths : south) {
                if (souths.getId() == 63) {
                    if (souths.getNbtData() != null) {
                        if (souths.getNbtData().getString("Text2").contains("[Doorway]")) {
                            hasSouth = true;
                            if (doorwayHeight == -1)
                                doorwayHeight = y;
                            if (y != doorwayHeight)
                                throw new MisalignedDoorwayException("South doorway is not aligned!");
                        }
                    }
                }
            }
            for (BaseBlock wests : west) {
                if (wests.getId() == 63) {
                    if (wests.getNbtData() != null) {
                        if (wests.getNbtData().getString("Text2").contains("[Doorway]")) {
                            hasWest = true;
                            if (doorwayHeight == -1)
                                doorwayHeight = y;
                            if (y != doorwayHeight)
                                throw new MisalignedDoorwayException("West doorway is not aligned!");
                        }
                    }
                }
            }
        }
        doorwayOffset = doorwayHeight - lowY;
        orientation = new Orientation(hasNorth, hasEast, hasSouth, hasWest);
        if (orientation.count() == 0)
            throw new MissingDoorwayException("room has no doorways!");
    }

    public int getDoorwayOffset() {
        return doorwayOffset;
    }

    public RoomSchematic getSchematic() {
        return schematic;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public EditSession paste(Location location, int rotation) {
        return schematic.paste(location.clone().subtract(0, doorwayOffset, 0), rotation);
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public int getSize() {
        return size;
    }

    public RoomType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getSchematicName() {
        return "room_" + id + ".schematic";
    }

    public abstract String getPath();

}
