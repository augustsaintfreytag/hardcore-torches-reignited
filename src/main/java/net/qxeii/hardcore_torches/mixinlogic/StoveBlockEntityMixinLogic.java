package net.qxeii.hardcore_torches.mixinlogic;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

public interface StoveBlockEntityMixinLogic {

	// Properties

	public static final String NBT_KEY_FUEL = "Fuel";

	public int getFuel();

	public void setFuel(int fuel);

	// NBT

	public default void readNbt(NbtCompound compound) {
		if (compound.contains(NBT_KEY_FUEL)) {
			var fuel = compound.getInt(NBT_KEY_FUEL);
			this.setFuel(fuel);
		}
	}

	public default void writeNbt(NbtCompound compound) {
		compound.putInt(NBT_KEY_FUEL, this.getFuel());
	}

	public default void toInitialChunkDataNbt(NbtCompound nbt) {
		nbt.putInt(NBT_KEY_FUEL, this.getFuel());
	}

	// Tick

	public static void tick(World world, BlockPos pos, BlockState state, StoveBlockEntity stoveBlockEntity) {
		if (world.isClient) {
			StoveBlockEntity.animationTick(world, pos, state, stoveBlockEntity);
		} else {
			StoveBlockEntity.cookingTick(world, pos, state, stoveBlockEntity);
		}

		var isLit = state.get(StoveBlock.LIT);

		if (!isLit) {
			return;
		}

		var stoveBlockEntityMixin = (StoveBlockEntityMixinLogic) stoveBlockEntity;
		var updatedFuel = Math.max(0, stoveBlockEntityMixin.getFuel() - 1);

		stoveBlockEntityMixin.setFuel(updatedFuel);

		if (updatedFuel == 0) {
			stoveBlockEntityMixin.extinguish(world, pos, state);
		}
	}

	// Logic

	private void extinguish(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, (BlockState) state.with(StoveBlock.LIT, false));

		if (!world.isClient) {
			world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.0F,
					false);
		}
	}

}
