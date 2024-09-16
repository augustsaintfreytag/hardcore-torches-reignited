package net.qxeii.hardcore_torches.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qxeii.hardcore_torches.mixinlogic.StoveBlockMixinLogic;
import vectorwing.farmersdelight.common.block.StoveBlock;

@Mixin(StoveBlock.class)
public abstract class StoveBlockMixin implements StoveBlockMixinLogic {

	@Inject(method = "getTicker", at = @At("RETURN"), cancellable = true)
	public <T extends BlockEntity> void injectedGetTicker(World level, BlockState state,
			BlockEntityType<T> blockEntityType, CallbackInfoReturnable<@Nullable BlockEntityTicker<T>> callbackInfo) {
		var ticker = getTicker(level, state, blockEntityType);
		callbackInfo.setReturnValue(ticker);
	}

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	public void injectedOnUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit, CallbackInfoReturnable<ActionResult> callbackInfo) {

		var result = onUse(state, level, pos, player, hand, hit);

		if (result) {
			callbackInfo.setReturnValue(ActionResult.SUCCESS);
		}
	}

	@Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
	public void injectedGetPlacementState(ItemPlacementContext context,
			CallbackInfoReturnable<BlockState> callbackInfo) {
		var placementState = callbackInfo.getReturnValue();
		callbackInfo.setReturnValue(getPlacementState(context, placementState));
	}

}
