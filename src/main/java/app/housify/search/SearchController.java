package app.housify.search;

import app.housify.util.Path;
import io.javalin.Handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SearchController {

    public static Handler renderSearch = context -> {
        Map<String, Object> data = new HashMap<>();
        context.renderVelocity(Path.Template.SEARCH, data);
    };

    public static Handler performSearch = context -> {
        Map<String, Object> data = new HashMap<>();
        // TODO: Hook into SearchDao
        data.put("results", Arrays.asList(
                new SearchResult(0),
                new SearchResult(1),
                new SearchResult(2)
        ));
        context.renderVelocity(Path.Template.SEARCH_RESULTS, data);
    };

}
