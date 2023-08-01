package thedavid.tccnew;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class discordMessageListener extends ListenerAdapter {
    private final Plugin plugin;


    public discordMessageListener(Plugin plugin) {
        this.plugin = plugin;
    }
    TextChannel general = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("general");
    TextChannel trade = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("trade");
    TextChannel ad = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("ad");
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getAuthor().isBot()){
            return;
        }
        Map<String, UUID> link = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
        String message = e.getMessage().getContentRaw();
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        OfflinePlayer author = Bukkit.getOfflinePlayer(link.get(e.getAuthor().getId()));
        buf.writeUTF(author.getName());
        if(e.getChannel() == trade) {
            if(!Objects.equals(pluginMessageHandler.playerTradeCooldown.get(link.get(e.getAuthor().getId())),null) && pluginMessageHandler.playerTradeCooldown.get(link.get(e.getAuthor().getId())) > 0){
                e.getMessage().delete().queue();
                DiscordUtil.privateMessage(e.getAuthor(),"交易頻道 冷卻時間: " + pluginMessageHandler.playerTradeCooldown.get(link.get(e.getAuthor().getId())) + "秒");
                return;
            }
            buf.writeUTF("trade");
            pluginMessageHandler.playerTradeCooldown.put(link.get(e.getAuthor().getId()), 300);
        }else if(e.getChannel() == ad ){
            if(!Objects.equals(pluginMessageHandler.playerAdCooldown.get(link.get(e.getAuthor().getId())),null) && pluginMessageHandler.playerAdCooldown.get(link.get(e.getAuthor().getId())) > 0){
                e.getMessage().delete().queue();
                DiscordUtil.privateMessage(e.getAuthor(),"設施宣傳頻道 冷卻時間: " + pluginMessageHandler.playerAdCooldown.get(link.get(e.getAuthor().getId())) + "秒");
                return;
            }
            buf.writeUTF("ad");
            pluginMessageHandler.playerAdCooldown.put(link.get(e.getAuthor().getId()), 300);
        }else if(e.getChannel() == general){
            buf.writeUTF("general");
        }else{
            return;
        }
        buf.writeUTF(message);
        buf.writeUTF(e.getMessage().getId());
        Bukkit.getServer().sendPluginMessage(plugin, "tcc:discord", buf.toByteArray());
        Bukkit.getLogger().info(e.getAuthor() + " > " + e.getMessage());
    }
}
