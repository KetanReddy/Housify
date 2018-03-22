package app.housify.agent;

import app.housify.Path;
import app.housify.util.ExtensionsKt;
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
        Map<String, Object> data = new HashMap<>();
        Agent agent = agentDao.getAgentByID(context.param("id"));
        if (agent != null) {
            data.put("agent", true);
            // We could leverage current user to determine which information to send
            switch (ExtensionsKt.getAuthType(context)) {
                case MANAGER:
                    // Put employment info in data
                    data.put("salary", agent.getSalary());
                case AGENT:
                    // Put personal info in data
                    data.put("address", agent.getAddress());
                    // TODO: If current user is the same agent, then put salary as well
                default:
                    // Put the rest of the agent info in data
                    data.put("name", agent.getName());
                    data.put("office", agent.getOffice()); // TODO: Should most likely lookup office by ID instead of passing ID to user
                    data.put("telephone", agent.getTelephone());
            }
        }
        context.renderVelocity(Path.Template.AGENT_SINGLE, data);
    };

}
