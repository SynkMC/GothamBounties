package cc.synkdev.gothamBounties.commands;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.Util;
import cc.synkdev.gothamBounties.guis.ConfirmGui;
import cc.synkdev.gothamBounties.guis.TopGui;
import cc.synkdev.gothamBounties.objects.Bounty;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BountyCommand implements CommandExecutor, TabExecutor {
    private final GothamBounties core = GothamBounties.getInstance();
    private CommandSender sender;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        this.sender = sender;
        switch (args.length) {
            case 0:
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    TopGui.gui().open(p);
                } else sender.sendMessage(core.prefix+ ChatColor.RED+"This command is only usable by players!");
                break;
            case 1:
                switch (args[0]) {
                    case "add":
                        sender.sendMessage(core.prefix+ChatColor.RED+"Usage: /bounty add <target> <value>");
                        break;
                    case "check":
                        if (checkPerm("bounty.command.check", true)) {
                            sender.sendMessage(core.prefix+ChatColor.RED+"Usage: /bounty check <player>");
                        }
                        break;
                    case "top":
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            TopGui.gui().open(p);
                        } else sender.sendMessage(core.prefix+ ChatColor.RED+"This command is only usable by players!");
                        break;
                }
                break;
            case 2:
                switch (args[0]) {
                    case "add":
                        sender.sendMessage(core.prefix + ChatColor.RED + "Usage: /bounty add <target> <value>");
                        break;
                    case "check":
                        if (checkPerm("bounty.command.check", true)) {
                            OfflinePlayer op = Util.getOfflinePlayer(args[1]);
                            if (op != null) {
                                if (Util.canSendBounty(op, null)) {
                                    sender.sendMessage(core.prefix+ChatColor.GOLD+op.getName()+" didn't send a bounty to anyone.");
                                } else {
                                    List<Bounty> bs = Util.getPlayersSentBounties(op);
                                    for (Bounty b : bs) {
                                        TextComponent main = new TextComponent(core.prefix+ChatColor.GOLD+op.getName()+" put a $"+b.getValue()+" bounty to "+b.getTarget().getName()+"'s head. ");
                                        TextComponent rm = new TextComponent(ChatColor.RED+""+ChatColor.BOLD+"[-]");
                                        rm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bounty remove "+b.getOrigin().getName()+" "+b.getTarget().getName()));
                                        main.addExtra(rm);
                                        if (sender instanceof Player) ((Player) sender).spigot().sendMessage(main);
                                        else sender.sendMessage(core.prefix+ChatColor.GOLD+op.getName()+" put a $"+b.getValue()+" bounty to "+b.getTarget().getName()+"'s head.");
                                        sender.sendMessage("");
                                    }
                                }

                                if (Util.getPlayersBounties(op).isEmpty()) {
                                    sender.sendMessage(core.prefix+ChatColor.GOLD+"This player has no bounties!");
                                } else {
                                    sender.sendMessage(core.prefix+ChatColor.GOLD+op.getName()+"'s bounties:");
                                    for (Bounty b : Util.getPlayersBounties(op)) {
                                        TextComponent main = new TextComponent(ChatColor.GOLD+"- $"+b.getValue()+" by "+b.getOrigin().getName()+" ");
                                        TextComponent rm = new TextComponent(ChatColor.RED+""+ChatColor.BOLD+"[-]");
                                        rm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bounty remove "+b.getOrigin()+" "+b.getTarget()));
                                        main.addExtra(rm);
                                        if (sender instanceof Player) ((Player) sender).spigot().sendMessage(main);
                                        else sender.sendMessage(ChatColor.GOLD+"- $"+b.getValue()+" by "+b.getOrigin().getName());
                                    }
                                }
                            } else sender.sendMessage(core.prefix+ChatColor.RED+"This player has never connected to the server before!");
                        }
                        break;
                }
                break;
            case 3:
                switch (args[0]) {
                    case "remove":
                        if (checkPerm("bounty.command.remove", true)) {
                            OfflinePlayer origin = Util.getOfflinePlayer(args[1]);
                            OfflinePlayer target = Util.getOfflinePlayer(args[2]);
                            if (origin != null) {
                                if (target != null) {
                                    List<Bounty> list = Util.getPlayersBounties(target);
                                    if (list.isEmpty()) {
                                        sender.sendMessage(ChatColor.RED+origin.getName() + "didn't put a bounty to "+target.getName()+"'s name!");
                                        return true;
                                    }

                                    Bounty b = null;
                                    for (Bounty bb : list) {
                                        if (Util.comparePlayers(origin, bb.getOrigin()) && Util.comparePlayers(target, bb.getTarget())) {
                                            b = bb;
                                        }
                                    }

                                    if (b == null) {
                                        sender.sendMessage(ChatColor.RED+origin.getName() + "didn't put a bounty to "+target.getName()+"'s name!");
                                        return true;
                                    }

                                    if (b.getTarget().isOnline()) {
                                        ((Player) b.getTarget()).sendMessage(core.prefix+ChatColor.GREEN+b.getOrigin().getName()+"'s bounty put to your head has been cancelled by an admin!");
                                    }

                                    if (b.getOrigin().isOnline()) {
                                        ((Player) b.getOrigin()).sendMessage(core.prefix+ChatColor.GREEN+b.getTarget().getName()+"'s bounty that you put to their head has been cancelled by an admin and refunded!");
                                    }
                                    core.bountyMap.remove(b);
                                    core.eco.depositPlayer(b.getOrigin(), b.getValue());
                                    sender.sendMessage(core.prefix+ChatColor.GREEN+"The bounty was successfully removed!");
                                } else {
                                    sender.sendMessage(ChatColor.RED+args[2]+" has never played on the server!");
                                    return true;
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED+args[1]+" has never played on the server!");
                                return true;
                            }
                        }
                        break;
                    case "add":
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            OfflinePlayer target = Util.getOfflinePlayer(args[1]);
                            if (target != null) {
                                if (!Util.canSendBounty(p, target)) {
                                    p.sendMessage(core.prefix+ChatColor.RED+"You can only put one bounty on the same player!");
                                    return false;
                                }

                                if (Util.comparePlayers(p, target)) {
                                    p.sendMessage(core.prefix + ChatColor.RED + "You can't add a bounty on yourself!");
                                    return true;
                                }
                                double d;
                                try {
                                    d = Double.parseDouble(args[2]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(core.prefix + ChatColor.RED + "The value you provided is incorrect.");
                                    break;
                                }
                                if (d == 0) {
                                    p.sendMessage(core.prefix + ChatColor.RED + "This value is too small!");
                                    return true;
                                }
                                if (d > core.eco.getBalance(p)) {
                                    Bukkit.getLogger().info(p.getName() + "'s balance:" + core.eco.getBalance(p));
                                    p.sendMessage(core.prefix + ChatColor.RED + "You don't have enough money to send this bounty!");
                                    return true;
                                }
                                Bounty b = new Bounty(p, target, d);
                                ConfirmGui.gui(b).open(p);
                            } else p.sendMessage(core.prefix+ChatColor.RED+"This player has never connected to the server before!");
                        } else sender.sendMessage(core.prefix+ ChatColor.RED+"This command is only usable by players!");
                        break;
                }
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        this.sender = sender;
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                list.add("add");
                list.add("top");
                if (checkPerm("bounty.command.check", false)) list.add("check");
                if (checkPerm("bounty.command.remove", false)) list.add("remove");
                break;
            case 2:
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("remove")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("remove") && checkPerm("bounty.command.remove",false)) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }
                }
        }
        return list;
    }
    private Boolean checkPerm(String s, Boolean msg) {
        if (msg && !sender.hasPermission(s)) {
            sender.sendMessage(core.prefix+ChatColor.RED+"You don't have permission to use this!");
            return false;
        }

        return sender.hasPermission(s);
    }
}
