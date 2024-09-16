package net.qxeii.hardcore_torches.mixinlogic;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qxeii.hardcore_torches.Mod;
import net.qxeii.hardcore_torches.util.InteractionUtils;
import net.qxeii.hardcore_torches.util.WorldUtils;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

public interface StoveBlockMixinLogic {

	public default <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World level, BlockState state,
			BlockEntityType<T> blockEntityType) {
		return (World _world, BlockPos _blockPos, BlockState _blockState, T _blockEntity) -> {
			StoveBlockEntityMixinLogic.tick(_world, _blockPos, _blockState, (StoveBlockEntity) _blockEntity);
		};
	}

	public default boolean onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		var heldStack = player.getStackInHand(hand);
		var heldItem = heldStack.getItem();

		var stoveBlockEntityMixin = (StoveBlockEntityMixinLogic) ((StoveBlockEntity) world.getBlockEntity(pos));
		var stoveFuel = stoveBlockEntityMixin.getFuel();

		// Fuel Check

		if (player.isSneaking() && heldStack.isEmpty()) {
			if (world.isClient) {
				displayFuelMessage(player, stoveFuel);
			}

			return true;
		}

		// Lighting

		// This runs the same check done by Farmer's Delight code.
		if ((heldItem instanceof FlintAndSteelItem || heldItem instanceof FireChargeItem)) {
			if (stoveFuel == 0) {
				if (world.isClient) {
					displayFuelMessage(player, stoveFuel);
				}

				return true;
			} else {
				if (Mod.config.fuelMessage) {
					if (world.isClient) {
						displayFuelMessage(player, stoveFuel);
					}
				}
			}
		}

		// Refuelling

		var itemFuelValue = FuelRegistry.INSTANCE.get(heldItem);

		if (itemFuelValue != null && itemFuelValue > 0) {
			var updatedStoveFuel = stoveFuel + itemFuelValue * Mod.config.campfireFuelAdditionMultiplier;

			heldStack.setCount(heldStack.getCount() - 1);
			stoveBlockEntityMixin.setFuel(updatedStoveFuel);

			if (Mod.config.fuelMessage) {
				displayFuelMessage(player, updatedStoveFuel);
			}

			InteractionUtils.playItemRefuellingSound(world, pos, heldStack);
			InteractionUtils.swingHand(world, player);

			return true;
		}

		return false;
	}

	public default BlockState getPlacementState(ItemPlacementContext context, BlockState placementState) {
		return placementState.with(StoveBlock.LIT, false);
	}

	private void displayFuelMessage(PlayerEntity player, int fuel) {
		var fuelTimeMessage = WorldUtils.formattedFuelText(fuel);
		player.sendMessage(fuelTimeMessage, true);
	}

}
