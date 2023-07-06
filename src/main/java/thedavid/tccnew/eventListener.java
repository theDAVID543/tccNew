package thedavid.tccnew;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.util.Objects;

public class eventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent e){
        e.setCancelled(true);
        String plainMessageString = PlainTextComponentSerializer.plainText().serialize(e.message());
        if(plainMessageString.isEmpty()){
            return;
        }
        String messageGsonString = GsonComponentSerializer.gson().serialize(e.message());
        String messagePlainString = PlainTextComponentSerializer.plainText().serialize(e.message());
        Component prefixcomponent = LegacyComponentSerializer.legacyAmpersand().deserialize(TccNew.chat.getPlayerPrefix(e.getPlayer()));
        String prefixString = GsonComponentSerializer.gson().serialize(prefixcomponent);
        if(messagePlainString.charAt(0) == '$'){
            if(!Objects.equals(pluginMessageHandler.playerTradeCooldown.get(e.getPlayer().getUniqueId()),null) && pluginMessageHandler.playerTradeCooldown.get(e.getPlayer().getUniqueId()) > 0){
                e.getPlayer().sendMessage(Component.text()
                        .append(Component.text("交易頻道 冷卻時間: " + pluginMessageHandler.playerTradeCooldown.get(e.getPlayer().getUniqueId()) + "秒").color(NamedTextColor.GOLD))
                );
                return;
            }
            messageGsonString = messageGsonString.replaceFirst("\\$","");
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF(e.getPlayer().getName());
            buf.writeUTF(prefixString);
            buf.writeUTF("trade");
            buf.writeUTF(messageGsonString);
            e.getPlayer().sendPluginMessage(TccNew.instance, "tcc:channel", buf.toByteArray());
            TextChannel tradeChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("trade");
            messagePlainString = messagePlainString.replaceFirst("\\$","");
            InteractiveChatDiscordSrvAddon.discordsrv.processChatMessage(e.getPlayer(), messagePlainString, "trade", false, e);
        }else if(messagePlainString.charAt(0) == '!'){
            if(!Objects.equals(pluginMessageHandler.playerAdCooldown.get(e.getPlayer().getUniqueId()),null) && pluginMessageHandler.playerAdCooldown.get(e.getPlayer().getUniqueId()) > 0){
                e.getPlayer().sendMessage(Component.text()
                        .append(Component.text("設施宣傳頻道 冷卻時間: " + pluginMessageHandler.playerAdCooldown.get(e.getPlayer().getUniqueId()) + "秒").color(NamedTextColor.AQUA))
                );
                return;
            }
            messageGsonString = messageGsonString.replaceFirst("!","");
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF(e.getPlayer().getName());
            buf.writeUTF(prefixString);
            buf.writeUTF("ad");
            buf.writeUTF(messageGsonString);
            e.getPlayer().sendPluginMessage(TccNew.instance, "tcc:channel", buf.toByteArray());
            messagePlainString = messagePlainString.replaceFirst("!","");
            InteractiveChatDiscordSrvAddon.discordsrv.processChatMessage(e.getPlayer(), messagePlainString, "ad", false, e);
        }else{
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF(e.getPlayer().getName());
            buf.writeUTF(prefixString);
            buf.writeUTF("general");
            buf.writeUTF(messageGsonString);
            e.getPlayer().sendPluginMessage(TccNew.instance, "tcc:channel", buf.toByteArray());
            InteractiveChatDiscordSrvAddon.discordsrv.processChatMessage(e.getPlayer(), messagePlainString, "general", false, e);
//            InteractiveChatDiscordSrvAddon.discordsrv.sendLeaveMessage();
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!pluginMessageHandler.changeServer.contains(e.getPlayer())){
                    TextChannel general = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("general");
                    final String uuid = e.getPlayer().getUniqueId().toString().replace("-","");
                    MessageEmbed messageEmbed = new EmbedBuilder().setColor(Color.RED).setAuthor(e.getPlayer().getName() + " 離開了伺服器", null, "https://crafatar.com/avatars/" + uuid + ".png?size=128&overlay").build();
                    general.sendMessageEmbeds(messageEmbed).queue();
                }else{
                    pluginMessageHandler.changeServer.remove(e.getPlayer());
                }
            }
        }.runTaskLater(TccNew.instance, 10L);
    }
}
