package app.housify.manage;

import app.housify.search.SearchResult;
import app.housify.util.Path;
import io.javalin.Handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ManageController {
    public static Handler renderManage = context -> {
        Map<String, Object> data = new HashMap<>();
        context.renderVelocity(Path.Template.MANAGE, data);
    };

    public static Handler showListings = context -> {
        Map<String, Object> data = new HashMap<>();
        // TODO: Hook into SearchDao
        data.put("results", Arrays.asList(
                new SearchResult(0),
                new SearchResult(1),
                new SearchResult(2)
        ));
        context.renderVelocity(Path.Template.LISTINGS, data);
    };

}
