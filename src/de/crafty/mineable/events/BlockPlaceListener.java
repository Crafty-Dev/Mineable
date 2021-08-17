package de.crafty.mineable.events;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagInt;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;

public class BlockPlaceListener implements Listener {


    @EventHandler
    public static void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockState state = block.getState();
        ItemStack stack = event.getItemInHand();
        World world = player.getWorld();

        if (state instanceof Container) {

            CraftWorld craftWorld = (CraftWorld) world;
            CraftBlock craftBlock = (CraftBlock) block;
            BlockPosition pos = new BlockPosition(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
            net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
            NBTTagCompound compound = nmsStack.getOrCreateTag().getCompound("BlockEntityTag");

            //Updating the blocks tileEntity like minecraft does it
            TileEntity tileentity = craftWorld.getHandle().getTileEntity(pos);
            if (compound != null) {
                if (tileentity != null) {

                    NBTTagCompound compound1 = tileentity.save(new NBTTagCompound());
                    NBTTagCompound compound2 = compound1.clone();

                    //Merging old tileEntity data and the itemStack data
                    compound1.a(compound);
                    compound1.set("x", NBTTagInt.a(block.getLocation().getBlockX()));
                    compound1.set("y", NBTTagInt.a(block.getLocation().getBlockY()));
                    compound1.set("z", NBTTagInt.a(block.getLocation().getBlockZ()));

                    //When the old and merged data do not match, the tileEntity gets updated
                    if (!compound1.equals(compound2)) {
                        tileentity.load(craftBlock.getNMS(), compound1);
                        tileentity.update();
                    }
                }
            }

            //Updating furnaces to fix animation bugs
            if (state instanceof Furnace) {
                Furnace furnace = (Furnace) state;
                furnace.setBurnTime(furnace.getBurnTime());
                furnace.update(true);
            }


        }
    }
}
