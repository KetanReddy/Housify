package app.housify.search;

import app.housify.Path;
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
        data.put("results", Arrays.asList(
                new SearchResult(0),
                new SearchResult(1),
                new SearchResult(2)
        ));
        context.renderVelocity(Path.Template.SEARCH_RESULTS, data);
    };

}
