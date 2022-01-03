package coloryr.allmusic.musicAPI.web.getMusicLyric;

import coloryr.allmusic.api.ILyric;

public class LyricOBJ implements ILyric {
    private lrc lrc;
    private tlyric tlyric;
    private boolean nolyric;
    private boolean uncollected;

    public boolean isok() {
        return lrc != null || isNone();
    }

    public String getLyric() {
        return lrc.getLyric();
    }

    public String getTlyric() {
        return tlyric.getLyric();
    }

    public boolean isNone() {
        return nolyric || uncollected;
    }
}

class lrc {
    private String lyric;

    public String getLyric() {
        return lyric == null ? "" : lyric;
    }
}

class tlyric {
    private String lyric;

    public String getLyric() {
        return lyric;
    }
}