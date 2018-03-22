package app.housify;

import app.housify.agent.AgentController;
import app.housify.agent.AgentDao;
import app.housify.auth.AuthController;
import app.housify.search.SearchController;
import io.javalin.ApiBuilder;
import io.javalin.Javalin;

import static io.javalin.ApiBuilder.*;

public class Main {

    public static H2ConnectionManager connectionManager;
    public static AgentDao agentDao;

    public static void main(String[] args) throws ClassNotFoundException {
        // Initialize H2
        connectionManager = new H2ConnectionManager("~/housify/housify", "user", "pass");

        // Initialize DAOs
        agentDao = new AgentDao();

        // Initialize Javalin
        Javalin app = Javalin.create()
                .port(7000)
                .enableStaticFiles("/public")
                .start();

        app.routes(() -> {
            before(AuthController.ensureLogin);

            path(Path.Web.AGENT, () -> {
                get(AgentController.getAgents);
                path(":id", () -> {
                    get(AgentController.getAgent);
                });
            });
            ApiBuilder.get(Path.Web.SEARCH, SearchController.renderSearch);
            post(Path.Web.SEARCH, SearchController.performSearch);
        });
    }

}
