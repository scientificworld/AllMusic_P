package Color_yr.ALLMusic.Command;

import Color_yr.ALLMusic.ALLMusic;
import Color_yr.ALLMusic.MusicAPI.SongSearch.SearchOBJ;
import Color_yr.ALLMusic.MusicAPI.SongSearch.SearchPage;
import Color_yr.ALLMusic.MusicPlay.PlayMusic;
import Color_yr.ALLMusic.Utils.Function;
import net.md_5.bungee.api.chat.ClickEvent;

public class CommandEX {

    private static void AddMusic(Object sender, String Name, String[] args) {
        String MusicID;
        if (args[0].contains("id=") && !args[0].contains("/?userid")) {
            if (args[0].contains("&user"))
                MusicID = Function.getString(args[0], "id=", "&user");
            else
                MusicID = Function.getString(args[0], "id=", null);
        } else if (args[0].contains("song/")) {
            if (args[0].contains("/?userid"))
                MusicID = Function.getString(args[0], "song/", "/?userid");
            else
                MusicID = Function.getString(args[0], "song/", null);
        } else
            MusicID = args[0];
        if (Function.isInteger(MusicID)) {
            if (PlayMusic.getSize() >= ALLMusic.getConfig().getMaxList()) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getAddMusic().getListFull());
            } else if (ALLMusic.getConfig().getBanMusic().contains(MusicID)) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getAddMusic().getBanMusic());
            } else if (PlayMusic.isHave(MusicID)) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getAddMusic().getExistMusic());
            } else {
                ALLMusic.getConfig().RemoveNoMusicPlayer(Name);
                if (ALLMusic.Side.NeedPlay()) {
                    ALLMusic.Side.RunTask(() -> PlayMusic.addMusic(MusicID, Name, false));
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getAddMusic().getSuccess());
                } else
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getAddMusic().getNoPlayer());
            }
        } else
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getAddMusic().getNoID());
    }

    private static void ShowSearch(Object sender, SearchPage search) {
        int index = search.getIndex();
        SearchOBJ item;
        String info;
        ALLMusic.Side.SendMessage(sender, "");
        if (search.haveLastPage()) {
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getPage().getLast(),
                    ClickEvent.Action.RUN_COMMAND, "/music lastpage");
        }
        for (int a = 0; a < index; a++) {
            item = search.getRes(a + search.getPage() * 10);
            info = ALLMusic.getMessage().getPage().getChoice();
            info = info.replace("%index%", "" + (a + 1))
                    .replace("%MusicName%", item.getName())
                    .replace("%MusicAuthor%", item.getAuthor())
                    .replace("%MusicAl%", item.getAl());
            ALLMusic.Side.SendMessage(sender, info,
                    ClickEvent.Action.RUN_COMMAND, "/music select " + (a + 1));
        }
        if (search.haveNextPage()) {
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getPage().getNext(),
                    ClickEvent.Action.RUN_COMMAND, "/music nextpage");
        }
        ALLMusic.Side.SendMessage(sender, "");
    }

    public static void EX(Object sender, String Name, String[] args) {
        if (args.length == 0) {
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getCommand().getError());
        } else if (args[0].equalsIgnoreCase("help")) {
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2帮助手册");
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music [音乐ID] 来点歌§e[§n点我§r§e]",
                    ClickEvent.Action.SUGGEST_COMMAND, "/music ");
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music stop 停止播放歌曲§e[§n点我§r§e]",
                    ClickEvent.Action.RUN_COMMAND, "/music stop");
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music list 查看歌曲队列§e[§n点我§r§e]",
                    ClickEvent.Action.RUN_COMMAND, "/music list");
            ALLMusic.Side.SendMessage(sender, "§d[ALLmusic]§2使用/music vote 投票切歌§e[§n点我§r§e]",
                    ClickEvent.Action.RUN_COMMAND, "/music vote");
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music nomusic 不再参与点歌§e[§n点我§r§e]",
                    ClickEvent.Action.RUN_COMMAND, "/music nomusic");
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music search [歌名] 搜索歌曲§e[§n点我§r§e]",
                    ClickEvent.Action.SUGGEST_COMMAND, "/music search ");
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music select [序列] 选择歌曲§e[§n点我§r§e]",
                    ClickEvent.Action.SUGGEST_COMMAND, "/music select ");
            if (ALLMusic.VVEnable) {
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music vv enable 启用关闭VV§e[§n点我§r§e]",
                        ClickEvent.Action.RUN_COMMAND, "/music vv enable");
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2使用/music vv [位置] [坐标] [数值] 设置VV位置",
                        ClickEvent.Action.SUGGEST_COMMAND, "/music vv ");
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            ALLMusic.Side.Send("[Stop]", Name, false);
            if (ALLMusic.VVEnable) {
                ALLMusic.VV.clear(Name);
            }
            ALLMusic.removeNowPlayPlayer(Name);
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getMusicPlay().getStopPlay());
        } else if (args[0].equalsIgnoreCase("list")) {
            if (PlayMusic.NowPlayMusic == null) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getMusicPlay().getNoMusic());
            } else {
                String info = ALLMusic.getMessage().getMusicPlay().getPlay();
                info = info.replace("%MusicName%", PlayMusic.NowPlayMusic.getName())
                        .replace("%MusicAuthor%", PlayMusic.NowPlayMusic.getAuthor())
                        .replace("%MusicAl%", PlayMusic.NowPlayMusic.getAl())
                        .replace("%MusicAlia%", PlayMusic.NowPlayMusic.getAlia())
                        .replace("%PlayerName%", PlayMusic.NowPlayMusic.getCall());
                ALLMusic.Side.SendMessage(sender, info);
            }
            if (PlayMusic.getSize() == 0) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getMusicPlay().getNoPlay());
            } else {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getMusicPlay().getListMusic().getHead()
                        .replace("&Count&", "" + PlayMusic.getSize()));
                ALLMusic.Side.SendMessage(sender, PlayMusic.getAllList());
            }
        } else if (args[0].equalsIgnoreCase("vote")) {
            if (ALLMusic.getConfig().isNeedPermission() && ALLMusic.Side.checkPermission(Name, "ALLMusic.vote")) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getVote().getNoPermission());
                return;
            }
            if (PlayMusic.getSize() == 0 && ALLMusic.getConfig().getPlayList().size() == 0) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getMusicPlay().getNoPlay());
            } else if (PlayMusic.VoteTime == 0) {
                PlayMusic.VoteTime = 30;
                ALLMusic.addVote(Name);
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getVote().getDoVote());
                String data = ALLMusic.getMessage().getVote().getBQ();
                ALLMusic.Side.bq(data.replace("%PlayerName%", Name));
            } else if (PlayMusic.VoteTime > 0) {
                if (!ALLMusic.containVote(Name)) {
                    ALLMusic.addVote(Name);
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getVote().getAgree());
                    String data = ALLMusic.getMessage().getVote().getBQAgree();
                    data = data.replace("%PlayerName%", Name)
                            .replace("%Count%", "" + ALLMusic.getVoteCount());
                    ALLMusic.Side.bq(data);
                } else {
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getVote().getARAgree());
                }
            }
            ALLMusic.getConfig().RemoveNoMusicPlayer(Name);
        } else if (args[0].equalsIgnoreCase("reload")) {
            ALLMusic.Side.reload();
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2已重读配置文件");
        } else if (args[0].equalsIgnoreCase("next") && ALLMusic.getConfig().getAdmin().contains(Name)) {
            PlayMusic.MusicAllTime = 1;
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2已强制切歌");
            ALLMusic.getConfig().RemoveNoMusicPlayer(Name);
        } else if (args[0].equalsIgnoreCase("nomusic")) {
            ALLMusic.Side.Send("[Stop]", Name, false);
            ALLMusic.getConfig().AddNoMusicPlayer(Name);
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getMusicPlay().getNoPlayMusic());
        } else if (args[0].equalsIgnoreCase("ban") && args.length == 2
                && ALLMusic.getConfig().getAdmin().contains(Name)) {
            if (Function.isInteger(args[1])) {
                ALLMusic.getConfig().addBanID(args[1]);
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2已禁止" + args[1]);
            } else {
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2请输入有效的ID");
            }
        } else if (args[0].equalsIgnoreCase("delete") && args.length == 2
                && ALLMusic.getConfig().getAdmin().contains(Name)) {
            if (!args[1].isEmpty() && Function.isInteger(args[1])) {
                int music = Integer.parseInt(args[1]);
                if (music == 0) {
                    ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2请输入有效的序列ID");
                    return;
                }
                if (music > PlayMusic.getSize()) {
                    ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2序列号过大");
                    return;
                }
                PlayMusic.remove(music - 1);
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2已删除序列" + music);
            } else {
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2请输入有效的序列ID");
            }
        } else if (args[0].equalsIgnoreCase("addlist") && args.length == 2
                && ALLMusic.getConfig().getAdmin().contains(Name)) {
            if (Function.isInteger(args[1])) {
                ALLMusic.Music.SetList(args[1], sender);
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2添加空闲音乐列表" + args[1]);
            } else {
                ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2请输入有效的音乐列表ID");
            }
        } else if (args[0].equalsIgnoreCase("clearlist")
                && ALLMusic.getConfig().getAdmin().contains(Name)) {
            ALLMusic.getConfig().getPlayList().clear();
            ALLMusic.save();
            ALLMusic.Side.SendMessage(sender, "§d[ALLMusic]§2添加空闲音乐列表已清空");
        } else if (args[0].equalsIgnoreCase("search") && args.length >= 2) {
            if (ALLMusic.getConfig().isNeedPermission() && ALLMusic.Side.checkPermission(Name, "ALLMusic.search")) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoPer());
                return;
            }
            ALLMusic.Side.RunTask(() -> {
                SearchPage search = ALLMusic.Music.Search(args);
                if (search == null)
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getCantSearch().replace("%Music%", args[1]));
                else {
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getRes());
                    ShowSearch(sender, search);
                    ALLMusic.addSearch(Name, search);
                }
            });
        } else if (args[0].equalsIgnoreCase("select") && args.length == 2) {
            if (ALLMusic.getConfig().isNeedPermission() && ALLMusic.Side.checkPermission(Name, "ALLMusic.search")) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoPer());
                return;
            }
            SearchPage obj = ALLMusic.getSearch(Name);
            if (obj == null) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoSearch());
            } else if (!args[1].isEmpty() && Function.isInteger(args[1])) {
                int a = Integer.parseInt(args[1]);
                if (a == 0) {
                    ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getErrorNum());
                    return;
                }
                String[] ID = new String[1];
                ID[0] = obj.GetSong((obj.getPage() * 10) + (a - 1));
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getChose().replace("%Num%", "" + a));
                AddMusic(sender, Name, ID);
                ALLMusic.removeSearch(Name);
            } else {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getErrorNum());
            }
        } else if (args[0].equalsIgnoreCase("nextpage")) {
            if (ALLMusic.getConfig().isNeedPermission() && ALLMusic.Side.checkPermission(Name, "ALLMusic.search")) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoPer());
                return;
            }
            SearchPage obj = ALLMusic.getSearch(Name);
            if (obj == null) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoSearch());
            } else if (obj.nextPage()) {
                ShowSearch(sender, obj);
            } else {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getCantNext());
            }
        } else if (args[0].equalsIgnoreCase("lastpage")) {
            if (ALLMusic.getConfig().isNeedPermission() && ALLMusic.Side.checkPermission(Name, "ALLMusic.search")) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoPer());
                return;
            }
            SearchPage obj = ALLMusic.getSearch(Name);
            if (obj == null) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getNoSearch());
            } else if (obj.lastPage()) {
                ShowSearch(sender, obj);
            } else {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getSearch().getCantLast());
            }
        } else if (ALLMusic.VVEnable && args[0].equalsIgnoreCase("vv")) {
            if (args.length == 1) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getCommand().getError());
            } else if (args[1].equalsIgnoreCase("enable")) {
                boolean temp = ALLMusic.VV.SetEnable(Name);
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getVV().getState().replace("%State%", temp ? "启用" : "关闭"));
            } else if (args.length != 4) {
                ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getCommand().getError());
            } else {
                ALLMusic.Side.RunTask(() -> {
                    try {
                        if (!ALLMusic.VV.SetPot(Name, args[1], args[2], args[3]))
                            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getCommand().getError());
                        else
                            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getVV().getSet());
                    } catch (Exception e) {
                        ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getCommand().getError());
                    }
                });
            }
        } else if (ALLMusic.getConfig().isNeedPermission() && ALLMusic.Side.checkPermission(Name, "ALLMusic.addmusic"))
            ALLMusic.Side.SendMessage(sender, ALLMusic.getMessage().getCommand().getNoPer());
        else
            AddMusic(sender, Name, args);

    }
}
