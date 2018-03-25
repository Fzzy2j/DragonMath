package me.fzzy.dragonmath.util;

import com.sk89q.worldedit.blocks.BaseBlock;
import me.fzzy.dragonmath.dungeons.session.DungeonSession;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidWorldGenerator extends ChunkGenerator {

    DungeonSession session;

    public VoidWorldGenerator(DungeonSession session) {
        this.session = session;
    }

    @Override
    public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
        byte[] result = new byte[32768];
        for (int i = 0; i < result.length; i++) {
            result[i] = 3;
        }
        return result;
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, -10, 128, -10);
    }

}
