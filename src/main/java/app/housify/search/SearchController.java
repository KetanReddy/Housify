package app.housify.search;

import app.housify.util.ExtensionsKt;
import app.housify.util.Path;
import io.javalin.Handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static app.housify.Main.listingDao;

public class SearchController {

    public static Handler renderSearch = context -> {
        Map<String, Object> data = new HashMap<>();
        data.put("listings", listingDao.searchActiveListings(ExtensionsKt.extractFormData(context.body())));
        data.put("error", "No active listings meet the search criteria");
        context.renderVelocity(Path.Template.SEARCH, data);
    };

    public static Handler performSearch = context -> {
        Map<String, Object> data = new HashMap<>();
        data.put("listings", listingDao.searchActiveListings(ExtensionsKt.extractFormData(context.body())));
        data.put("error", "No active listings meet the search criteria");
        context.renderVelocity(Path.Template.LISTINGS, data);
    };

}
