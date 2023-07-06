package thedavid.tccnew;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DiscordSRVListener {
    TextChannel general = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("general");
    TextChannel trade = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("trade");
    TextChannel ad = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("ad");

    private final Plugin plugin;
    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void messageSentOnDiscord(DiscordGuildMessagePreProcessEvent e) {
//        e.setCancelled(true);
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
                e.setCancelled(true);
                return;
            }
            buf.writeUTF("trade");
            pluginMessageHandler.playerTradeCooldown.put(link.get(e.getAuthor().getId()), 300);
        }else if(e.getChannel() == ad ){
            if(!Objects.equals(pluginMessageHandler.playerAdCooldown.get(link.get(e.getAuthor().getId())),null) && pluginMessageHandler.playerAdCooldown.get(link.get(e.getAuthor().getId())) > 0){
                e.getMessage().delete().queue();
                DiscordUtil.privateMessage(e.getAuthor(),"設施宣傳頻道 冷卻時間: " + pluginMessageHandler.playerAdCooldown.get(link.get(e.getAuthor().getId())) + "秒");
                e.setCancelled(true);
                return;
            }
            buf.writeUTF("ad");
            pluginMessageHandler.playerAdCooldown.put(link.get(e.getAuthor().getId()), 300);
        }else if(e.getChannel() == general){
            buf.writeUTF("general");
        }else{
            e.setCancelled(true);
            return;
        }
        buf.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(TccNew.instance, "tcc:discord", buf.toByteArray());
        e.setCancelled(true);
    }
    @Subscribe
    public void discordMessageProcessed(GameChatMessagePostProcessEvent e) {
//        String[] message = e.getProcessedMessage().split(" » ",2);
//        Bukkit.getLogger().info(Arrays.toString(message));
//        if(message[1].charAt(1) == '$'){
//            Bukkit.getLogger().info("canceled");
//            e.setCancelled(true);
//            e.setChannel("trade");
//        } else if (message[1].charAt(1) == '!') {
//            Bukkit.getLogger().info("canceled");
//            e.setCancelled(true);
//            e.setChannel("ad");
//        }
        if(e.getChannel().equals("global")){
            e.setCancelled(true);
        }
    }
}
