package Color_yr.AllMusic.command;

import Color_yr.AllMusic.AllMusic;
import Color_yr.AllMusic.Utils.Function;
import Color_yr.AllMusic.musicAPI.songSearch.SearchOBJ;
import Color_yr.AllMusic.musicAPI.songSearch.SearchPage;
import Color_yr.AllMusic.musicPlay.MusicObj;
import Color_yr.AllMusic.musicPlay.MusicSearch;
import Color_yr.AllMusic.musicPlay.PlayMusic;
import Color_yr.AllMusic.musicPlay.sendHud.HudUtils;
import Color_yr.AllMusic.musicPlay.sendHud.PosOBJ;

import java.util.Locale;

public class CommandEX {
    private static void searchMusic(Object sender, String name, String[] args, boolean isDefault) {
        MusicObj obj = new MusicObj();
        obj.sender = sender;
        obj.Name = name;
        obj.args = args;
        obj.isDefault = isDefault;
        MusicSearch.addSearch(obj);
    }

    private static void addMusic(Object sender, String name, String[] args) {
        String musicID;
        if (args[0].contains("id=") && !args[0].contains("/?userid")) {
            if (args[0].contains("&user"))
                musicID = Function.getString(args[0], "id=", "&user");
            else
                musicID = Function.getString(args[0], "id=", null);
        } else if (args[0].contains("song/")) {
            if (args[0].contains("/?userid"))
                musicID = Function.getString(args[0], "song/", "/?userid");
            else
                musicID = Function.getString(args[0], "song/", null);
        } else
            musicID = args[0];
        if (Function.isInteger(musicID)) {
            if (PlayMusic.getSize() >= AllMusic.getConfig().getMaxList()) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getListFull());
            } else if (AllMusic.getConfig().getBanMusic().contains(musicID)) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getBanMusic());
            } else if (PlayMusic.isHave(musicID)) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getExistMusic());
            } else {
                if (AllMusic.getConfig().isUseCost() && AllMusic.Vault != null) {
                    if (!AllMusic.Vault.check(name, AllMusic.getConfig().getAddMusicCost())) {
                        AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCost().getNoMoney());
                        return;
                    }
                }
                AllMusic.getConfig().RemoveNoMusicPlayer(name);
                if (AllMusic.Side.NeedPlay()) {
                    MusicObj obj = new MusicObj();
                    obj.sender = musicID;
                    obj.Name = name;
                    obj.isDefault = false;
                    PlayMusic.addTask(obj);
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getSuccess());
                    if (AllMusic.getConfig().isUseCost() && AllMusic.Vault != null) {
                        AllMusic.Vault.cost(name, AllMusic.getConfig().getAddMusicCost(),
                                AllMusic.getMessage().getCost().getAddMusic()
                                        .replace("%Cost%", "" + AllMusic.getConfig().getAddMusicCost()));
                    }
                } else
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getNoPlayer());
            }
        } else
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getNoID());
    }

    public static void showSearch(Object sender, SearchPage search) {
        int index = search.getIndex();
        SearchOBJ item;
        String info;
        AllMusic.Side.sendMessage(sender, "");
        if (search.haveLastPage()) {
            AllMusic.Side.sendMessageRun(sender, "§d[AllMusic]§2输入/music lastpage上一页",
                    AllMusic.getMessage().getPage().getLast(), "/music lastpage");
        }
        for (int a = 0; a < index; a++) {
            item = search.getRes(a + search.getPage() * 10);
            info = AllMusic.getMessage().getPage().getChoice();
            info = info.replace("%index%", "" + (a + 1))
                    .replace("%MusicName%", item.getName())
                    .replace("%MusicAuthor%", item.getAuthor())
                    .replace("%MusicAl%", item.getAl());
            AllMusic.Side.sendMessageRun(sender, info,
                    AllMusic.getMessage().getClick().This, "/music select " + (a + 1));
        }
        if (search.haveNextPage()) {
            AllMusic.Side.sendMessageRun(sender, "§d[AllMusic]§2输入/music nextpage下一页",
                    AllMusic.getMessage().getPage().getNext(), "/music nextpage");
        }
        AllMusic.Side.sendMessage(sender, "");
    }

    public static void ex(Object sender, String name, String[] args) {
        if (args.length == 0) {
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getError());
            return;
        } else if (args[0].equalsIgnoreCase("help")) {
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getHelp().getNormal().getHead());
            AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getNormal().getBase(),
                    AllMusic.getMessage().getClick().Check, "/music ");
            AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getNormal().getStop(),
                    AllMusic.getMessage().getClick().This, "/music stop");
            AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getNormal().getList(),
                    AllMusic.getMessage().getClick().This, "/music list");
            AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getNormal().getVote(),
                    AllMusic.getMessage().getClick().This, "/music vote");
            AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getNormal().getNoMusic(),
                    AllMusic.getMessage().getClick().This, "/music nomusic");
            AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getNormal().getSearch(),
                    AllMusic.getMessage().getClick().Check, "/music search ");
            AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getNormal().getSelect(),
                    AllMusic.getMessage().getClick().Check, "/music select ");
            AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getNormal().getHud1(),
                    AllMusic.getMessage().getClick().Check, "/music hud enable ");
            AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getNormal().getHud2(),
                    AllMusic.getMessage().getClick().Check, "/music hud ");
            if (AllMusic.getConfig().getAdmin().contains(name)) {
                AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getAdmin().getReload(),
                        AllMusic.getMessage().getClick().This, "/music reload");
                AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getAdmin().getNext(),
                        AllMusic.getMessage().getClick().This, "/music next");
                AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getAdmin().getBan(),
                        AllMusic.getMessage().getClick().Check, "/music ban ");
                AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getAdmin().getUrl(),
                        AllMusic.getMessage().getClick().Check, "/music url ");
                AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getAdmin().getDelete(),
                        AllMusic.getMessage().getClick().Check, "/music delete ");
                AllMusic.Side.sendMessageSuggest(sender, AllMusic.getMessage().getHelp().getAdmin().getAddList(),
                        AllMusic.getMessage().getClick().Check, "/music addlist ");
                AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getAdmin().getClearList(),
                        AllMusic.getMessage().getClick().This, "/music clearlist");
                AllMusic.Side.sendMessageRun(sender, AllMusic.getMessage().getHelp().getAdmin().getLogin(),
                        AllMusic.getMessage().getClick().This, "/music login");
            }
            return;
        } else if (args[0].equalsIgnoreCase("stop")) {
            AllMusic.Side.clearHud(name);
            AllMusic.Side.send("[Stop]", name, false);
            HudUtils.clearHud(name);
            AllMusic.removeNowPlayPlayer(name);
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getMusicPlay().getStopPlay());
            return;
        } else if (args[0].equalsIgnoreCase("list")) {
            if (PlayMusic.NowPlayMusic == null || PlayMusic.NowPlayMusic.isNull()) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getMusicPlay().getNoMusic());
            } else {
                String info = AllMusic.getMessage().getMusicPlay().getPlay();
                info = info.replace("%MusicName%", PlayMusic.NowPlayMusic.getName())
                        .replace("%MusicAuthor%", PlayMusic.NowPlayMusic.getAuthor())
                        .replace("%MusicAl%", PlayMusic.NowPlayMusic.getAl())
                        .replace("%MusicAlia%", PlayMusic.NowPlayMusic.getAlia())
                        .replace("%PlayerName%", PlayMusic.NowPlayMusic.getCall());
                AllMusic.Side.sendMessage(sender, info);
            }
            if (PlayMusic.getSize() == 0) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getMusicPlay().getNoPlay());
            } else {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getMusicPlay().getListMusic().getHead()
                        .replace("&Count&", "" + PlayMusic.getSize()));
                AllMusic.Side.sendMessage(sender, PlayMusic.getAllList());
            }
            return;
        } else if (args[0].equalsIgnoreCase("vote")) {
            if (AllMusic.getConfig().isNeedPermission() && AllMusic.Side.checkPermission(name, "AllMusic.vote")) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getVote().getNoPermission());
                return;
            }
            if (PlayMusic.getSize() == 0 && AllMusic.getConfig().getPlayList().size() == 0) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getMusicPlay().getNoPlay());
            } else if (PlayMusic.VoteTime == 0) {
                PlayMusic.VoteTime = 30;
                AllMusic.addVote(name);
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getVote().getDoVote());
                String data = AllMusic.getMessage().getVote().getBQ();
                AllMusic.Side.bq(data.replace("%PlayerName%", name));
            } else if (PlayMusic.VoteTime > 0) {
                if (!AllMusic.containVote(name)) {
                    AllMusic.addVote(name);
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getVote().getAgree());
                    String data = AllMusic.getMessage().getVote().getBQAgree();
                    data = data.replace("%PlayerName%", name)
                            .replace("%Count%", "" + AllMusic.getVoteCount());
                    AllMusic.Side.bq(data);
                } else {
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getVote().getARAgree());
                }
            }
            AllMusic.getConfig().RemoveNoMusicPlayer(name);
            return;
        } else if (args[0].equalsIgnoreCase("nomusic")) {
            AllMusic.Side.send("[Stop]", name, false);
            AllMusic.Side.clearHud(name);
            AllMusic.getConfig().AddNoMusicPlayer(name);
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getMusicPlay().getNoPlayMusic());
            return;
        } else if (args[0].equalsIgnoreCase("search") && args.length >= 2) {
            if (AllMusic.getConfig().isNeedPermission() && AllMusic.Side.checkPermission(name, "AllMusic.search")) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoPer());
                return;
            }
            if (AllMusic.getConfig().isUseCost() && AllMusic.Vault != null) {
                if (!AllMusic.Vault.check(name, AllMusic.getConfig().getSearchCost())) {
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCost().getNoMoney());
                    return;
                }
                AllMusic.Vault.cost(name, AllMusic.getConfig().getAddMusicCost(),
                        AllMusic.getMessage().getCost().getAddMusic()
                                .replace("%Cost%", "" + AllMusic.getConfig().getAddMusicCost()));
            }
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getStartSearch());
            searchMusic(sender, name, args, false);
            return;
        } else if (args[0].equalsIgnoreCase("select") && args.length == 2) {
            if (AllMusic.getConfig().isNeedPermission() && AllMusic.Side.checkPermission(name, "AllMusic.search")) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoPer());
                return;
            }
            SearchPage obj = AllMusic.getSearch(name);
            if (obj == null) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoSearch());
            } else if (!args[1].isEmpty() && Function.isInteger(args[1])) {
                int a = Integer.parseInt(args[1]);
                if (a == 0) {
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getErrorNum());
                    return;
                }
                String[] ID = new String[1];
                ID[0] = obj.getSong((obj.getPage() * 10) + (a - 1));
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getChose().replace("%Num%", "" + a));
                addMusic(sender, name, ID);
                AllMusic.removeSearch(name);
            } else {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getErrorNum());
            }
            return;
        } else if (args[0].equalsIgnoreCase("nextpage")) {
            if (AllMusic.getConfig().isNeedPermission() && AllMusic.Side.checkPermission(name, "AllMusic.search")) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoPer());
                return;
            }
            SearchPage obj = AllMusic.getSearch(name);
            if (obj == null) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoSearch());
            } else if (obj.nextPage()) {
                showSearch(sender, obj);
            } else {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getCantNext());
            }
            return;
        } else if (args[0].equalsIgnoreCase("lastpage")) {
            if (AllMusic.getConfig().isNeedPermission() && AllMusic.Side.checkPermission(name, "AllMusic.search")) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoPer());
                return;
            }
            SearchPage obj = AllMusic.getSearch(name);
            if (obj == null) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getNoSearch());
            } else if (obj.lastPage()) {
                showSearch(sender, obj);
            } else {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getSearch().getCantLast());
            }
            return;
        } else if (args[0].equalsIgnoreCase("hud")) {
            if (args.length == 1) {
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getError());
            } else {
                if (args[1].equalsIgnoreCase("enable")) {
                    if (args.length == 3) {
                        try {
                            String pos = args[2].toLowerCase(Locale.ROOT);
                            boolean temp = HudUtils.setHudEnable(name, pos);
                            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getHud().getState()
                                    .replace("%State%", temp ? "启用" : "关闭")
                                    .replace("%Hud%", AllMusic.getMessage().getHudList().Get(pos)));
                        } catch (Exception e) {
                            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getError());
                        }
                    } else {
                        boolean temp = HudUtils.setHudEnable(name, null);
                        AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getHud().getState()
                                .replace("%State%", temp ? "启用" : "关闭")
                                .replace("%Hud%", AllMusic.getMessage().getHudList().getAll()));
                    }
                } else if (args[1].equalsIgnoreCase("reset")) {
                    HudUtils.reset(name);
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getHud().getReset());
                } else if (args.length != 4) {
                    AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getError());
                } else {
                    try {
                        PosOBJ obj = HudUtils.setHudPos(name, args[1], args[2], args[3]);
                        if (obj == null) {
                            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getError());
                        } else {
                            String temp = AllMusic.getMessage().getHud().getSet()
                                    .replace("%Hud%", args[1])
                                    .replace("%x%", args[2])
                                    .replace("%y%", args[3]);
                            AllMusic.Side.sendMessage(sender, temp);
                        }
                    } catch (Exception e) {
                        AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getError());
                    }
                }
            }
            return;
        } else if (args[0].equalsIgnoreCase("reload")) {
            AllMusic.Side.reload();
            AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2已重读配置文件");
            return;
        } else if (AllMusic.getConfig().getAdmin().contains(name)) {
            if (args[0].equalsIgnoreCase("next")) {
                PlayMusic.MusicLessTime = 1;
                AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2已强制切歌");
                AllMusic.getConfig().RemoveNoMusicPlayer(name);
                return;
            } else if (args[0].equalsIgnoreCase("ban") && args.length == 2) {
                if (Function.isInteger(args[1])) {
                    AllMusic.getConfig().addBanID(args[1]);
                    AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2已禁止" + args[1]);
                } else {
                    AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2请输入有效的ID");
                }
                return;
            } else if (args[0].equalsIgnoreCase("url") && args.length == 2) {
                MusicObj obj = new MusicObj();
                obj.isUrl = true;
                obj.url = args[1];
                PlayMusic.addTask(obj);
                AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getAddMusic().getSuccess());
                return;
            } else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
                if (!args[1].isEmpty() && Function.isInteger(args[1])) {
                    int music = Integer.parseInt(args[1]);
                    if (music == 0) {
                        AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2请输入有效的序列ID");
                        return;
                    }
                    if (music > PlayMusic.getSize()) {
                        AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2序列号过大");
                        return;
                    }
                    PlayMusic.remove(music - 1);
                    AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2已删除序列" + music);
                } else {
                    AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2请输入有效的序列ID");
                }
                return;
            } else if (args[0].equalsIgnoreCase("addlist") && args.length == 2) {
                if (Function.isInteger(args[1])) {
                    AllMusic.getMusicApi().setList(args[1], sender);
                    AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2添加空闲音乐列表" + args[1]);
                } else {
                    AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2请输入有效的音乐列表ID");
                }
                return;
            } else if (args[0].equalsIgnoreCase("clearlist")) {
                AllMusic.getConfig().getPlayList().clear();
                AllMusic.save();
                AllMusic.Side.sendMessage(sender, "§d[AllMusic]§2添加空闲音乐列表已清空");
                return;
            } else if (args[0].equalsIgnoreCase("login")) {
                AllMusic.Side.sendMessage(sender, "§d[AllMusic]§d重新登录网易云账户");
                AllMusic.getMusicApi().login(sender);
                return;
            }
        }
        if (AllMusic.getConfig().isNeedPermission() && AllMusic.Side.checkPermission(name, "AllMusic.addmusic"))
            AllMusic.Side.sendMessage(sender, AllMusic.getMessage().getCommand().getNoPer());
        else {
            switch (AllMusic.getConfig().getDefaultAddMusic()) {
                case 1:
                    searchMusic(sender, name, args, true);
                    break;
                case 0:
                default:
                    addMusic(sender, name, args);
            }
        }
    }
}
