package coloryr.allmusic.musicAPI.web.getMusicList;

import java.util.ArrayList;
import java.util.List;

public class DataOBJ {
    private playlist playlist;

    public List<String> getPlaylist() {
        List<String> list = new ArrayList<>();
        for (track item : playlist.getTracks()) {
            list.add(item.getId());
        }
        return list;
    }

    public String getName() {
        return playlist.getName();
    }
}

class track {
    private int id;

    public String getId() {
        return String.valueOf(id);
    }
}

class playlist {
    private List<track> trackIds;
    private String name;

    public List<track> getTracks() {
        return trackIds;
    }

    public String getName() {
        return name;
    }
}