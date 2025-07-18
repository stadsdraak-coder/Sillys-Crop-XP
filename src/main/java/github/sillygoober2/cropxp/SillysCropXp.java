package github.sillygoober2.cropxp;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

public class SillysCropXp implements ModInitializer {
	public static final String MOD_ID = "sillys-crop-xp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Map<String, int[]> cropXpMap;

	@Override
	public void onInitialize() {
		LOGGER.info("Silly's Crop XP loaded");
		ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server)-> {
			ConfigFile.ensureConfigExists(server);
			ConfigFile.loadConfig(server);
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			MinecraftServer server = world.getServer();
			Block block = state.getBlock();
			if(block instanceof CropBlock){
				int cropMaxAge = ((CropBlock) block).getMaxAge();
				int cropAge = ((CropBlock) block).getAge(state);

				if(cropAge == cropMaxAge){
					givePoints(player, block);
				}
			}
			else if (block instanceof NetherWartBlock){
				int netherWartAge = state.get(NetherWartBlock.AGE);
				if(netherWartAge == 3){
					givePoints(player, block);
				}
			}
		});
	}

	public static void givePoints(PlayerEntity player, Block crop){
		String id = Registries.BLOCK.getId(crop).toString();
		int[] xpRange = ConfigFile.getConfigMap().get(id);
		if(xpRange != null){
			int minXp = xpRange[0];
			int maxXp = xpRange[1];

			Random random = new Random();
			int randomXP = minXp + random.nextInt(maxXp - minXp + 1);
			System.out.println(randomXP);
			player.addExperience(randomXP);
		}
	}
}