package cc.synkdev.gothamBounties.commands;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.Util;
import cc.synkdev.gothamBounties.objects.Band;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("band|bands")
@Description("Main band command")
public class BandCommand extends BaseCommand {
    private final GothamBounties core = GothamBounties.getInstance();
    private final String prefix = ChatColor.translateAlternateColorCodes('&',"&r&8[&6GothamBands&8] » &r");

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Syntax("[name]")
    @Description("Create a guild of your own")
    public void onCreate(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(prefix+ChatColor.RED+"Please specify a name for your band!");
        }
        else {
            String name;
            if (args.length < 2) {
                name = args[0];
            } else {
                StringBuilder sb = new StringBuilder();

                int index = 0;
                for (int i = 0; i < args.length-1; i++) {
                    sb.append(args[i]).append(" ");
                    index = i;
                }
                sb.append(args[index+1]);
                name = sb.toString();
            }

            core.bandsMap.add(new Band(p, name));
            p.sendMessage(prefix+ChatColor.GREEN+"Your band has been created!");
        }
    }

    @Subcommand("invite")
    @Description("Invite a player to your band")
    @CommandCompletion("@allplayers:30")
    @Syntax("[player]")
    public void onInvite(Player p, String[] args) {
        Band b = Util.getBand(p);
        if (b == null) {
            p.sendMessage(prefix+ChatColor.RED+"You must be in a band to do this!");
            return;
        }

        if (!Util.comparePlayers(Bukkit.getOfflinePlayer(b.getLeader()), p)) {
            p.sendMessage(prefix+ChatColor.RED+"You must be the owner of the band to invite people!");
            return;
        }

        OfflinePlayer op = Util.getOfflinePlayer(args[0]);
        if (op != null) {
            if (Util.comparePlayers(op, p)) {
                p.sendMessage(prefix+ChatColor.RED+"You can't invite yourself!");
                return;
            }

            b.getInvites().add(op.getUniqueId());
            if (op.isOnline()) {
                ((Player) op).sendMessage(prefix+ChatColor.GREEN+"You have been invited to join the band "+b.getName()+"!");
            }
            core.updateData(b);
        } else {
            p.sendMessage(prefix+ChatColor.RED+"This player has never connected to the server before!");
        }
    }

    @Subcommand("leave")
    @Description("Leave your band")
    public void onLeave(Player p) {
        Band b = Util.getBand(p);
        if (b == null) {
            p.sendMessage(prefix+ChatColor.RED+"You are not in a band!!");
            return;
        }

        if (Util.comparePlayers(Bukkit.getOfflinePlayer(b.getLeader()), p)) {
            p.sendMessage(prefix+ChatColor.RED+"You are the leader of your band! Use /band disband!");
            return;
        }

        b.getMembers().remove(p);
        b.broadcast(prefix+ChatColor.GOLD+p.getName()+" left the band!");
        core.updateData(b);
    }

    @Subcommand("kick")
    @Syntax("[username]")
    @Description("Kick someone from your band")
    public void onKick(Player p, String[] args) {
        Band b = Util.getBand(p);
        if (b == null) {
            p.sendMessage(prefix+ChatColor.RED+"You are not in a band!!");
            return;
        }

        if (!Util.comparePlayers(Bukkit.getOfflinePlayer(b.getLeader()), p)) {
            p.sendMessage(prefix+ChatColor.RED+"You must be the leader of your band to use this!");
            return;
        }

        if (args.length < 1) {
            p.sendMessage(prefix+ChatColor.RED+"Please specify a player's name.");
            return;
        }

        OfflinePlayer oP = Util.getOfflinePlayer(args[0]);

        if (oP == null) {
            p.sendMessage(prefix+ChatColor.RED+"This player has never connected to the server before!");
            return;
        }

        if (!b.isMember(oP)) {
            p.sendMessage(prefix+ChatColor.RED+"This player is not in your band!");
            return;
        }

        b.broadcast(prefix+ChatColor.RED+ oP.getName()+" was kicked from the band!");
        b.getMembers().remove(oP.getUniqueId());
        core.updateData(b);
    }

    @Subcommand("disband")
    @Description("Disband your band")
    public void onDisband(Player p) {
        Band b = Util.getBand(p);
        if (b == null) {
            p.sendMessage(prefix+ChatColor.RED+"You are not in a band!!");
            return;
        }

        if (!Util.comparePlayers(Bukkit.getOfflinePlayer(b.getLeader()), p)) {
            p.sendMessage(prefix+ChatColor.RED+"You are not the leader of your band! Use /band leave!");
            return;
        }

        b.broadcast(prefix+ChatColor.GOLD+p.getName()+" disbanded the band!");
        core.bountiesMap.remove(b);
    }

    @Subcommand("join")
    @Description("Join a band that invited you")
    @Syntax("[band]")
    public void onJoin(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(prefix+ChatColor.RED+"Please specify a band's name!");
            return;
        }

        if (Util.getBand(p) != null) {
            p.sendMessage(prefix+ChatColor.RED+"Leave your current band first!");
            return;
        }

        Band b = Util.getBand(args[0]);
        if (b == null) {
            p.sendMessage(prefix+ChatColor.RED+"This band doesn't exist!");
            return;
        }

        if (!b.isInvited(p)) {
            p.sendMessage(prefix+ChatColor.RED+"You weren't invited to join this band!");
            return;
        }

        b.getInvites().remove(p);
        b.getMembers().add(p.getUniqueId());
        core.updateData(b);
        b.broadcast(prefix+ChatColor.GOLD+p.getName()+" joined the band!");
    }

    @Subcommand("info")
    @Description("Shows the list of members in your band")
    public void onInfo(Player p) {
        Band b = Util.getBand(p);
        if (b == null) {
            p.sendMessage(prefix+ChatColor.RED+"You are not in a band!");
            return;
        }

        p.sendMessage(prefix+ChatColor.GOLD+"Informations about "+b.getName());
        p.sendMessage(ChatColor.GOLD+"Leader: "+Bukkit.getOfflinePlayer(b.getLeader()).getName());

        String members;
        if (b.getMembers().isEmpty()) {
            p.sendMessage(ChatColor.GOLD+"No members");
            return;
        }

        if (b.getMembers().size()< 2) members = Bukkit.getOfflinePlayer(b.getMembers().get(0)).getName();
        else {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (int i = 0; i < b.getMembers().size()-1; i++) {
                sb.append(Bukkit.getOfflinePlayer(b.getMembers().get(i)).getName()).append(", ");
                index = i;
            }
            sb.append(Bukkit.getOfflinePlayer(b.getMembers().get(index+1)));
            members = sb.toString();
        }

        p.sendMessage(ChatColor.GOLD+"Members: "+members);

    }

    @Subcommand("top")
    @Description("Top bands by kills and playtime")
    public void onTop(Player p) {
        p.sendMessage(prefix+ChatColor.GOLD+"Top bands by kills and playtime:");
        for (int i = 0; i < 10; i++) {
            if (core.globalTop.size()>i) {
                p.sendMessage(ChatColor.GOLD+"#"+(i+1)+" - "+core.globalTop.get(i).getName());
            } else break;
        }
    }
}
