package net.qxeii.hardcore_torches.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qxeii.hardcore_torches.Mod;

public class InteractionUtils {

	public static void swingHand(World world, PlayerEntity player) {
		if (world.isClient) {
			player.swingHand(player.getActiveHand());
		}
	}

	public static void playItemRefuellingSound(World world, BlockPos blockPos, ItemStack itemStack) {
		if (world.isClient) {
			return;
		}

		if (itemStack.isIn(Mod.CAMPFIRE_FUELS)) {
			world.playSound(null, blockPos, Mod.CAMPFIRE_LOG_PLACE_SOUND, SoundCategory.BLOCKS, 0.75F, 1.75F);
		} else {
			world.playSound(null, blockPos, Mod.CAMPFIRE_LOG_PLACE_SOUND, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

}
