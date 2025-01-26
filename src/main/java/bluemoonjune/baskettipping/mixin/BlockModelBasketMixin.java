package bluemoonjune.baskettipping.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.model.*;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicBasket;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockModelBasket.class)
public abstract class BlockModelBasketMixin<T extends BlockLogicBasket> extends BlockModel<T> {


	public BlockModelBasketMixin(Block block) {
		super(block);
	}

	@Redirect(
		method = "render",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/model/BlockModelBasket;renderStandardBlock(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/util/phys/AABB;III)Z", ordinal = 0),
		remap = false
	)
	public boolean flipBottom(BlockModelBasket<T> instance, Tessellator tessellator, AABB aabb, int x, int y, int z) {
		if ((renderBlocks.blockAccess.getBlockMetadata(x, y, z) & 1) == 1) {
			aabb.set(0, 15/16.0, 0, 1, 1, 1);
		}
		instance.renderStandardBlock(tessellator, aabb, x, y, z);
		return true;
	}

	@Inject(
		method = "render",
		at = @At("RETURN"),
		remap = false
	)
	public void resetAfterRender(CallbackInfoReturnable<Boolean> cir) {
		resetRenderBlocks();
	}

	@Inject(
		method = "render",
		at = @At("HEAD"),
		remap = false
	)
	public void renderFlipped(Tessellator tessellator, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if ((renderBlocks.blockAccess.getBlockMetadata(x, y, z) & 1) == 1) {
			renderBlocks.uvRotateNorth = 3;
			renderBlocks.uvRotateEast = 3;
			renderBlocks.uvRotateSouth = 3;
			renderBlocks.uvRotateWest = 3;
		}
	}

	@Redirect(
		method = "render",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/model/BlockModelBasket;setRenderSide(Lnet/minecraft/core/util/helper/Side;Z)V"),
		remap = false
	)
	public void fixEdges(BlockModelBasket instance, Side side, boolean b) {
		instance.setRenderSide(side,side == Side.BOTTOM || b);
	}
}
