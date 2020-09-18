package instagram;

import java.util.HashMap;
import java.util.Map;

import database.DatabaseManager;

public class Credentials {
    private String shortLink;

    Credentials(String shortLink) {
        this.shortLink = shortLink;
    }

    public Map<String, String> getLogInDetails(DatabaseManager dbConn) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("mail", dbConn.getInstagramMail(this.shortLink));
        map.put("password", dbConn.getInstagramPassword(this.shortLink));
        return map;
    }
}
