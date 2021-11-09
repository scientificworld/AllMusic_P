package Color_yr.AllMusic.http;

import Color_yr.AllMusic.AllMusic;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    private static OkHttpClient client;
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 7;

    static {
        try {
            synchronized (OkHttpClient.class) {
                client = new OkHttpClient.Builder().cookieJar(new CookieJar() {
                            @Override
                            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                                if (AllMusic.Cookie.cookieStore.containsKey(httpUrl.host())) {
                                    List<Cookie> cookies = AllMusic.Cookie.cookieStore.get(httpUrl.host());
                                    for (Cookie item : list) {
                                        for (Cookie item1 : cookies) {
                                            if (item.name().equalsIgnoreCase(item1.name())) {
                                                cookies.remove(item1);
                                                break;
                                            }
                                        }
                                    }
                                    List<Cookie> cookies1 = new CopyOnWriteArrayList<>();
                                    cookies1.addAll(cookies);
                                    cookies1.addAll(list);
                                    AllMusic.Cookie.cookieStore.put(httpUrl.host(), cookies1);
                                } else {
                                    AllMusic.Cookie.cookieStore.put(httpUrl.host(), list);
                                }
                                AllMusic.saveCookie();
                            }

                            @NotNull
                            @Override
                            public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                                List<Cookie> cookies = AllMusic.Cookie.cookieStore.get(httpUrl.host());
                                return cookies != null ? cookies : new CopyOnWriteArrayList<>();
                            }
                        })
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Res get(String path, String data) {
        try {
            data = URLEncoder.encode(data, "UTF-8");
            Request request = new Request.Builder().url(path + data)
                    .addHeader("referer", "https://music.163.com")
                    .addHeader("content-type", "application/json;charset=UTF-8")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.864.41")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            ResponseBody body = response.body();
            if (body == null) {
                AllMusic.log.warning("§d[AllMusic]§c获取网页错误");
                return null;
            }
            InputStream inputStream = body.byteStream();
            boolean ok = httpCode == 200;
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String data1 = result.toString(StandardCharsets.UTF_8.name());
            if (!ok) {
                AllMusic.log.warning("§d[AllMusic]§c服务器返回错误：" + data1);
            }
            return new Res(data1, ok);
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c获取网页错误");
            e.printStackTrace();
        }
        return null;
    }

    public static Res post(String url, JsonObject data, EncryptType type, String ourl) {
        try {
            RequestBody formBody;
            Request.Builder request = new Request.Builder();
            request = request.addHeader("Content-Type", "application/x-www-form-urlencoded");
            request = request.addHeader("Referer", "https://music.163.com");
            encRes res;
            List<Cookie> cookies = new ArrayList<>();
            if (AllMusic.Cookie.cookieStore.containsKey("music.163.com")) {
                cookies = AllMusic.Cookie.cookieStore.get("music.163.com");
            }
            if (type == EncryptType.weapi) {
                request = request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/13.10586");
                String csrfToken = "";
                for (Cookie item : cookies) {
                    if (item.name().equalsIgnoreCase("__csrf")) {
                        csrfToken = item.value();
                    }
                }
                data.addProperty("csrf_token", csrfToken);
                res = CryptoUtil.weapiEncrypt(AllMusic.gson.toJson(data));
                url = url.replaceFirst("\\w*api", "weapi");
                request = request.url(url);
                formBody = new FormBody.Builder()
                        .add("params", res.params)
                        .add("encSecKey", res.encSecKey)
                        .build();
            } else if (type == EncryptType.eapi) {
                request = request.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 9; PCT-AL10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.64 HuaweiBrowser/10.0.3.311 Mobile Safari/537.36");
                JsonObject header = new JsonObject();
                header.addProperty("appver", "8.0.0");
                header.addProperty("versioncode", "140");
                header.addProperty("buildver", new Date().toString().substring(0, 10));
                header.addProperty("resolution", "1920x1080");
                header.addProperty("os", "android");
                String requestId = "0000" + (new Date() + "_" + Math.floor(Math.random() * 1000));
                header.addProperty("requestId", requestId);
                for (Cookie item : cookies) {
                    if (item.name().equalsIgnoreCase("MUSIC_U")) {
                        header.addProperty("MUSIC_U", item.value());
                    } else if (item.name().equalsIgnoreCase("MUSIC_A")) {
                        header.addProperty("MUSIC_A", item.value());
                    } else if (item.name().equalsIgnoreCase("channel")) {
                        header.addProperty("channel", item.value());
                    } else if (item.name().equalsIgnoreCase("mobilename")) {
                        header.addProperty("mobilename", item.value());
                    } else if (item.name().equalsIgnoreCase("osver")) {
                        header.addProperty("osver", item.value());
                    } else if (item.name().equalsIgnoreCase("__csrf")) {
                        header.addProperty("__csrf", item.value());
                    }
                }

                data.add("header", header);
                res = CryptoUtil.eapi(ourl, data);
                url = url.replaceFirst("\\w*api", "eapi");
                request = request.url(url);
                formBody = new FormBody.Builder()
                        .add("params", res.params)
                        .build();
            } else {
                request = request.url(url);
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, JsonElement> item : data.entrySet()) {
                    builder = builder.add(item.getKey(), item.getValue().getAsString());
                }
                formBody = builder.build();
            }
            request = request.post(formBody);
            Response response = client.newCall(request.build()).execute();
            int httpCode = response.code();
            ResponseBody body = response.body();
            if (body == null) {
                AllMusic.log.warning("§d[AllMusic]§c获取网页错误");
                return null;
            }
            InputStream inputStream = body.byteStream();
            boolean ok = httpCode == 200;
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String data1 = result.toString(StandardCharsets.UTF_8.name());
            if (!ok) {
                AllMusic.log.warning("§d[AllMusic]§c服务器返回错误：" + data1);
            }
            return new Res(data1, ok);
        } catch (Exception e) {
            AllMusic.log.warning("§d[AllMusic]§c获取网页错误");
            e.printStackTrace();
        }
        return null;
    }
}
