package bluemoonjune.baskettipping.mixin;

import net.minecraft.core.block.BlockBasket;
import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicBasket.class)
public abstract class BasketMixin extends BlockTileEntity {

	public BasketMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Inject(
		method = "onBlockRightClicked",
		at = @At("HEAD"),
		remap = false,
		cancellable = true
	)
	public void tipBasket(CallbackInfo ci) {
		ci.cancel();
	}
}
