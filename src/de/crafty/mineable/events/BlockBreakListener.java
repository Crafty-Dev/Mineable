package de.crafty.mineable.events;

import de.crafty.mineable.main.Mineable;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockBreakListener implements Listener {


    @EventHandler
    public static void onBlockDestroy(BlockBreakEvent event) {

        Player player = event.getPlayer();
        ItemStack heldItem = player.getItemInHand();
        World world = player.getWorld();
        Block block = event.getBlock();
        BlockState state = block.getState();

        if (state instanceof Container) {
            Container container = (Container) state;
            Inventory inv = container.getInventory();


            if (heldItem.getEnchantments().containsKey(Enchantment.SILK_TOUCH) && !inv.isEmpty() && !(state instanceof ShulkerBox) && Mineable.getInstance().getConfig().getBoolean(block.getType().name().toLowerCase())) {

                ItemStack stack = new ItemStack(block.getType());

                CraftWorld craftWorld = (CraftWorld) world;
                BlockPosition pos = new BlockPosition(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());

                //Modifying the blocks tileEntity
                TileEntity t = craftWorld.getHandle().getTileEntity(pos);
                net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
                NBTTagCompound compound = t.save(new NBTTagCompound());
                compound.remove("x");
                compound.remove("y");
                compound.remove("z");

                //Saving the blocks modified tileEntity data to the item that is dropped
                NBTTagCompound compound1 = new NBTTagCompound();
                compound1.set("BlockEntityTag", compound);
                nmsStack.setTag(compound1);
                stack = CraftItemStack.asBukkitCopy(nmsStack);

                //Modifying the items tooltip
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(container.getCustomName());
                meta.setLore(generateLore(state));
                stack.setItemMeta(meta);

                //Preventing duplication of items
                event.setDropItems(false);
                craftWorld.getHandle().removeTileEntity(pos);

                //Dropping the new modified item
                world.dropItemNaturally(block.getLocation(), stack);
            }
        }
    }


    private static List<String> generateLore(BlockState state) {
        ArrayList<String> lore = new ArrayList<>();

        if (state instanceof Furnace) {
            Furnace furnace = (Furnace) state;
            ItemStack fuel = furnace.getInventory().getFuel();
            ItemStack item = furnace.getInventory().getSmelting();
            ItemStack output = furnace.getInventory().getResult();

            lore.add("§7Fuel: " + getInvItemName(fuel) + " " + getInvItemAmount(fuel));
            lore.add("§7Item: " + getInvItemName(item) + " " + getInvItemAmount(item));
            lore.add("§7Output: " + getInvItemName(output) + " " + getInvItemAmount(output));

        } else if (state instanceof BrewingStand) {
            BrewingStand brewingStand = (BrewingStand) state;
            ItemStack fuel = brewingStand.getInventory().getFuel();
            ItemStack ingredient = brewingStand.getInventory().getIngredient();
            ItemStack item1 = brewingStand.getInventory().getContents()[0];
            ItemStack item2 = brewingStand.getInventory().getContents()[1];
            ItemStack item3 = brewingStand.getInventory().getContents()[2];

            lore.add("§7Fuel Level: " + brewingStand.getFuelLevel());
            lore.add("§7Fuel: " + getInvItemName(fuel) + " " + getInvItemAmount(fuel));
            lore.add("§7Ingredient: " + getInvItemName(ingredient) + " " + getInvItemAmount(ingredient));
            lore.add("§7Item 1: " + getInvItemName(item1) + " " + getInvItemAmount(item1));
            lore.add("§7Item 2: " + getInvItemName(item2) + " " + getInvItemAmount(item2));
            lore.add("§7Item 3: " + getInvItemName(item3) + " " + getInvItemAmount(item3));

        } else if (state instanceof Container) {
            ArrayList<ItemStack> contents = new ArrayList<>();

            for (ItemStack stack : ((Container) state).getInventory().getContents()) {
                if (stack != null)
                    contents.add(stack);
            }

            int length = contents.size();
            if (length > 6)
                length = 6;
            for (int i = 0; i < length; i++) {
                net.minecraft.server.v1_16_R3.ItemStack nmsStack1 = CraftItemStack.asNMSCopy(contents.get(i));
                lore.add("§7" + nmsStack1.getName().getString() + " x" + contents.get(i).getAmount());
            }
            if (length != contents.size()) {
                int rest = contents.size() - length;
                lore.add("§7§oand " + rest + " more...");
            }
        }

        return lore;
    }

    private static String getInvItemName(ItemStack stack) {
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        String name = nmsStack.getName().getString();
        return name.equals("Air") ? "Empty" : name;
    }

    private static String getInvItemAmount(ItemStack stack) {
        return stack == null ? "" : "x" + stack.getAmount();
    }

}
