package bluemoonjune.baskettipping.mixin;

import bluemoonjune.baskettipping.IFlip;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicBasket;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicBasket.class)
public abstract class BasketMixin extends BlockLogic {

	public final int FLIP = 0b100000;

	public BasketMixin(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player entityplayer, Side side, double xPlaced, double yPlaced) {
		flip(world, x, y, z);
		return true;
	}

	@Override
	public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
		flip(world, x, y, z);
	}

	public void flip(World world, int x, int y, int z) {
		TileEntityBasket te = (TileEntityBasket)world.getTileEntity(x, y, z);
		((IFlip)te).setFlipTime(20);
		world.setBlockMetadata(x, y, z, world.getBlockMetadata(x, y, z) | FLIP);

	}
}
