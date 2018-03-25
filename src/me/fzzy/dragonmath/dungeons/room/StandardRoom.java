package me.fzzy.dragonmath.dungeons.room;

import me.fzzy.dragonmath.DragonMath;
import me.fzzy.dragonmath.dungeons.Dungeon;
import me.fzzy.dragonmath.util.*;
import me.fzzy.dragonmath.util.exception.InvalidRoomSizeException;
import me.fzzy.dragonmath.util.exception.MisalignedDoorwayException;
import me.fzzy.dragonmath.util.exception.MissingDoorwayException;

import java.io.File;

public class StandardRoom extends Room {

    private StandardRoomType layoutType;

    public StandardRoom(int id, Dungeon dungeon, RoomSchematic schematic) throws InvalidRoomSizeException, MisalignedDoorwayException, MissingDoorwayException {
        super(id, RoomType.STANDARD, dungeon, schematic);
        layoutType = getLayoutType(orientation);
    }

    public StandardRoomType getLayoutType() {
        return layoutType;
    }

    @Override
    public String getPath() {
        return DragonMath.instance.getDataFolder().getAbsolutePath() + File.separator + dungeon.getName() + File.separator + "rooms" + File.separator;
    }

    public static StandardRoomType getLayoutType(Orientation orientation) {
        int amtDoors = orientation.count();
        if (amtDoors == 1) {
            return StandardRoomType.DEAD_END;
        } else if (amtDoors == 2) {
            if (orientation.hasAdjacent())
                return StandardRoomType.ELBOW;
            else
                return StandardRoomType.HALL;
        } else if (amtDoors == 3) {
            return StandardRoomType.T;
        } else if (amtDoors == 4) {
            return StandardRoomType.FOUR;
        }
        return null;
    }
}
