package Color_yr.AllMusic.side.sideBukkit;

import Color_yr.AllMusic.AllMusic;
import Color_yr.AllMusic.AllMusicBukkit;
import Color_yr.AllMusic.api.ISide;
import Color_yr.AllMusic.hudsave.HudSave;
import Color_yr.AllMusic.musicPlay.sendHud.SaveOBJ;
import com.google.gson.Gson;
//import net.minecraft.util.io.netty.buffer.ByteBuf;
//import net.minecraft.util.io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class SideBukkit implements ISide {
    private static Class ByteBufC;
    private static Class UnpooledC;
    private static Method bufferM;
    private static Method writeByteM;
    private static Method writeBytesM;
    private static Method arrayM;
    static {
        try {
            ByteBufC = Class.forName("net.minecraft.util.io.netty.buffer.ByteBuf");
        } catch (Exception e) {
            try {
                ByteBufC = Class.forName("io.netty.buffer.ByteBuf");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (ByteBufC != null) {
                try {
                    arrayM = ByteBufC.getMethod("array");
                    writeByteM = ByteBufC.getMethod("writeByte", int.class);
                    writeBytesM = ByteBufC.getMethod("writeBytes", byte[].class);
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
            }
        }
        try {
            UnpooledC = Class.forName("net.minecraft.util.io.netty.buffer.Unpooled");
        } catch (Exception e) {
            try {
                UnpooledC = Class.forName("io.netty.buffer.Unpooled");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (UnpooledC != null) {
                try {
                    bufferM = UnpooledC.getMethod("buffer", int.class);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private boolean isOK(String player, boolean in) {
        if (AllMusic.getConfig().getNoMusicPlayer().contains(player))
            return false;
        return !in || AllMusic.containNowPlay(player);
    }

    @Override
    public void send(String data, String player, Boolean isplay) {
        send(Bukkit.getPlayer(player), data, isplay);
    }

    @Override
    public void send(String data, Boolean isplay) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!AllMusic.getConfig().getNoMusicPlayer().contains(player.getName())) {
                if (isplay && !isOK(player.getName(), false))
                    continue;
                send(player, data, isplay);
            }
        }
    }

    @Override
    public int getAllPlayer() {
        return Bukkit.getServer().getOnlinePlayers().size();
    }

    @Override
    public void sendHudLyric(String data) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if (!isOK(player.getName(), true))
                continue;
            SaveOBJ obj = HudSave.get(name);
            if (!obj.isEnableLyric())
                continue;
            send(player, "[Lyric]" + data, null);
        }
    }

    @Override
    public void bq(String data) {
        Bukkit.broadcastMessage(data);
    }

    @Override
    public void bqt(String data) {
        Bukkit.getScheduler().runTask(AllMusicBukkit.plugin, () -> Bukkit.broadcastMessage(data));
    }

    @Override
    public boolean NeedPlay() {
        int online = getAllPlayer();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (AllMusic.getConfig().getNoMusicPlayer().contains(player.getName())) {
                online--;
            }
        }
        return online > 0;
    }

    @Override
    public void sendHudInfo(String data) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String Name = player.getName();
            if (!isOK(player.getName(), true))
                continue;
            SaveOBJ obj = HudSave.get(Name);
            if (!obj.isEnableInfo())
                continue;
            send(player, "[Info]" + data, null);
        }
    }

    @Override
    public void sendHudList(String data) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String Name = player.getName();
            if (!isOK(player.getName(), true))
                continue;
            SaveOBJ obj = HudSave.get(Name);
            if (!obj.isEnableList())
                continue;
            send(player, "[List]" + data, null);
        }
    }

    @Override
    public void sendHudSaveAll() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String Name = player.getName();
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
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            send(player, "[clear]", null);
        }
    }

    @Override
    public void sendMessaget(Object obj, String Message) {
        Bukkit.getScheduler().runTask(AllMusicBukkit.plugin, () -> ((CommandSender) obj).sendMessage(Message));
    }

    @Override
    public void sendMessage(Object obj, String Message) {
        CommandSender sender = (CommandSender) obj;
        sender.sendMessage(Message);
    }

    @Override
    public void sendMessageRun(Object obj, String Message, String end, String command) {
        if (!Message.isEmpty())
            sendMessage(obj, Message);
    }

    @Override
    public void sendMessageSuggest(Object obj, String Message, String end, String command) {
        if (!Message.isEmpty())
            sendMessage(obj, Message);
    }

    @Override
    public void runTask(Runnable run) {
        Bukkit.getScheduler().runTask(AllMusicBukkit.plugin, run);
    }

    @Override
    public void reload() {
        new AllMusic().init(AllMusicBukkit.plugin.getDataFolder());
    }

    @Override
    public boolean checkPermission(String player, String permission) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 == null)
            return true;
        return !player1.hasPermission(permission);
    }

    @Override
    public void task(Runnable run, int delay) {
        Bukkit.getScheduler().runTaskLater(AllMusicBukkit.plugin, run, delay);
    }

    private void send(Player players, String data, Boolean isplay) {
        if (players == null)
            return;
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            Object buf = bufferM.invoke(null, bytes.length + 1);
            writeByteM.invoke(buf, 666);
            writeBytesM.invoke(buf, bytes);

//            ByteBuf buf = Unpooled.buffer(bytes.length + 1);
//
//            buf.writeByte(666);
//            buf.writeBytes(bytes);
            runTask(() ->
//                    players.sendPluginMessage(AllMusicBukkit.plugin, AllMusic.channel, buf.array()));
            {
                try {
                    players.sendPluginMessage(AllMusicBukkit.plugin, AllMusic.channel, (byte[]) arrayM.invoke(buf));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            if (isplay != null) {
                if (isplay) {
                    AllMusic.addNowPlayPlayer(players.getName());
                } else {
                    AllMusic.removeNowPlayPlayer(players.getName());
                }
            }
        } catch (Exception e) {
            AllMusic.log.warning("§c数据发送发生错误");
            e.printStackTrace();
        }
    }
}
