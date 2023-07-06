package thedavid.tccnew;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class commandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // this is just an example! Select your sender as you wish.
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        buf.writeUTF("test");
        if (sender instanceof Player) {
            ((Player) sender).sendPluginMessage(TccNew.instance, "tcc:channel", buf.toByteArray());
        } else {
            // this will send the message for each player or none, if no players are online
            Bukkit.getServer().sendPluginMessage(TccNew.instance, "tcc:channel", buf.toByteArray());
        }
        return true;
    }
}
