package coloryr.allmusic.side.velocity;

import coloryr.allmusic.AllMusic;
import coloryr.allmusic.side.bc.SideBC;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ServerConnection;

public class EventVelocity {
    @Subscribe
    public void onPlayerQuit(final DisconnectEvent event) {
        AllMusic.removeNowPlayPlayer(event.getPlayer().getUsername());
    }

    @Subscribe
    public void onPostLoginEvent(final PostLoginEvent event) {
        AllMusic.removeNowPlayPlayer(event.getPlayer().getUsername());
    }

    @Subscribe
    public void onPluginMessageEvent(final PluginMessageEvent event){
        if (event.getIdentifier().getId().equals(AllMusic.channelBC)) {
            event.setResult(PluginMessageEvent.ForwardResult.handled());
            if (event.getSource() instanceof ServerConnection) {
                ServerConnection server = (ServerConnection) event.getSource();
                SideVelocity.TopServers.add(server);
                SideVelocity.sendAllToServer(server);
                SideVelocity.sendLyricToServer(server);
            }
        }
    }
}
