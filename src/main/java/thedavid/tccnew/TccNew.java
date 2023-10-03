package thedavid.tccnew;

import github.scarsz.discordsrv.DiscordSRV;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TccNew extends JavaPlugin {
    public static JavaPlugin instance;
    public DiscordSRVListener discordSRVListener;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        discordSRVListener = new DiscordSRVListener(this);
        DiscordSRV.api.subscribe(discordSRVListener);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "tcc:channel");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "tcc:channel", new pluginMessageHandler());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "tcc:discord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "tcc:discord", new pluginMessageHandler());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "tcc:cooldown", new pluginMessageHandler());
        Objects.requireNonNull(Bukkit.getPluginCommand("test")).setExecutor(new commandHandler());
        Bukkit.getPluginManager().registerEvents(new eventListener(), instance);
        setupChat();
        new BukkitRunnable(){
            @Override
            public void run() {
                Set<UUID> keySet = pluginMessageHandler.playerTradeCooldown.keySet();
                for(UUID uuid : keySet){
                    pluginMessageHandler.playerTradeCooldown.put(uuid, pluginMessageHandler.playerTradeCooldown.get(uuid) - 1);
                    if(pluginMessageHandler.playerTradeCooldown.get(uuid) <= 0){
                        pluginMessageHandler.playerTradeCooldown.remove(uuid);
                    }
                }

                keySet = pluginMessageHandler.playerAdCooldown.keySet();
                for(UUID uuid : keySet){
                    pluginMessageHandler.playerAdCooldown.put(uuid, pluginMessageHandler.playerAdCooldown.get(uuid) - 1);
                    if(pluginMessageHandler.playerAdCooldown.get(uuid) <= 0){
                        pluginMessageHandler.playerAdCooldown.remove(uuid);
                    }
                }
            }
        }.runTaskTimerAsynchronously(instance, 220, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        DiscordSRV.api.unsubscribe(discordSRVListener);
    }
    public static Chat chat = null;
    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
    }
}
