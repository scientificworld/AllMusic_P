package coloryr.allmusic.http;

public class encRes {
    public String params;
    public String encSecKey;

    public encRes(String params, String encSecKey) {
        this.encSecKey = encSecKey;
        this.params = params;
    }
}
