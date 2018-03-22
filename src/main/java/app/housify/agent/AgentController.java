package app.housify.agent;

import app.housify.util.Path;
import io.javalin.Handler;

import java.util.HashMap;
import java.util.Map;

import static app.housify.Main.*;

public class AgentController {

    public static Handler getAgents = context -> {
        Map<String, Object> data = new HashMap<>();
        data.put("agents", agentDao.getAgents());
        context.renderVelocity(Path.Template.AGENT_LIST, data);
    };

    public static Handler getAgent = context -> {
        context.renderVelocity(Path.Template.AGENT_SINGLE, agentDao.getAgentByID(context.param("id")));
    };

}
