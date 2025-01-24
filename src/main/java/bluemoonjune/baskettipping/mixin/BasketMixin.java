package bluemoonjune.baskettipping.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicBasket;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicBasket.class)
public abstract class BasketMixin extends BlockLogic {


	public BasketMixin(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player entityplayer, Side side, double xPlaced, double yPlaced) {
		entityplayer.sendMessage("mwah ha ha i have taken over your basket");
		return false;
	}
}
