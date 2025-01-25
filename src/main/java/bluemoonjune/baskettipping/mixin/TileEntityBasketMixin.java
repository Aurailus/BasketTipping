package bluemoonjune.baskettipping.mixin;

import bluemoonjune.baskettipping.IFlip;
import net.minecraft.core.block.BlockLogicLever;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@Mixin(value = TileEntityBasket.class)
public class TileEntityBasketMixin extends TileEntity implements IFlip {
	public int flipTime = 0;
	@Inject(
		method = "tick",
		at = @At("TAIL"),
		remap = false
	)
	public void flipCheck(CallbackInfo ci) {
		if (flipTime > 0) {
			flipTime--;
		}
	}

	@Override
	public void setFlipTime(int flipTime) {
		this.flipTime = flipTime;
	}

	/**
	 * @author BlueMoonJune
	 * @reason Basket Tipping: no random position or velocity
	 */
	@Overwrite(
		remap = false
	)
	private void dropItemStack(Random rand, ItemStack itemstack) {
		float f = 0.5f;
		float f1 = 0.5f;
		float f2 = 0.5f;
		World workingWorld;
		if (this.worldObj != null) {
			workingWorld = this.worldObj;
		} else {
			if (this.carriedBlock == null) {
				return;
			}

			workingWorld = this.carriedBlock.world;
		}

		EntityItem item = new EntityItem(workingWorld, (double)((float)this.x + f), (double)((float)this.y + f1), (double)((float)this.z + f2), itemstack);
		item.xd = 0;
		item.yd = 0;
		item.zd = 0;
		workingWorld.entityJoinedWorld(item);
	}

	@Inject(
		method = "importItemStack",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void dontPickupIfFlipped(CallbackInfoReturnable<Boolean> ci) {
		if (flipTime > 0) {
			ci.setReturnValue(false);
			ci.cancel();
		}
	}

	@Shadow(
		remap = false
	)
	private Map<TileEntityBasket.BasketEntry, Integer> contents;



	public void insertItemsBelow(World world, int x, int y, int z) {
		Container belowTE = (Container)world.getTileEntity(x, y-1, z);

		if (belowTE != null) {
			int size = belowTE.getContainerSize();
			for (int i = 0; i < size; i++) {
				ItemStack slot = belowTE.getItem(i);
				ArrayList<TileEntityBasket.BasketEntry> toRemove = new ArrayList<TileEntityBasket.BasketEntry>();
				for(Map.Entry<TileEntityBasket.BasketEntry, Integer> entry : this.contents.entrySet()) {
					TileEntityBasket.BasketEntry basketEntry = (TileEntityBasket.BasketEntry)entry.getKey();
					ItemStack basketEntryStack = new ItemStack(basketEntry.id, (Integer)entry.getValue(), basketEntry.metadata, basketEntry.tag);
					if (slot.canStackWith(basketEntryStack)) {
						int amt = Math.max(slot.getMaxStackSize(belowTE), basketEntryStack.stackSize);
						slot.stackSize += amt;
						basketEntryStack.stackSize -= amt;
						if (basketEntryStack.stackSize <= 0) {
							toRemove.add(basketEntry);
						}
					}
				}
				for(TileEntityBasket.BasketEntry entry : toRemove) {
					this.contents.remove(entry);
				}
			}
			return;
		}
		dropContents(world, x, y, z);
	}
}
