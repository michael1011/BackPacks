package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.Crafting;
import at.michael1011.backpacks.EnchantGlow;
import at.michael1011.backpacks.Main;
import at.michael1011.backpacks.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static at.michael1011.backpacks.Crafting.slots;
import static at.michael1011.backpacks.Crafting.type;
import static at.michael1011.backpacks.Main.*;

public class RightClick implements Listener {

    static final HashMap<Player, String> openInvs = new HashMap<>();
    static final HashMap<Player, String[]> openInvsCommand = new HashMap<>();

    static final HashMap<Player, String> openFurnaces = new HashMap<>();
    static final HashMap<Player, Inventory> openFurnacesInvs = new HashMap<>();

    public RightClick(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rightClickEvent(PlayerInteractEvent e) {
        Action action = e.getAction();

        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if(action == Action.RIGHT_CLICK_BLOCK) {
                Material block = e.getClickedBlock().getType();

                if(block.equals(Material.WORKBENCH) || block.equals(Material.FURNACE) || block.equals(Material.BURNING_FURNACE) ||
                        block.equals(Material.CHEST) || block.equals(Material.ENDER_CHEST) || block.equals(Material.ENCHANTMENT_TABLE) ||
                        block.equals(Material.ANVIL) || block.equals(Material.HOPPER) || block.equals(Material.DISPENSER)) {
                    return;
                }
            }

            final ItemStack item = e.getItem();

            if(item != null) {
                String backPack = Crafting.loreMap.get(item.getItemMeta().getLore());

                if(backPack != null) {
                    final Player p = e.getPlayer();

                    if(p.hasPermission("backpacks.use."+backPack)) {
                        final String finalBackPack = backPack;

                        final String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

                        switch (type.get(backPack)) {
                            case "normal":
                                SQL.checkTable("bp_"+backPack+"_"+trimmedID, new SQL.Callback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean rs) {
                                        if(rs) {
                                            SQL.getResult("SELECT * FROM bp_"+finalBackPack+"_"+trimmedID, new SQL.Callback<ResultSet>() {
                                                @Override
                                                public void onSuccess(ResultSet rs) {
                                                    Inventory open = getInv(rs, p, finalBackPack, item.getItemMeta().getDisplayName(), true, null);

                                                    if(open != null) {
                                                        p.openInventory(open);
                                                    }

                                                }

                                                @Override
                                                public void onFailure(Throwable e) {}

                                            });

                                        } else {
                                            SQL.query("CREATE TABLE IF NOT EXISTS bp_"+finalBackPack+"_"+trimmedID+"(position INT(100), material VARCHAR(100), "+
                                                    "durability INT(100), amount INT(100), name VARCHAR(100), lore VARCHAR(1000), enchantments VARCHAR(1000), "+
                                                    "potion VARCHAR(1000))", new SQL.Callback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean rs) {
                                                    openInvs.put(p, finalBackPack);

                                                    p.openInventory(Bukkit.getServer().createInventory(p, slots.get(finalBackPack),
                                                            item.getItemMeta().getDisplayName()));
                                                }

                                                @Override
                                                public void onFailure(Throwable e) {}

                                            });

                                        }

                                    }

                                    @Override
                                    public void onFailure(Throwable e) {}

                                });

                                break;

                            case "ender":
                                p.openInventory(p.getEnderChest());

                                break;

                            case "crafting":
                                p.openWorkbench(p.getLocation(), true);

                                break;

                            case "furnace":
                                if(Crafting.furnaceGui.containsKey(finalBackPack)) {
                                    if(Crafting.furnaceGui.get(finalBackPack).equals("true")) {
                                        SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='"+trimmedID+"'", new SQL.Callback<ResultSet>() {
                                            @Override
                                            public void onSuccess(ResultSet rs) {
                                                try {
                                                    rs.beforeFirst();

                                                    if(rs.next()) {
                                                        openFurnace(p, finalBackPack, item.getItemMeta().getDisplayName(), Boolean.valueOf(rs.getString("ores")),
                                                                Boolean.valueOf(rs.getString("food")), Boolean.valueOf(rs.getString("autoFill")), rs.getInt("coal"));

                                                    } else {
                                                        final Boolean ores = furnaceGui.getBoolean("ores.defaultOption");
                                                        final Boolean food = furnaceGui.getBoolean("food.defaultOption");
                                                        final Boolean autoFill = furnaceGui.getBoolean("autoFill.defaultOption");

                                                        SQL.query("INSERT INTO bp_furnaces (uuid, ores, food, autoFill, coal) VALUES ('"+trimmedID+"', '"+
                                                                String.valueOf(ores)+"', '"+
                                                                String.valueOf(food)+"', '"+
                                                                String.valueOf(autoFill)+"', '0')", new SQL.Callback<Boolean>() {
                                                            @Override
                                                            public void onSuccess(Boolean rs) {
                                                                openFurnace(p, finalBackPack, item.getItemMeta().getDisplayName(), ores, food, autoFill, 0);
                                                            }

                                                            @Override
                                                            public void onFailure(Throwable e) {}

                                                        });
                                                    }

                                                    rs.close();

                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Throwable e) {}

                                        });

                                    }

                                }

                                break;
                        }

                    } else {
                        p.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',
                                messages.getString("Help.noPermission")));
                    }

                }

            }

        }

    }

    public static Inventory getInv(ResultSet rs, Player opener, String backPack, String name,
                                   Boolean openerIsOwner, String ownerID) {

        if(openInvs.containsKey(opener)) {
            return null;
        }

        if(rs != null) {
            try {
                rs.beforeFirst();

                Inventory inv = Bukkit.getServer().createInventory(opener, slots.get(backPack), name);

                while(rs.next()) {
                    String material = rs.getString("material");

                    ItemStack item = new ItemStack(Material.valueOf(material),
                            rs.getInt("amount"));

                    item.setDurability((short) rs.getInt("durability"));

                    ItemMeta meta = item.getItemMeta();

                    String nameM = rs.getString("name");
                    String loreM = rs.getString("lore");
                    String enchantment = rs.getString("enchantments");
                    String potion = rs.getString("potion");

                    if(!nameM.equals("")) {
                        meta.setDisplayName(nameM);

                        item.setItemMeta(meta);
                    }

                    if(!loreM.equals("")) {
                        meta.setLore(Arrays.asList(rs.getString("lore").split("~")));

                        item.setItemMeta(meta);
                    }

                    if(!enchantment.equals("")) {
                        if(!enchantment.equals("MobSpawnerEgg/")) {
                            String[] enchantments = enchantment.substring(0, enchantment.length()-1).split("/");

                            for(String enchant : enchantments) {
                                String[] parts = enchant.split(":");

                                Enchantment ench = Enchantment.getByName(parts[0]);
                                int enchLvl = Integer.valueOf(parts[1]);

                                item.addUnsafeEnchantment(ench, enchLvl);
                                meta.addEnchant(ench, enchLvl, true);
                            }
                        }
                    }

                    if(!potion.equals("")) {
                        if(material.toLowerCase().contains("potion")) {
                            String[] parts = potion.split("/");

                            PotionMeta potionM = (PotionMeta) meta;

                            PotionData potionD = new PotionData(PotionType.valueOf(parts[0]), Boolean.parseBoolean(parts[1]),
                                    Boolean.parseBoolean(parts[2]));

                            potionM.setBasePotionData(potionD);

                            item.setItemMeta(potionM);

                        } else if(material.equals("MONSTER_EGG")) {
                            try {
                                Object nmsStack = Class.forName("org.bukkit.craftbukkit."+version+".inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
                                Object nmsCompound = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);

                                if(nmsCompound == null) {
                                    nmsCompound = Class.forName("net.minecraft.server."+version+".NBTTagCompound").getConstructor().newInstance();
                                }

                                Object compound = nmsCompound.getClass().getConstructor().newInstance();

                                compound.getClass().getMethod("setString", String.class, String.class).invoke(compound, "id", potion);
                                nmsCompound.getClass().getMethod("set", String.class, Class.forName("net.minecraft.server."+version+".NBTBase"))
                                        .invoke(nmsCompound, "EntityTag", compound);

                                Boolean mobSpawnerEgg = enchantment.equals("MobSpawnerEgg/");

                                if(mobSpawnerEgg) {
                                    nmsCompound.getClass().getMethod("setString", String.class, String.class)
                                            .invoke(nmsCompound, "MobSpawnerEgg", "MobSpawnerEgg");
                                }

                                nmsStack.getClass().getMethod("setTag", nmsCompound.getClass()).invoke(nmsStack, nmsCompound);

                                item = ((ItemStack) Class.forName("org.bukkit.craftbukkit."+version+".inventory.CraftItemStack").getMethod("asBukkitCopy", nmsStack.getClass()).invoke(null, nmsStack));

                                if(mobSpawnerEgg) {
                                    EnchantGlow.addGlow(item);
                                }

                            } catch(InstantiationException | InvocationTargetException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException exception) {
                                exception.printStackTrace();
                            }

                        } else if(material.equals("SKULL_ITEM")) {
                            SkullMeta skull = (SkullMeta) item.getItemMeta();

                            skull.setOwner(potion);

                            item.setItemMeta(skull);
                        }

                    }

                    inv.setItem(rs.getInt("position"), item);
                }

                rs.close();

                if(openerIsOwner) {
                    openInvs.put(opener, backPack);

                } else {
                    openInvsCommand.put(opener, new String[]{backPack, ownerID});
                }

                return inv;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    private void openFurnace(final Player opener, String backPack, String name, Boolean oresEnabled, Boolean foodEnabled,
                             Boolean autoFillEnable, int amountCoal) {
        ItemStack ores = new ItemStack(Material.IRON_ORE);
        ItemStack food = new ItemStack(Material.POTATO_ITEM);
        ItemStack autoFill = new ItemStack(Material.FURNACE);

        setMeta(ores, "ores");
        setMeta(food, "food");
        setMeta(autoFill, "autoFill");

        ItemStack oresToggle = new ItemStack(Material.WOOL, 1, (byte) getColor(oresEnabled));
        ItemStack foodToggle = new ItemStack(Material.WOOL, 1, (byte) getColor(foodEnabled));
        ItemStack autoFillToggle = new ItemStack(Material.WOOL, 1, (byte) getColor(autoFillEnable));

        setToggleMeta(oresToggle, oresEnabled);
        setToggleMeta(foodToggle, foodEnabled);
        setToggleMeta(autoFillToggle, autoFillEnable);

        ItemStack blank = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);

        ItemMeta blankM = blank.getItemMeta();

        blankM.setDisplayName(ChatColor.GRAY+"");

        blank.setItemMeta(blankM);

        Inventory inv = Bukkit.getServer().createInventory(opener, 36, name);

        if(amountCoal != 0) {
            ItemStack coal = new ItemStack(Material.COAL, amountCoal);

            inv.setItem(35, coal);
        }

        for(int i = 0; i < 35; i++) {
            inv.setItem(i, blank);
        }

        inv.setItem(3, ores);
        inv.setItem(4, food);
        inv.setItem(5, autoFill);

        inv.setItem(12, oresToggle);
        inv.setItem(13, foodToggle);
        inv.setItem(14, autoFillToggle);

        opener.getOpenInventory().close();
        opener.openInventory(inv);

        openFurnaces.put(opener, backPack);
        openFurnacesInvs.put(opener, inv);
    }

    private void setMeta(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', furnaceGui.getString(name+".name")));
        meta.setLore(getLore(name+".description"));

        item.setItemMeta(meta);
    }

    static void setToggleMeta(ItemStack item, Boolean toggle) {
        ItemMeta meta = item.getItemMeta();

        String name;

        if(toggle) {
            name = ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("enabled"));
        } else {
            name = ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("disabled"));
        }

        meta.setDisplayName(name);

        item.setItemMeta(meta);
    }

    static List<String> getLore(String path) {
        String lore = "";

        Map<String, Object> loreSec = furnaceGui.getConfigurationSection(path).getValues(true);

        for(Map.Entry<String, Object> ent : loreSec.entrySet()) {
            lore = lore+","+ChatColor.translateAlternateColorCodes('&', ent.getValue().toString());
        }

        return Arrays.asList(lore.split("\\s*,\\s*"));
    }

    static int getColor(Boolean bool) {
        if(bool) {
            return 5;
        }

        return 14;
    }

}
