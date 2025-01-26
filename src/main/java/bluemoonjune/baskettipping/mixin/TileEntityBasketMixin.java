package bluemoonjune.baskettipping.mixin;

import bluemoonjune.baskettipping.IFlip;
import net.minecraft.core.block.BlockLogicLever;
import net.minecraft.core.block.BlockLogicTorchRedstone;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(value = TileEntityBasket.class)
public abstract class TileEntityBasketMixin extends TileEntity implements IFlip {
	public int flipTime = 0;

	@Shadow(remap = false)
	@Final
	private Map<TileEntityBasket.BasketEntry, Integer> contents;

	@Inject(
		method = "tick",
		at = @At("TAIL"),
		remap = false
	)
	public void flipCheck(CallbackInfo ci) {
		if (worldObj == null) return;
		if (flipTime > 0) {
			flipTime--;
			if (flipTime == 0) {
				worldObj.setBlockMetadata(x, y, z, worldObj.getBlockMetadata(x, y, z) & ~1);
				worldObj.notifyBlockChange(this.x, this.y, this.z, Blocks.BASKET.id());
			}
		}
	}

	@Override
	public void flip(int flipTime) {
		this.flipTime = flipTime;
		if (worldObj == null) return;
		TileEntity below = worldObj.getTileEntity(x, y-1, z);

		int offset = below instanceof TileEntityActivator ? ((TileEntityActivator)below).stackSelector : 0;

		if (below instanceof Container) {
			Container container = (Container)below;
			List<TileEntityBasket.BasketEntry> toRemove = new ArrayList<>();

			for(Map.Entry<TileEntityBasket.BasketEntry, Integer> entry : this.contents.entrySet()) {
				TileEntityBasket.BasketEntry basketEntry = (TileEntityBasket.BasketEntry)entry.getKey();
				ItemStack basketEntryStack = new ItemStack(basketEntry.id, (Integer)entry.getValue(), basketEntry.metadata, basketEntry.tag);

				int size = container.getContainerSize();

				for (int j = 0; j < size; j++) {
					int i = (j + offset) % size;
					ItemStack slot = container.getItem(i);
					if (slot == null) {
						container.setItem(i, basketEntryStack.splitStack(Math.min(64, basketEntryStack.stackSize)));
					}
					else if (slot.canStackWith(basketEntryStack)) {
						int amt = Math.min(basketEntryStack.stackSize, slot.getMaxStackSize() - slot.stackSize);
						basketEntryStack.stackSize -= amt;
						slot.stackSize += amt;
					}
					if (basketEntryStack.stackSize <= 0) {
						toRemove.add(basketEntry);
						break;
					}
				}
				this.contents.put(basketEntry, basketEntryStack.stackSize);

			}

			for(TileEntityBasket.BasketEntry entry : toRemove) {
				this.contents.remove(entry);
			}

			((TileEntityBasket)(TileEntity)this).updateNumUnits();
			worldObj.notifyBlockChange(this.x, this.y, this.z, Blocks.BASKET.id());
			return;
		}
		dropContents(worldObj, x, y, z);
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

}
