package net.qxeii.hardcore_torches.mixinlogic;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qxeii.hardcore_torches.Mod;
import net.qxeii.hardcore_torches.util.WorldUtils;

public interface CampfireBlockMixinLogic {

	// Properties

	public static final BooleanProperty LIT = Properties.LIT;

	public static final BooleanProperty SIGNAL_FIRE = CampfireBlock.SIGNAL_FIRE;

	public static final BooleanProperty WATERLOGGED = CampfireBlock.WATERLOGGED;

	public static final DirectionProperty FACING = CampfireBlock.FACING;

	public StateManager<Block, BlockState> getStateManager();

	public void setDefaultState(BlockState defaultState);

	public BlockState getDefaultState();

	// Placement

	public default BlockState injectedGetPlacementState(ItemPlacementContext context, BlockState state) {
		return state.with(LIT, false);
	}

	// Interaction

	public default ActionResult injectedOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand hand,
			BlockHitResult hit) {
		var blockEntity = (CampfireBlockEntity) world.getBlockEntity(pos);
		var campfireBlockEntity = (CampfireBlockEntityMixinLogic) (Object) blockEntity;
		var isLit = state.get(LIT);

		var stack = player.getStackInHand(hand);

		if (stack.isEmpty() && player.isSneaking()) {
			if (world.isClient) {
				displayFuelMessage(player, campfireBlockEntity.getFuel());
			}

			return ActionResult.PASS;
		}

		var stackIsShovel = stack.isIn(Mod.CAMPFIRE_SHOVELS);
		var stackIsCoal = stack.isIn(Mod.CAMPFIRE_FUELS);
		var stackIsLog = stack.isIn(Mod.CAMPFIRE_LOG_FUELS);

		if (stackIsShovel) {
			if (!isLit) {
				return ActionResult.PASS;
			}

			CampfireBlockEntityMixinLogic.extinguish(world, pos, state);
			useShovelItemStack(stack, player, hand);

			if (world.isClient) {
				player.swingHand(hand);
			}

			return ActionResult.SUCCESS;
		}

		if (!stackIsCoal && !stackIsLog) {
			return ActionResult.PASS;
		}

		var item = stack.getItem();
		var itemFuelValue = FuelRegistry.INSTANCE.get(item) * Mod.config.campfireFuelAdditionMultiplier;

		if (!world.isClient) {
			stack.setCount(stack.getCount() - 1);
			campfireBlockEntity.setFuel(campfireBlockEntity.getFuel() + itemFuelValue);

			if (stackIsLog) {
				world.playSound(null, pos, Mod.CAMPFIRE_LOG_PLACE_SOUND, SoundCategory.BLOCKS, 1.0F, 1.0F);
			} else if (stackIsCoal) {
				world.playSound(null, pos, Mod.CAMPFIRE_LOG_PLACE_SOUND, SoundCategory.BLOCKS, 0.75F, 1.75F);
			}

			displayFuelMessage(player, campfireBlockEntity.getFuel());
		} else {
			player.swingHand(hand);
		}

		return ActionResult.SUCCESS;
	}

	private void useShovelItemStack(ItemStack stack, PlayerEntity player, Hand itemHand) {
		stack.damage(1, player, forwardedPlayer -> {
			breakShovelItemStack(player, stack, itemHand);
		});
	}

	private void breakShovelItemStack(PlayerEntity player, ItemStack stack, Hand itemHand) {
		var equipmentSlot = itemHand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		var itemSlot = player.getInventory().getSlotWithStack(stack);

		player.sendEquipmentBreakStatus(equipmentSlot);
		
		if (!FabricLoader.getInstance().isModLoaded("ruined_equipment")) {
			// If equipment mod is not loaded, item can be removed directly.
			
			player.getInventory().removeStack(itemSlot);
			return;
		}

		var ruinedShovelItemId = shovelItemMap().get(stack.getItem().getTranslationKey());
		var ruinedShovelItem = Registries.ITEM.get(ruinedShovelItemId);

		if (ruinedShovelItem == Items.AIR) {
			return;
		}

		var ruinedShovelStack = new ItemStack(ruinedShovelItem, 1);
		player.getInventory().setStack(itemSlot, ruinedShovelStack);
	}

	private void displayFuelMessage(PlayerEntity player, int fuel) {
		var fuelTimeMessage = WorldUtils.formattedFuelText(fuel);
		player.sendMessage(fuelTimeMessage, true);
	}

	private Map<String, Identifier> shovelItemMap() {
		return new HashMap<String, Identifier>(
			Map.of(
				"minecraft:wooden_shovel", new Identifier("ruined_equipment", "ruined_wooden_shovel"),
				"minecraft:stone_shovel", new Identifier("ruined_equipment", "ruined_stone_shovel"),
				"minecraft:iron_shovel", new Identifier("ruined_equipment", "ruined_iron_shovel"),
				"minecraft:golden_shovel", new Identifier("ruined_equipment", "ruined_golden_shovel"),
				"minecraft:diamond_shovel", new Identifier("ruined_equipment", "ruined_diamond_shovel")
			)
		);
	}

}