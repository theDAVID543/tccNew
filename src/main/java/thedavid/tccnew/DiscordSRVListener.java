package thedavid.tccnew;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
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

    private final Plugin plugin;
    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }
    TextChannel general = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("general");
    TextChannel trade = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("trade");
    TextChannel ad = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("ad");
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new discordMessageListener(plugin));
    }
    @Subscribe
    public void discordMessageProcessed(GameChatMessagePostProcessEvent e) {
        if(e.getChannel().equals("global")){
            e.setCancelled(true);
        }
    }
}
