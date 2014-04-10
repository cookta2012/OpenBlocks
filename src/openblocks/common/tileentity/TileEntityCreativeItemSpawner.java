package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import openblocks.client.gui.GuiCreativeItemSpawner;
import openblocks.common.container.ContainerCreativeItemSpawner;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.OpenMods;
import openmods.api.IHasGui;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.OpenTileEntity;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityCreativeItemSpawner extends SyncedTileEntity implements IHasGui, IInventoryProvider {

	private GenericInventory inventory = new GenericInventory("inventory.creativeitemspawner", false, 4);
	
	private SyncableInt range;
	private SyncableInt delay;
	
	@Override
	protected void createSyncedFields() {
		range = new SyncableInt(10);
		delay = new SyncableInt(100);
	}
	
	public SyncableInt getRange() {
		return range;
	}

	public SyncableInt getDelay() {
		return delay;
	}
	
	@Override
	public void updateEntity() {
		
		if (!worldObj.isRemote && OpenMods.proxy.getTicks(worldObj) % 20 == 0) {

			if (worldObj.rand.nextInt(delay.getValue()) == 0) {
				
				int spread = range.getValue();
				int doubleSpread = spread * 2;
				
				int x = xCoord - spread + worldObj.rand.nextInt(doubleSpread);
				int z = zCoord - spread + worldObj.rand.nextInt(doubleSpread);
				int y = worldObj.getHeightValue(x, z);
				
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					
					ItemStack stack = inventory.getStackInSlot(i);
					
					if (stack != null) {
						EntityItem entityitem = new EntityItem(worldObj, x + 0.5, y + 0.5, z + 0.5, stack);
						entityitem.delayBeforeCanPickup = 10;
						if (stack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
						}
						worldObj.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
	}
	
	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerCreativeItemSpawner(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiCreativeItemSpawner((ContainerCreativeItemSpawner) getServerGui(player));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

}