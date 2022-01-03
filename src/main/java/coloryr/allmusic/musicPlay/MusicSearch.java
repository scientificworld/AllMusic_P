package coloryr.allmusic.musicPlay;

import coloryr.allmusic.AllMusic;
import coloryr.allmusic.command.CommandEX;
import coloryr.allmusic.musicAPI.songSearch.SearchPage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MusicSearch {
    private static boolean isRun;
    private static final Queue<MusicObj> tasks = new ConcurrentLinkedQueue<>();

    private static void task() {
        while (isRun) {
            try {
                MusicObj obj = tasks.poll();
                if (obj != null) {
                    SearchPage search = AllMusic.getMusicApi().search(obj.args, obj.isDefault);
                    if (search == null)
                        AllMusic.Side.sendMessaget(obj.sender, AllMusic.getMessage().getSearch()
                                .getCantSearch().replace("%Music%", obj.isDefault ? obj.args[0] : obj.args[1]));
                    else {
                        AllMusic.Side.sendMessaget(obj.sender, AllMusic.getMessage().getSearch().getRes());
                        AllMusic.addSearch(obj.Name, search);
                        AllMusic.Side.runTask(() -> CommandEX.showSearch(obj.sender, search));
                    }
                }
                Thread.sleep(100);
            } catch (Exception e) {
                AllMusic.log.warning("搜歌出现问题");
                e.printStackTrace();
            }
        }
    }

    ;

    public static void start() {
        Thread taskT = new Thread(MusicSearch::task, "AllMusic_search");
        isRun = true;
        taskT.start();
    }

    public static void stop() {
        isRun = false;
    }

    public static void addSearch(MusicObj obj) {
        tasks.add(obj);
    }
}
