package listeners;

import java.util.Random;

import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class EnchantingTable {

    private EntityPlayer entityPlayer;
    private Player player;
    private EnchantingTableContainer container;

    private String title = " ";
    private int amount;
    private int counter;

    public EnchantingTable(Player player){
        this.player = player;
        entityPlayer = ((CraftPlayer)player).getHandle();
        container = new EnchantingTableContainer(entityPlayer);
        counter = entityPlayer.nextContainerCounter();
    }

    public void open(){
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(counter, "minecraft:enchanting_table", IChatBaseComponent.ChatSerializer.a(title)));

        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.windowId = counter;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setNotRealBookshelfAmount(int amount){
        this.amount = amount;
    }

    public void addItem(EnchantingSlot slot, org.bukkit.inventory.ItemStack stack){
        player.getOpenInventory().setItem(slot.getSlot(), stack);
    }

    public enum EnchantingSlot{
        ENCHANT(0),
        LAPIS(1);

        int slot;
        private EnchantingSlot(int slot){
            this.slot = slot;
        }
        public int getSlot() {
            return slot;
        }

    }

    private class EnchantingTableContainer extends ContainerEnchantTable {

        private Random k = new Random();

        public EnchantingTableContainer(EntityPlayer entityPlayer) {
            super(entityPlayer.inventory, entityPlayer.world, new BlockPosition(0, 0, 0));
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public void a(IInventory iinventory){
            if (iinventory == enchantSlots) {
                ItemStack itemstack = iinventory.getItem(0);
                if (itemstack != null) {
                    k.setSeed(f);
                    for(int j = 0; j<3; j++){
                        k.setSeed(f);
                        costs[j] = EnchantmentManager.a(k, j, amount, itemstack);
                    }

                }
            }

        }

    }

}
