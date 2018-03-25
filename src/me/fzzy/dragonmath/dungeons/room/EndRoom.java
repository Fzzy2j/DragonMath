package me.fzzy.dragonmath.dungeons.room;

import me.fzzy.dragonmath.DragonMath;
import me.fzzy.dragonmath.dungeons.Dungeon;
import me.fzzy.dragonmath.util.*;
import me.fzzy.dragonmath.util.exception.ImproperEndRoomException;
import me.fzzy.dragonmath.util.exception.InvalidRoomSizeException;
import me.fzzy.dragonmath.util.exception.MisalignedDoorwayException;
import me.fzzy.dragonmath.util.exception.MissingDoorwayException;

import java.io.File;

public class EndRoom extends Room {

    public EndRoom(int id, Dungeon dungeon, RoomSchematic schematic) throws InvalidRoomSizeException, MisalignedDoorwayException, MissingDoorwayException, ImproperEndRoomException {
        super(id, RoomType.END, dungeon, schematic);
        if (orientation.count() != 1)
            throw new ImproperEndRoomException("End room must be a dead end!");
    }

    @Override
    public String getPath() {
        return DragonMath.instance.getDataFolder().getAbsolutePath() + File.separator + dungeon.getName() + File.separator + "endRooms" + File.separator;
    }
}
