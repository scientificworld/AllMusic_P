package coloryr.allmusic.side.bc;

import coloryr.allmusic.AllMusic;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventBC implements Listener {
    @EventHandler
    public void onPlayerquit(final PlayerDisconnectEvent event) {
        AllMusic.removeNowPlayPlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onLoginEvent(PostLoginEvent event) {
        AllMusic.joinPlay(event.getPlayer().getName());
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (event.getTag().equals(AllMusic.channelBC)) {
            event.setCancelled(true);
            if (event.getSender() instanceof Server) {
                Server server = (Server) event.getSender();
                SideBC.TopServers.add(server);
                SideBC.sendAllToServer(server);
                SideBC.sendLyricToServer(server);
            }
        }
    }
}
