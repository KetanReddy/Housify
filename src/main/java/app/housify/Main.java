package app.housify;

import app.housify.agent.AgentController;
import app.housify.agent.AgentDao;
import app.housify.h2.H2ConnectionManager;
import app.housify.search.SearchController;
import app.housify.util.Path;
import io.javalin.Javalin;

import static io.javalin.ApiBuilder.*;

public class Main {

    public static H2ConnectionManager connectionManager;
    public static AgentDao agentDao;

    public static void main(String[] args) throws Exception {
        // Initialize H2
        connectionManager = new H2ConnectionManager("./out/h2/housify", "admin", "pencil");

        // Initialize DAOs
        agentDao = new AgentDao();

        // Initialize Javalin
        Javalin app = Javalin.create()
                .port(7000)
                .enableStaticFiles("/public")
                .start();

        app.routes(() -> {
            path(Path.Web.AGENT, () -> {
                get(AgentController.getAgents);
                path(":id", () -> {
                    get(AgentController.getAgent);
                });
            });
            get(Path.Web.SEARCH, SearchController.renderSearch);
            post(Path.Web.SEARCH, SearchController.performSearch);
            get("error", c -> { throw new Exception("error"); });
        });
    }

}
