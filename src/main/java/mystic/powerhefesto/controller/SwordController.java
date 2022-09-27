package mystic.powerhefesto.controller;

import com.google.common.base.Strings;
import me.dpohvar.powernbt.api.NBTCompound;
import mystic.powerhefesto.PowerHefesto;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftItem;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SwordController {

    public PowerHefesto main = PowerHefesto.getPlugin(PowerHefesto.class);

    public ItemStack item;

    public SwordController(ItemStack item) {
        this.item = item;
    }

    public boolean hasUpgrades() {
        if (item.getItemMeta().hasLore()) {
            return getSwordLevel() > 0;
        }
        return false;
    }

    public double getMultiplier(Player player) {
        double multiplier = 1.0;

        if (player.hasPermission("hefestoplugin.mult.vip.Whis")) {
            multiplier = 1.2;
        }

        if (player.hasPermission("hefestoplugin.mult.vip.Zeno")) {
            multiplier = 1.4;
        }

        return multiplier;
    }

    public int getSwordLevel() {
        return Integer.parseInt(item.getItemMeta().getLore().get(1).split("§7Nivel: §f")[1]);
    }

    public double getUpgradePerLife(double life) {
        double sangramento = 0;

        if (life <= 10000000) {
            sangramento = 0.1;
        }

        if (life > 10000000) {
            sangramento = 0.2;
        }

        if (life >= 50000000) {
            sangramento = 0.4;
        }

        return sangramento;
    }

    public double getUpgradePercent() {
        return Double.parseDouble(item.getItemMeta().getLore().get(2).split("§7Sangramento: §f")[1].replace("%", ""));
    }

    public NBTTagList getAttrList(net.minecraft.server.v1_7_R4.ItemStack nms) {
        if (nms.tag == null) {
            nms.tag = new NBTTagCompound();
        }
        NBTTagList attrmod = nms.tag.getList("AttributeModifiers", 10);
        if (attrmod == null) {
            nms.tag.set("AttributeModifiers", new NBTTagList());
        }
        return nms.tag.getList("AttributeModifiers", 10);
    }


    public void setDamageAtributes(Player player, int damage) {

        if (countDamageAtributes(player) > 0) {
            net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(player.getItemInHand());
            NBTTagCompound compound = (nms.hasTag() ? nms.getTag() : new NBTTagCompound());
            NBTTagList attrmod = getAttrList(nms);

            for (int i = 0; i < attrmod.size(); ++i) {
                NBTTagCompound c = attrmod.get(i);
                String atribute = c.getString("AttributeName");
                if (atribute.equals("generic.attackDamage")) {
                    c.setDouble("Amount", damage);
                    Bukkit.broadcastMessage("Dano: " + c.getDouble("Amount"));
                }
            }
            compound.set("AttributeModifiers", attrmod);
            nms.setTag(compound);
            player.getInventory().addItem(CraftItemStack.asCraftMirror(nms));
            player.getInventory().remove(player.getItemInHand());
            player.updateInventory();

        }
    }

    public double countDamageAtributes(Player player) {
        double quantity = 0;
        if (!(player.getItemInHand() == null) && player.getItemInHand().getType() != Material.AIR) {
            net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(player.getItemInHand());
            NBTTagList attrmod = getAttrList(nms);
            for (int i = 0; i < attrmod.size(); ++i) {
                NBTTagCompound c = attrmod.get(i);
                String atribute = c.getString("AttributeName");
                if (atribute.equals("generic.attackDamage")) {
                    quantity = quantity + c.getInt("Amount");
                }
            }
        }
        return quantity;
    }

    public void upgradeSwordLevel(Player player) {
        int money = (getSwordLevel() + 1) * 150000;
        double damage = (100000 * getMultiplier(player)) + countDamageAtributes(player);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lores = new ArrayList<>();
        lores.add("§7Upgrades");
        lores.add("§7Nivel: §f" + (getSwordLevel() + 1));
        lores.add("§7Sangramento: §f0%");
        lores.add("§8[" + getProgressBar(0, 100, 25, '|', ChatColor.GREEN, ChatColor.GRAY) + "§8]");
        lores.add("§7Forjador: §f" + player.getName());
        meta.setLore(lores);
        item.setItemMeta(meta);
        setDamageAtributes(player, (int) damage);
        player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 0.5f);
        main.econ.withdrawPlayer(player, money);
        player.sendMessage("§a§lGG §aVoce upou sua espada para o nivel §f" + getSwordLevel());
        player.sendMessage("§aAgora sua espada esta com o dano em: §f" + format(damage));
    }

    public String getProgressBar(double current, int max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    public String format(Double value) {
        String[] suffix = new String[]{"K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D"};
        int size = (value.intValue() != 0) ? (int) Math.log10(value) : 0;
        if (size >= 3) {
            while (size % 3 != 0) {
                size = size - 1;
            }
        }
        double notation = Math.pow(10, size);
        return (size >= 3) ? +(Math.round((value / notation) * 100) / 100.0d) + suffix[(size / 3) - 1] : +value + "";
    }

}
