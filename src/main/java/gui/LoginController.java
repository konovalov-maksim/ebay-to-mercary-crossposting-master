package gui;

import core.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import okhttp3.Cookie;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URL;
import java.util.*;

public class LoginController implements Initializable {

    private Settings settings;
    private Logger logger;

    private WebViewCookieStore store = new WebViewCookieStore();

    @FXML
    private WebView mainWv;
    private WebEngine webEngine;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CookieManager cookieManager = new CookieManager(store, CookiePolicy.ACCEPT_ALL);
        CookieManager.setDefault(cookieManager);

//        Locale.setDefault(new Locale("en", "US"));
//        System.setProperty("https.proxyHost", "192.41.19.53");
//        System.setProperty("https.proxyPort", "3128");
        webEngine = mainWv.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
        webEngine.documentProperty().addListener((ov, oldDoc, doc) -> {
            if (doc == null) return;
            String documentUri = doc.getDocumentURI();
            if (documentUri.equals("https://www.mercari.com/mypage/")) {
                log("Successfully logged in");
                saveLoginCookies(store.get("www.mercari.com"));
                ((Stage) mainWv.getScene().getWindow()).close();
            }
        });
    }

    private void saveLoginCookies(List<HttpCookie> httpCookies) {
        try {
            List<Cookie> cookies = new ArrayList<>();
            for (HttpCookie httpCookie : httpCookies) {
                System.out.println(new Date(httpCookie.getMaxAge()));

                Cookie.Builder builder = new Cookie.Builder()
                        .domain(httpCookie.getDomain().replaceAll("^\\.", ""))
                        .name(httpCookie.getName())
                        .value(httpCookie.getValue());
                if (httpCookie.getPath() != null)
                    builder.path(httpCookie.getPath());
                if (httpCookie.getSecure())
                    builder.secure();
                if (httpCookie.isHttpOnly())
                    builder.httpOnly();
                cookies.add(builder.build());
            }
            DataManager.getInstance().saveCookies(cookies);
        } catch (Exception e) {
            e.printStackTrace();
            log("Failed to save credentials");
        }
    }

    public void loadLoginPage() {
        webEngine.load("https://www.mercari.com/login/");
    }


    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
