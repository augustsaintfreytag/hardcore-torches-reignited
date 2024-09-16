package net.qxeii.hardcore_torches.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.qxeii.hardcore_torches.mixinlogic.StoveBlockEntityMixinLogic;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

@Mixin(StoveBlockEntity.class)
public abstract class StoveBlockEntityMixin implements StoveBlockEntityMixinLogic {

	// Properties

	private int fuel = 0;

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = Math.max(0, fuel);
	}

	// NBT

	@Inject(method = "readNbt", at = @At("TAIL"))
	public void injectedReadNbt(NbtCompound compound, CallbackInfo callbackInfo) {
		readNbt(compound);
	}

	@Inject(method = "writeNbt", at = @At("TAIL"))
	public void injectedWriteNbt(NbtCompound compound, CallbackInfo callbackInfo) {
		writeNbt(compound);
	}

	@Inject(method = "toInitialChunkDataNbt", at = @At("RETURN"))
	public void injectedToInitialChunkDataNbt(CallbackInfoReturnable<NbtCompound> callbackInfo) {
		var nbt = callbackInfo.getReturnValue();
		toInitialChunkDataNbt(nbt);
	}

}
