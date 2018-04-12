package app.housify.manage;

import app.housify.util.ExtensionsKt;
import app.housify.util.Path;
import io.javalin.Handler;

import java.util.HashMap;
import java.util.Map;

import static app.housify.Main.*;

public class ManageController {
    public static Handler renderManage = context -> {
        Map<String, Object> data = new HashMap<>();
        data.put("offices", officeDao.getOffices());
        data.put("agents", agentDao.getAgents());
        data.put("metrics", saleDao.getMetrics());
        data.put("listings", listingDao.getActiveListings());
        context.renderVelocity(Path.Template.MANAGE, data);
    };

    public static Handler showListings = context -> {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> form = ExtensionsKt.extractFormData(context.body());

        String agentId = form.get("agent");
        String officeId = form.get("office");
        Boolean queryAgentMetrics = false;
        Boolean queryOfficeMetrics = false;
        if (officeId != null && !officeId.isEmpty()) queryOfficeMetrics = true;
        if (agentId != null && !agentId.isEmpty()) queryAgentMetrics = true;

        if (queryAgentMetrics && queryOfficeMetrics) {
            data.put("metrics", saleDao.getOfficeAgentMetrics(officeId, agentId));
            data.put("listings", listingDao.getOfficeAgentActiveListings(officeId, agentId));
        } else if (queryOfficeMetrics) {
            data.put("metrics", saleDao.getOfficeMetrics(officeId));
            data.put("listings", listingDao.getOfficeActiveListings(officeId));
        } else if (queryAgentMetrics) {
            data.put("metrics", saleDao.getAgentMetrics(agentId));
            data.put("listings", listingDao.getAgentActiveListings(agentId));
        } else {
            data.put("metrics", saleDao.getMetrics());
            data.put("listings", listingDao.getActiveListings());
        }

        context.renderVelocity(Path.Template.AGENT_METRICS, data);
    };

}
