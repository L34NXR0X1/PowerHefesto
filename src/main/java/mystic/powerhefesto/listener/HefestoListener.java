package mystic.powerhefesto.listener;

import mystic.powerhefesto.PowerHefesto;
import mystic.powerhefesto.controller.SwordController;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HefestoListener implements Listener {

    PowerHefesto main = PowerHefesto.getPlugin(PowerHefesto.class);


    @EventHandler
    public void onDamage(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.DIAMOND_SWORD) return;

            if (!event.getEntity().getType().toString().equals("CUSTOMNPCS_CUSTOMNPC")) return;

            ItemStack item = player.getItemInHand();

            SwordController swordController = new SwordController(item);

            if (item.getItemMeta().hasLore()) {

                if (item.getItemMeta().getLore().contains("§7Upgrades")) {

                    if (swordController.getUpgradePercent() >= 100) return;

                    double percentnow = swordController.getUpgradePercent() + swordController.getUpgradePerLife(event.getEntity().getMaxHealth());
                    DecimalFormat numberFormat = new DecimalFormat("#.0");
                    ItemMeta meta = item.getItemMeta();
                    ArrayList<String> lores = new ArrayList<>();
                    lores.add("§7Upgrades");
                    lores.add("§7Nivel: §f" + swordController.getSwordLevel());
                    lores.add("§7Sangramento: §f" + (percentnow < 1 ? "0" : "") + numberFormat.format(percentnow) + "%");
                    lores.add("§8[" + swordController.getProgressBar(swordController.getUpgradePercent(), 100, 25, '|', ChatColor.GREEN, ChatColor.GRAY) + "§8]");
                    if (swordController.hasUpgrades()) lores.add("§7Forjador: §f" + player.getName());
                    meta.setLore(lores);
                    item.setItemMeta(meta);

                } else {

                    ItemMeta meta = item.getItemMeta();
                    ArrayList<String> lores = new ArrayList<>();
                    lores.add("§7Upgrades");
                    lores.add("§7Nivel: §f0");
                    lores.add("§7Sangramento: §f0.0%");
                    lores.add("§8[" + swordController.getProgressBar(0, 100, 25, '|', ChatColor.GREEN, ChatColor.GRAY) + "§8]");
                    meta.setLore(lores);
                    item.setItemMeta(meta);

                }

            } else {

                ItemMeta meta = item.getItemMeta();
                ArrayList<String> lores = new ArrayList<>();
                lores.add("§7Upgrades");
                lores.add("§7Nivel: §f0");
                lores.add("§7Sangramento: §f0.0%");
                lores.add("§8[" + swordController.getProgressBar(0, 100, 25, '|', ChatColor.GREEN, ChatColor.GRAY) + "§8]");
                meta.setLore(lores);
                item.setItemMeta(meta);

            }
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission("itemizer.attribute")){
            PermissionsEx permissionsEx = PermissionsEx.getPlugin(PermissionsEx.class);
            permissionsEx.getPermissionsManager().getUser(player).removePermission("itemizer.attribute");
        }

        if(event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getTypeId() == 118) {

            if (player.getItemInHand() == null) return;

            ItemStack item = player.getItemInHand();

            if (item.getItemMeta().getLore().contains("§7Upgrades")) {

                SwordController swordController = new SwordController(item);

                int money = (swordController.getSwordLevel() + 1) * 150000;

                if (main.econ.getBalance(player) < money) {
                    player.sendMessage("§cVoce não possui dinheiro para isso.");
                    player.sendMessage("§cPara upar sua espada voce precisa de: §f" + swordController.format((double) money));
                    return;
                }

                if (swordController.getUpgradePercent() >= 100) {

                    swordController.upgradeSwordLevel(player);

                } else {

                    player.sendMessage("§cPara upar sua espada voce deve possuir 100% de sangramento!!!");
                }
            }
        }
    }

}
