package coloryr.allmusic.side.velocity;

import coloryr.allmusic.AllMusic;
import coloryr.allmusic.AllMusicVelocity;
import coloryr.allmusic.api.ISide;
import coloryr.allmusic.hud.HudSave;
import coloryr.allmusic.hud.obj.SaveOBJ;
import coloryr.allmusic.music.play.PlayMusic;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

public class SideVelocity implements ISide {
    public static final Set<ServerConnection> TopServers = new CopyOnWriteArraySet<>();
    private boolean isOK(Player player, boolean in) {
        try {
            if (player == null)
                return false;
            if (AllMusic.getConfig().getNoMusicServer()
                    .contains(player.getCurrentServer().get().getServerInfo().getName()))
                return false;
            String name = player.getUsername();
            if (AllMusic.getConfig().getNoMusicPlayer().contains(player.getUsername()))
                return false;
            return !in || AllMusic.containNowPlay(name);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public void send(String data, String player, Boolean isplay) {
        if (AllMusicVelocity.plugin.server.getPlayer(player).isPresent()) {
            send(AllMusicVelocity.plugin.server.getPlayer(player).get(), data, isplay);
        } else {
            AllMusic.log.warning("§d[AllMusic]§c找不到玩家:" + player);
        }
    }

    @Override
    public void send(String data, Boolean isplay) {
        try {
            Collection<Player> values = AllMusicVelocity.plugin.server.getAllPlayers();
            for (Player player : values) {
                if (isplay && !isOK(player, false))
                    continue;
                send(player, data, isplay);
            }
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c歌曲发送发生错误");
            e.printStackTrace();
        }
    }

    @Override
    public int getAllPlayer() {
        return AllMusicVelocity.plugin.server.getPlayerCount();
    }

    @Override
    public void bq(String data) {
        if (AllMusic.getConfig().isMessageLimit()
                && data.length() > AllMusic.getConfig().getMessageLimitSize()) {
            data = data.substring(0, 30) + "...";
        }
        AllMusicVelocity.plugin.server.sendMessage(Component.text(data));
    }

    @Override
    public void bqt(String data) {
        if (AllMusic.getConfig().isMessageLimit()
                && data.length() > AllMusic.getConfig().getMessageLimitSize()) {
            data = data.substring(0, 30) + "...";
        }
        AllMusicVelocity.plugin.server.sendMessage(Component.text(data));
    }

    @Override
    public boolean NeedPlay() {
        int online = 0;
        for (RegisteredServer server : AllMusicVelocity.plugin.server.getAllServers()) {
            if (AllMusic.getConfig().getNoMusicServer().contains(server.getServerInfo().getName()))
                continue;
            for (Player player : server.getPlayersConnected())
                if (!AllMusic.getConfig().getNoMusicPlayer().contains(player.getUsername()))
                    online++;
        }
        return online > 0;
    }

    @Override
    public void sendHudLyric(String data) {
        try {
            for (Player player : AllMusicVelocity.plugin.server.getAllPlayers()) {
                if (!isOK(player, true))
                    continue;
                SaveOBJ obj = HudSave.get(player.getUsername());
                if (!obj.isEnableLyric())
                    continue;
                send(player, "[Lyric]" + data, null);
            }
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c歌词发送出错");
            e.printStackTrace();
        }
    }

    @Override
    public void sendHudInfo(String data) {
        try {
            for (Player player : AllMusicVelocity.plugin.server.getAllPlayers()) {
                if (!isOK(player, true))
                    continue;
                SaveOBJ obj = HudSave.get(player.getUsername());
                if (!obj.isEnableInfo())
                    continue;
                send(player, "[Info]" + data, null);
            }
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c歌词信息发送出错");
            e.printStackTrace();
        }
    }

    @Override
    public void sendHudList(String data) {
        try {
            for (Player player : AllMusicVelocity.plugin.server.getAllPlayers()) {
                if (!isOK(player, true))
                    continue;
                String name = player.getUsername();
                SaveOBJ obj = HudSave.get(name);
                if (!obj.isEnableList())
                    continue;
                send(player, "[List]" + data, null);
            }
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c歌曲列表发送出错");
            e.printStackTrace();
        }
    }

    @Override
    public void sendHudSaveAll() {
        for (Player players : AllMusicVelocity.plugin.server.getAllPlayers()) {
            String Name = players.getUsername();
            try {
                SaveOBJ obj = HudSave.get(Name);
                String data = new Gson().toJson(obj);
                send(data, Name, null);
            } catch (Exception e1) {
                AllMusic.log.warning("§d[AllMusic]§c数据发送发生错误");
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void clearHud(String player) {
        send("[clear]", player, null);
    }

    @Override
    public void clearHudAll() {
        try {
            Collection<Player> values = AllMusicVelocity.plugin.server.getAllPlayers();
            for (Player players : values) {
                send(players, "[clear]", null);
            }
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c歌词发生出错");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessaget(Object obj, String message) {
        CommandSource sender = (CommandSource) obj;
        sender.sendMessage(Component.text(message));
    }

    @Override
    public void sendMessage(Object obj, String message) {
        CommandSource sender = (CommandSource) obj;
        sender.sendMessage(Component.text(message));
    }

    @Override
    public void sendMessageRun(Object obj, String message, String end, String command) {
        CommandSource sender = (CommandSource) obj;
        TextComponent send = Component.text(message + end)
                .clickEvent(ClickEvent.runCommand(command));
        sender.sendMessage(send);
    }

    @Override
    public void sendMessageSuggest(Object obj, String message, String end, String command) {
        CommandSource sender = (CommandSource) obj;
        TextComponent send = Component.text(message + end)
                .clickEvent(ClickEvent.suggestCommand(command));
        sender.sendMessage(send);
    }

    @Override
    public void runTask(Runnable run) {
        AllMusicVelocity.plugin.server.getScheduler().buildTask(AllMusicVelocity.plugin, run).schedule();
    }

    @Override
    public void reload() {
        new AllMusic().init(AllMusicVelocity.plugin.dataDirectory.toFile());
    }

    @Override
    public boolean checkPermission(String player, String permission) {
        try {
            if (AllMusic.getConfig().getAdmin().contains(player))
                return false;
            Player player1 = AllMusicVelocity.plugin.server.getPlayer(player).get();
            player1.hasPermission(permission);
        } catch (NoSuchElementException ignored) {

        }
        return true;
    }

    @Override
    public void task(Runnable run, int delay) {
        AllMusicVelocity.plugin.server.getScheduler().buildTask(AllMusicVelocity.plugin, run)
                .delay(delay, TimeUnit.MICROSECONDS).schedule();
    }

    public static void sendAllToServer(ServerConnection server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(0);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF(AllMusic.getMessage().getPAPI().getNoMusic());
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getName());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(1);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getAl());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(2);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getAlia());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(3);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getAuthor());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(4);
        if (PlayMusic.nowPlayMusic == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.nowPlayMusic.getCall());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(5);
        out.writeInt(PlayMusic.getSize());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(6);
        out.writeUTF(PlayMusic.getAllList());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());
    }

    public static void sendLyricToServer(ServerConnection server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(7);
        if (PlayMusic.lyricItem == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.lyricItem.getLyric());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());

        out = ByteStreams.newDataOutput();
        out.writeInt(8);
        if (PlayMusic.lyricItem == null || PlayMusic.lyricItem.getTlyric() == null)
            out.writeUTF("");
        else
            out.writeUTF(PlayMusic.lyricItem.getTlyric());
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());
    }

    public static void sendPingToServer(ServerConnection server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(200);
        server.sendPluginMessage(AllMusicVelocity.channelBC, out.toByteArray());
    }

    @Override
    public void ping(){
        Iterator<ServerConnection> iterator = TopServers.iterator();
        while (iterator.hasNext()) {
            ServerConnection server = iterator.next();
            try{
                sendPingToServer(server);
            }catch (Exception e){
                iterator.remove();
            }
        }
    }

    @Override
    public void updateInfo() {
        for(ServerConnection server : TopServers) {
            try  {
                sendAllToServer(server);
            } catch(Exception e) {
                TopServers.remove(server);
            }
        }
    }

    @Override
    public void updateLyric() {
        for(ServerConnection server : TopServers) {
            try {
                sendLyricToServer(server);
            } catch(Exception e) {
                TopServers.remove(server);
            }
        }
    }

    private void send(Player players, String data, Boolean isplay) {
        if (players == null)
            return;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
            buf.writeByte(666);
            buf.writeBytes(bytes);
            runTask(() -> players.sendPluginMessage(AllMusicVelocity.plugin.channel, buf.array()));
            if (isplay != null) {
                if (isplay) {
                    AllMusic.addNowPlayPlayer(players.getUsername());
                } else {
                    AllMusic.removeNowPlayPlayer(players.getUsername());
                }
            }
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c数据发送发生错误");
            e.printStackTrace();
        }
    }
}
