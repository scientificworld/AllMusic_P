package coloryr.allmusic.side.bukkit;

import coloryr.allmusic.AllMusic;
import coloryr.allmusic.AllMusicBukkit;
import coloryr.allmusic.music.api.TopSongInfo;
import coloryr.allmusic.music.lyric.TopLyricItem;
import coloryr.allmusic.music.play.PlayGo;
import coloryr.allmusic.music.play.PlayMusic;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PluginMessage implements PluginMessageListener {
    private static ScheduledExecutorService service;

    public static int size;
    public static String allList;
    public static boolean update = false;
    private final TopSongInfo info;
    private final TopLyricItem lyric;

    public PluginMessage() {
        info = (TopSongInfo) PlayMusic.nowPlayMusic;
        lyric = (TopLyricItem) PlayMusic.lyricItem;

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(PluginMessage::clear, 0, 30, TimeUnit.SECONDS);
    }

    private static void clear(){
        update = false;
    }

    public void stop(){
        service.shutdownNow();
    }

    public static void startUpdate() {
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player == null)
            return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("allmusic");
        player.sendPluginMessage(AllMusicBukkit.plugin, AllMusic.channelBC, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals(AllMusic.channelBC)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        int type = in.readInt();
        update = true;
        switch (type) {
            case 0:
                info.setName(in.readUTF());
                break;
            case 1:
                info.setAl(in.readUTF());
                break;
            case 2:
                info.setAlia(in.readUTF());
                break;
            case 3:
                info.setAuthor(in.readUTF());
                break;
            case 4:
                info.setCall(in.readUTF());
                break;
            case 5:
                size = in.readInt();
                break;
            case 6:
                allList = in.readUTF();
                break;
            case 7:
                lyric.setLyric(in.readUTF());
                break;
            case 8:
                lyric.setTlyric(in.readUTF());
                break;
        }
    }
}
