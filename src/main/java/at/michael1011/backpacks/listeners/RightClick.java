package at.michael1011.backpacks.listeners;

import at.michael1011.backpacks.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static at.michael1011.backpacks.Crafting.backPacksMap;
import static at.michael1011.backpacks.Main.*;
import static at.michael1011.backpacks.listeners.InventoryClose.savingBackPacks;
import static at.michael1011.backpacks.nbt.CheckItem.checkItem;
import static at.michael1011.backpacks.nbt.NbtDecoder.decodeNbt;

public class RightClick implements Listener {

    static final HashMap<Player, BackPack> openInvs = new HashMap<>();
    static final HashMap<Player, BackPack> openInvsOther = new HashMap<>();

    static final HashMap<Player, BackPack> openFurnaces = new HashMap<>();
    static final HashMap<Player, Inventory> openFurnacesInvs = new HashMap<>();

    static final HashMap<Player, String> openInvsOwners = new HashMap<>();

    private static final ArrayList<Material> doNotOpen = new ArrayList<>();

    public RightClick(Main main) {
        List<String> doNotOpenRaw = config.getStringList("doNotOpen");

        for (String material : doNotOpenRaw) {
            material = material.toUpperCase();

            try {
                if (!material.contains("*")) {
                    doNotOpen.add(Material.valueOf(material));

                } else {
                    material = material.replaceAll("\\*", "");

                    for (Material compare : Material.values()) {
                        if (compare.name().contains(material)) {
                            doNotOpen.add(compare);
                        }
                    }
                }

            } catch (IllegalArgumentException e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                        messages.getString("Help.materialNotValidDoNotOpen")
                                .replaceAll("%material%", material)));
            }
        }

        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rightClickEvent(PlayerInteractEvent e) {
        final Player p = e.getPlayer();

        if (!savingBackPacks.contains(p)) {
            Action action = e.getAction();

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    if (doNotOpen.contains(e.getClickedBlock().getType())) {
                        return;
                    }

                }

                final ItemStack item = e.getItem();

                if (item != null) {
                    String backPackString = checkItem(item);

                    if (backPackString != null) {
                        final BackPack backPack = backPacksMap.get(backPackString);

                        if (backPack != null) {
                            if (p.hasPermission("backpacks.use." + backPack.getName()) || p.hasPermission("backpacks.*")) {
                                final String trimmedID = getTrimmedId(p);
                                final ItemMeta meta = item.getItemMeta();

                                playOpenSound(p, backPack);

                                switch (backPack.getType().toString()) {
                                    case "normal":
                                        SQL.checkTable("bp_" + backPack.getName() + "_" + trimmedID, new SQL.Callback<Boolean>() {

                                            @Override
                                            public void onSuccess(Boolean rs) {
                                                if (rs) {
                                                    SQL.getResult("SELECT * FROM bp_" + backPack.getName() + "_" + trimmedID,
                                                            new SQL.Callback<ResultSet>() {

                                                                @Override
                                                                public void onSuccess(ResultSet rs) {
                                                                    Inventory open = getInv(rs, p, backPack,
                                                                            getInventoryTitle(backPack, meta.getDisplayName()),
                                                                            true, null);

                                                                    if (open != null) {
                                                                        p.openInventory(open);
                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(Throwable e) {}

                                                            });

                                                } else {
                                                    SQL.query("CREATE TABLE IF NOT EXISTS bp_"+ backPack.getName() + "_" + trimmedID + "(position INT(100), material VARCHAR(100), " +
                                                            "durability INT(100), amount INT(100), name VARCHAR(100), lore VARCHAR(1000), enchantments VARCHAR(1000), " +
                                                            "potion VARCHAR(1000), nbt VARCHAR(1000))", new SQL.Callback<Boolean>() {

                                                        @Override
                                                        public void onSuccess(Boolean rs) {
                                                            openInvs.put(p, backPack);

                                                            p.openInventory(Bukkit.getServer().createInventory(p, backPack.getSlots(),
                                                                    getInventoryTitle(backPack, meta.getDisplayName())));
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
                                        openInvsOther.put(p, backPack);

                                        p.openInventory(p.getEnderChest());

                                        break;

                                    case "crafting":
                                        openInvsOther.put(p, backPack);

                                        p.openWorkbench(p.getLocation(), true);

                                        break;

                                    case "trash":
                                        openInvsOther.put(p, backPack);

                                        p.openInventory(Bukkit.getServer().createInventory(p, backPack.getSlots(),
                                                getInventoryTitle(backPack, meta.getDisplayName())));

                                    case "furnace":
                                        if (backPack.getFurnaceGui()) {
                                            SQL.getResult("SELECT * FROM bp_furnaces WHERE uuid='" + trimmedID + "'", new SQL.Callback<ResultSet>() {

                                                @Override
                                                public void onSuccess(ResultSet rs) {
                                                    try {
                                                        rs.beforeFirst();

                                                        if (rs.next()) {
                                                            openFurnace(p, backPack, getInventoryTitle(backPack, meta.getDisplayName()),
                                                                    Boolean.valueOf(rs.getString("ores")), Boolean.valueOf(rs.getString("food")),
                                                                    Boolean.valueOf(rs.getString("autoFill")), rs.getInt("coal"));

                                                        } else {
                                                            final Boolean ores = furnaceGui.getBoolean("ores.defaultOption");
                                                            final Boolean food = furnaceGui.getBoolean("food.defaultOption");
                                                            final Boolean autoFill = furnaceGui.getBoolean("autoFill.defaultOption");

                                                            SQL.query("INSERT INTO bp_furnaces (uuid, ores, food, autoFill, coal) VALUES ('" + trimmedID + "', '"+
                                                                    String.valueOf(ores) + "', '" +
                                                                    String.valueOf(food) + "', '" +
                                                                    String.valueOf(autoFill) + "', '0')", new SQL.Callback<Boolean>() {

                                                                @Override
                                                                public void onSuccess(Boolean rs) {
                                                                    openFurnace(p, backPack, item.getItemMeta().getDisplayName(), ores, food, autoFill, 0);
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

                                        break;

                                }

                            } else {
                                p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&',
                                        messages.getString("Help.noPermission")));
                            }

                        }

                    }

                }

            }

        } else {
            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messages.getString("Help.backPackSaving")));
        }

    }

    public static Inventory getInv(ResultSet rs, Player opener, BackPack backPack, String inventoryTitle, Boolean openerIsOwner, String ownerId) {
        if (openInvs.containsKey(opener) || openFurnaces.containsKey(opener) || openInvsOther.containsKey(opener)) {
            return null;
        }

        if (rs != null) {
            try {
                rs.beforeFirst();

                Inventory inv = Bukkit.getServer().createInventory(opener, backPack.getSlots(), inventoryTitle);

                while (rs.next()) {
                    String material = rs.getString("material");

                    ItemStack item = new ItemStack(Material.valueOf(material), rs.getInt("amount"));

                    item.setDurability((short) rs.getInt("durability"));

                    ItemMeta meta = item.getItemMeta();

                    String name = rs.getString("name");
                    String lore = rs.getString("lore");
                    String enchantment = rs.getString("enchantments");
                    String potion = rs.getString("potion");
                    String nbt = rs.getString("nbt");

                    if (!name.equals("")) {
                        meta.setDisplayName(name);

                        item.setItemMeta(meta);
                    }

                    if (!lore.equals("")) {
                        meta.setLore(Arrays.asList(rs.getString("lore").split("~")));

                        item.setItemMeta(meta);
                    }

                    if (!enchantment.equals("")) {
                        if (!enchantment.equals("MobSpawnerEgg/")) {
                            String[] enchantments = enchantment.substring(0, enchantment.length() - 1).split("/");

                            if (material.equals("ENCHANTED_BOOK")) {
                                EnchantmentStorageMeta storage = (EnchantmentStorageMeta) meta;

                                for (String enchant : enchantments) {
                                    String[] parts = enchant.split(":");

                                    Enchantment ench = Enchantment.getByName(parts[0]);
                                    int enchLvl = Integer.valueOf(parts[1]);

                                    storage.addStoredEnchant(ench, enchLvl, true);
                                }

                                item.setItemMeta(storage);

                            } else {
                                for (String enchant : enchantments) {
                                    String[] parts = enchant.split(":");

                                    Enchantment ench = Enchantment.getByName(parts[0]);
                                    int enchLvl = Integer.valueOf(parts[1]);

                                    item.addUnsafeEnchantment(ench, enchLvl);
                                    meta.addEnchant(ench, enchLvl, true);
                                }
                            }

                        }

                    }

                    if (!potion.equals("")) {
                        if (material.toLowerCase().contains("potion")) {
                            String[] parts = potion.split("/");

                            PotionMeta potionMeta = (PotionMeta) meta;

                            PotionData potionData = new PotionData(PotionType.valueOf(parts[0]), Boolean.parseBoolean(parts[1]),
                                    Boolean.parseBoolean(parts[2]));

                            potionMeta.setBasePotionData(potionData);
                            item.setItemMeta(potionMeta);

                        } else if (material.equals("MONSTER_EGG")) {
                            try {
                                Object nmsStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
                                Object nmsCompound = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);

                                if (nmsCompound == null) {
                                    nmsCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound").getConstructor().newInstance();
                                }

                                Object compound = nmsCompound.getClass().getConstructor().newInstance();

                                compound.getClass().getMethod("setString", String.class, String.class).invoke(compound, "id", potion);
                                nmsCompound.getClass().getMethod("set", String.class, Class.forName("net.minecraft.server." + version + ".NBTBase"))
                                        .invoke(nmsCompound, "EntityTag", compound);

                                Boolean mobSpawnerEgg = enchantment.equals("MobSpawnerEgg/");

                                if (mobSpawnerEgg) {
                                    nmsCompound.getClass().getMethod("setString", String.class, String.class)
                                            .invoke(nmsCompound, "MobSpawnerEgg", "MobSpawnerEgg");
                                }

                                nmsStack.getClass().getMethod("setTag", nmsCompound.getClass()).invoke(nmsStack, nmsCompound);

                                item = ((ItemStack) Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack").getMethod("asBukkitCopy", nmsStack.getClass()).invoke(null, nmsStack));

                                if (mobSpawnerEgg) {
                                    EnchantGlow.addGlow(item);
                                }

                            } catch (InstantiationException | InvocationTargetException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException exception) {
                                exception.printStackTrace();
                            }

                        } else if(material.equals("SKULL_ITEM")) {
                            SkullMeta skull = (SkullMeta) item.getItemMeta();

                            skull.setOwner(potion);

                            item.setItemMeta(skull);
                        }

                    }

                    if (nbt != null) {
                        if (!nbt.equals("")) {
                            try {
                                item = decodeNbt(nbt, item);
                            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    inv.setItem(rs.getInt("position"), item);
                }

                rs.close();

                openInvs.put(opener, backPack);

                if (!openerIsOwner) {
                    openInvsOwners.put(opener, ownerId);
                }

                return inv;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    private void openFurnace(final Player opener, BackPack backPack, String inventoryTitle, Boolean oresEnabled, Boolean foodEnabled,
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

        ItemMeta blankMeta = blank.getItemMeta();

        blankMeta.setDisplayName(ChatColor.GRAY + "");

        blank.setItemMeta(blankMeta);

        Inventory inv = Bukkit.getServer().createInventory(opener, 36, inventoryTitle);

        if (amountCoal != 0) {
            ItemStack coal = new ItemStack(Material.COAL, amountCoal);

            inv.setItem(35, coal);
        }

        for (int i = 0; i < 35; i++) {
            inv.setItem(i, blank);
        }

        inv.setItem(3, ores);
        inv.setItem(4, food);
        inv.setItem(5, autoFill);

        inv.setItem(12, oresToggle);
        inv.setItem(13, foodToggle);
        inv.setItem(14, autoFillToggle);

        openFurnaces.put(opener, backPack);
        openFurnacesInvs.put(opener, inv);

        opener.openInventory(inv);
    }

    public static String getInventoryTitle(BackPack backPack, String displayName) {
        String inventoryTitle = backPack.getInventoryTitle();

        if (inventoryTitle != null) {
            if (!inventoryTitle.equals("")) {
                return ChatColor.translateAlternateColorCodes('&', inventoryTitle);
            }
        }

        return displayName;
    }

    private void setMeta(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', furnaceGui.getString(name + ".name")));
        meta.setLore(getLore(name + ".description"));

        item.setItemMeta(meta);
    }

    static void setToggleMeta(ItemStack item, Boolean toggle) {
        ItemMeta meta = item.getItemMeta();

        String name;

        if (toggle) {
            name = ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("enabled"));
        } else {
            name = ChatColor.translateAlternateColorCodes('&', furnaceGui.getString("disabled"));
        }

        meta.setDisplayName(name);

        item.setItemMeta(meta);
    }

    private static List<String> getLore(String path) {
        Map<String, Object> loreSec = furnaceGui.getConfigurationSection(path).getValues(true);

        ArrayList<String> toReturn = new ArrayList<>();

        for (Map.Entry<String, Object> ent : loreSec.entrySet()) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', ent.getValue().toString()));
        }

        return toReturn;
    }

    static int getColor(Boolean bool) {
        if (bool) {
            return 5;
        }

        return 14;
    }

    public static void playOpenSound(Player p, BackPack backPack) {
        Sound open = backPack.getOpenSound();

        if (open != null) {
            p.playSound(p.getLocation(), open, 10, 1);
        }
    }

    static void playCloseSound(Player p, BackPack backPack) {
        Sound close = backPack.getCloseSound();

        if (close != null) {
            p.playSound(p.getLocation(), close, 10, 1);
        }
    }

}
