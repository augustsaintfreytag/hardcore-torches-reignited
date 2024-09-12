package net.qxeii.hardcore_torches.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.qxeii.hardcore_torches.mixinlogic.SpawnHelperMixinLogic;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin implements SpawnHelperMixinLogic {

	@Inject(method = "canSpawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/SpawnSettings$SpawnEntry;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z", at = @At("TAIL"), cancellable = true)
	private static void injectedPrivateCanSpawn(ServerWorld world, SpawnGroup group,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos,
			double squaredDistance, CallbackInfoReturnable<Boolean> callbackInfo) {
		var returnValue = (boolean) callbackInfo.getReturnValue();

		if (!returnValue) {
			return;
		}

		returnValue = SpawnHelperMixinLogic.canSpawn(world, group, pos, returnValue);
		callbackInfo.setReturnValue(returnValue);
	}

	@Inject(method = "canSpawn(Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z", at = @At("TAIL"), cancellable = true)
	private static void injectedPublicCanSpawn(SpawnRestriction.Location location, WorldView world, BlockPos pos,
			@Nullable EntityType<?> entityType, CallbackInfoReturnable<Boolean> callbackInfo) {
		var returnValue = (boolean) callbackInfo.getReturnValue();

		if (!returnValue || entityType == null) {
			return;
		}

		var group = entityType.getSpawnGroup();

		returnValue = SpawnHelperMixinLogic.canSpawn(world, group, pos, returnValue);
		callbackInfo.setReturnValue(returnValue);
	}

}
