package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class MercariLoginController implements Initializable {

    @FXML
    private WebView mainWv;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.setProperty("https.proxyHost", "192.41.71.199");
        System.setProperty("https.proxyPort", "3128");
        WebEngine webEngine = mainWv.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");

        URI uri = URI.create("https://www.mercari.com");
        Map<String, List<String>> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", getTestCookies());
        try {
            java.net.CookieHandler.getDefault().put(uri, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        webEngine.load("https://www.mercari.com/login/");
//        webEngine.load("https://whatismyipaddress.com/");

    }

    private List<String> getTestCookies() {
        List<String> cookies = new ArrayList<>();
        cookies.add("_mwus.sig=FrmgBD_wsb7B3kH2OgAK1vTXgt0\n");
        cookies.add("_MWUS=8j2v86h8j790dmsidi6j8k9q7d\n");
        cookies.add("G_ENABLED_IDPS=google");
        cookies.add("merCtx=2");
        cookies.add("_mwus=eyJhY2Nlc3NUb2tlbiI6ImV5SmhiR2NpT2lKSVV6STFOaUlzSW5SNWNDSTZJa3BYVkNKOS5leUppSWpvaVpHTTJZVFUxTkdZek9XVTBZalE0TWpFMFpEZ3pORFF4TVRVNVpXSTVObUkxTURreE56WXpZemRoWkdWa05HTTVOV013WldJek9EUTRPV1ZqWkRNeU1UUXhPRGt6TURRNU5EazJOVEJpTmpFNU5EQXhaV1F5T0dZNFl6azVNMkZpTUdOaE1UUmhabU5rTm1FeVpUazNNR016T0dZMVpqUXpZelE1TlRnNFl6aGxOek5pT1dVd09XSmxPREJtWVROa05EVmlOV0ZpWXpCa05XVTRPRFF3TWpoaU1qbGpaR1ZpTldFeE1URmhOVFprT1RBNE9ETXpOVE01TkRka1l6TXpJaXdpWkdGMFlTSTZleUoxZFdsa0lqb2laMmc2ZHpwbVlqTXpNV0prTWkxbE1XVXdMVFJqWmpndE9EUTVZaTB5T0RabVlXWXhZakpqWTJRaUxDSjFjMlZ5U1dRaU9qVXpOamN6TkRJeU9Td2lZV05qWlhOelZHOXJaVzRpT2lJeU9tUTNZek01WTJNM1pUUTVZV1UyTnpVek5HSmlNR1V3TlRNM1lUWTFNR0ppTXpOaE9EVTNNVFprWkRBMVpUbG1aV1ZrWldZNFpURmxNMlZqT0dZMlpqSWlmU3dpWlhod0lqb3hOVGcyT0RjeE5EZzFMQ0pwWVhRaU9qRTFPRFl5TmpZMk9EVjkuV1JXUFQzTnJ0cVFJZFFnZW1ucVNzVFJIaGkzOTRRc0RhMl9rLXZYNW5jbyIsInJlZnJlc2hUb2tlbiI6ImV5SmhiR2NpT2lKSVV6STFOaUlzSW5SNWNDSTZJa3BYVkNKOS5leUppSWpvaVpHTTJZVFUxTkdZek9XVTBZalE0TWpFMFpEZ3pORFF4TVRVNVpXSTVObUkxTURreE56WXpZemRoWkdWa05HTTVOV013WldJek9EUTRPV1ZqWkRNeU1UUXhPRGt6TURRNU5EazJOVEJpTmpFNU5EQXhaV1F5T0dZNFl6azVNMkZpTUdOaE1UUmhabU5rTm1FeVpUazNNR016T0dZMVpqUXpZelE1TlRnNFl6aGxOek5pT1dVd09XSmxPREJtWVROa05EVmlOV0ZpWXpCa05XVTRPRFF3TWpoaU1qbGpaR1ZpTldFeE1URmhOVFprT1RBNE9ETXpOVE01TkRka1l6TXpJaXdpWkdGMFlTSTZleUoxYzJWeVNXUWlPalV6Tmpjek5ESXlPU3dpZFhWcFpDSTZJbWRvT25jNlptSXpNekZpWkRJdFpURmxNQzAwWTJZNExUZzBPV0l0TWpnMlptRm1NV0l5WTJOa0luMHNJbWxoZENJNk1UVTROakkyTmpZNE5YMC51NTFOcmRDckgwUVNIakF3Y0wxU2lZYXVyaTZQd3Z4cExsNzBzWVdZclFrIiwib3B0aW1pemVFeHBlcmltZW50cyI6W3sidmFyaWFudCI6MCwiZXhwZXJpbWVudCI6Im9iSzh4N0RMVFhHRV9ZN25xbEJfX0EiLCJuYW1lIjoibGlrZV90b19yZWdfaG9sZG91dCIsImV4cGlyZWREYXRlIjoxNTk0MDE5Njk2fSx7InZhcmlhbnQiOjAsImV4cGVyaW1lbnQiOiJRRnNxZnFNUlQtU283akNheFh3a0VnIiwibmFtZSI6ImdldF90aGVfYXBwX2FnYWluc3Rfc2VsbF9ub3ciLCJleHBpcmVkRGF0ZSI6MTU5NDAxOTY5Nn0seyJ2YXJpYW50IjoxLCJleHBlcmltZW50IjoiYkhGMGJUZ1ZRVks0ZHlsQWMtRjZ3ZyIsIm5hbWUiOiJsdXhfaXRlbV9iYW5uZXIiLCJleHBpcmVkRGF0ZSI6MTU5NDAxOTY5Nn0seyJ2YXJpYW50IjowLCJleHBlcmltZW50IjoiQXE1aENKVUdTa0t0M0h3Ym5iWlBEUSIsIm5hbWUiOiJmcmVlX3NoaXBwaW5nX3RodW1iIiwiZXhwaXJlZERhdGUiOjE1OTQwMTk2OTZ9LHsidmFyaWFudCI6MiwiZXhwZXJpbWVudCI6IlJFdk11ekxWU2w2c1NhYzdKQUNqcVEiLCJuYW1lIjoicGF5cGFsX2NyZWRpdCIsImV4cGlyZWREYXRlIjoxNTk0MDE5Njk2fSx7InZhcmlhbnQiOjMsImV4cGVyaW1lbnQiOiJhLTdYaHVJdlFpT2RfSEUweFlNeFNRIiwibmFtZSI6ImdldC10aGUtYXBwLWRlc2t0b3AtMjAyMCIsImV4cGlyZWREYXRlIjoxNTk0MDE5Njk2fSx7InZhcmlhbnQiOjMsImV4cGVyaW1lbnQiOiIzcHBkLUxEVlRxR3dzMzFwTkRtcHZRIiwibmFtZSI6ImdldC10aGUtYXBwLW1vYmlsZS0yMDIwIiwiZXhwaXJlZERhdGUiOjE1OTQwMTk2OTZ9XSwiY3NyZlNlY3JldCI6IlNRdGMyMHNsNEEzX18wYUR4MW11SHlRdiIsInVzZXJJZCI6NTM2NzM0MjI5fQ==");
        return cookies;
    }
}
