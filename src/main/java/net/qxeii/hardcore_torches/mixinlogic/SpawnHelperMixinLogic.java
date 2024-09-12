package net.qxeii.hardcore_torches.mixinlogic;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldView;
import net.qxeii.hardcore_torches.util.WorldUtils;

public interface SpawnHelperMixinLogic {

	public static boolean canSpawn(WorldView world, SpawnGroup group, BlockPos position,
			boolean precedingCanSpawn) {
		if (group != SpawnGroup.MONSTER) {
			return precedingCanSpawn;
		}

		if (worldIsRaining(world, position) && !worldIsNight(world)) {
			return false;
		}

		return precedingCanSpawn;
	}

	private static boolean worldIsNight(WorldView world) {
		if (world instanceof ServerWorld) {
			var serverWorld = (ServerWorld) world;
			return !WorldUtils.worldIsDaytime(serverWorld.getTimeOfDay());
		}

		if (world instanceof ChunkRegion) {
			var chunkRegion = (ChunkRegion) world;
			return !WorldUtils.worldIsDaytime(chunkRegion.getLevelProperties().getTimeOfDay());
		}

		return false;
	}

	private static boolean worldIsRaining(WorldView world, BlockPos position) {
		if (world instanceof ServerWorld) {
			var serverWorld = (ServerWorld) world;
			return WorldUtils.worldIsRaining(serverWorld, position);
		}

		if (world instanceof ChunkRegion) {
			var chunkRegion = (ChunkRegion) world;
			return chunkRegion.getLevelProperties().isRaining();
		}

		return false;
	}

}
