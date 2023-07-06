package thedavid.tccnew;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class pluginMessageHandler implements PluginMessageListener {
    public static Set<OfflinePlayer> changeServer = new HashSet<>();
    public static Map<UUID, Integer> playerTradeCooldown = new HashMap<>();
    public static Map<UUID, Integer> playerAdCooldown = new HashMap<>();
    @Override
    public void onPluginMessageReceived(@NotNull String pluginChannel, @NotNull Player player, @NotNull byte[] bytes) {
        if(pluginChannel.equals("tcc:discord")){
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String event = in.readUTF();
            String inPlayer = in.readUTF();
            if(event.equals("change")){
                Bukkit.getLogger().info("received change server plugin message");
                changeServer.add(Bukkit.getOfflinePlayer(inPlayer));
            }else if(event.equals("login")){
                Bukkit.getLogger().info("received login plugin message");
                TextChannel general = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("general");
                final String uuid = Bukkit.getOfflinePlayer(inPlayer).getUniqueId().toString().replace("-","");
                MessageEmbed messageEmbed = new EmbedBuilder().setColor(Color.GREEN).setAuthor(inPlayer + " 加入了伺服器", null, "https://crafatar.com/avatars/" + uuid + ".png?size=128&overlay").build();
                general.sendMessageEmbeds(messageEmbed).queue();
            }
        }else if(pluginChannel.equals("tcc:channel")){
            Bukkit.getLogger().info("received tcc:channel");
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String inPlayer = in.readUTF();
            String prefix = in.readUTF();
            String channel = in.readUTF();
            String message = in.readUTF();
            Component prefixComponent = GsonComponentSerializer.gson().deserialize(prefix);
            String plainPrefix = LegacyComponentSerializer.legacyAmpersand().serialize(prefixComponent);
            Component messageComponent = GsonComponentSerializer.gson().deserialize(message);
            Component fullMessageComponent;
            if(channel.equals("trade")){
                fullMessageComponent = Component.text().append(Component.text("$ ").color(NamedTextColor.GOLD)).build();
            }else if(channel.equals("ad")) {
                fullMessageComponent = Component.text().append(Component.text("! ").color(NamedTextColor.AQUA)).build();
            }else if(channel.equals("general")) {
                fullMessageComponent = Component.text().build();
            }else{
                return;
            }
            fullMessageComponent = fullMessageComponent
                    .append(prefixComponent)
                    .append(Component.text(inPlayer).color(NamedTextColor.WHITE))
                    .append(Component.text("> ").color(NamedTextColor.WHITE))
                    .append(messageComponent);
            for(Player loopPlayer : Bukkit.getOnlinePlayers()){
                InteractiveChatAPI.sendMessage(loopPlayer, fullMessageComponent);
            }
        }else if(pluginChannel.equals("tcc:cooldown")){
            Bukkit.getLogger().info("received tcc:cooldown");
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String playerUUID = in.readUTF();
            String channel = in.readUTF();
            String time = in.readUTF();
            UUID uuid = UUID.fromString(playerUUID);
            if(channel.equals("trade")){
                playerTradeCooldown.put(uuid, Integer.parseInt(time));
            }else if(channel.equals("ad")){
                playerAdCooldown.put(uuid, Integer.parseInt(time));
            }
        }
    }
}
