package app.housify;

import app.housify.address.AddressDao;
import app.housify.agent.AgentController;
import app.housify.agent.AgentDao;
import app.housify.client.ClientDao;
import app.housify.h2.H2ConnectionManager;
import app.housify.listing.ListingDao;
import app.housify.manage.ManageController;
import app.housify.office.OfficeDao;
import app.housify.property.PropertyDao;
import app.housify.sale.SaleDao;
import app.housify.search.SearchController;
import app.housify.util.Path;
import io.javalin.Javalin;

import static io.javalin.ApiBuilder.*;

public class Main {

    public static H2ConnectionManager connectionManager;
    public static AgentDao agentDao;
    public static AddressDao addressDao;
    public static ClientDao clientDao;
    public static ListingDao listingDao;
    public static OfficeDao officeDao;
    public static PropertyDao propertyDao;
    public static SaleDao saleDao;

    public static void main(String[] args) throws Exception {
        // Initialize H2
        connectionManager = new H2ConnectionManager("./out/h2/housify", "admin", "pencil");

        // Initialize DAOs
        agentDao = new AgentDao();
        addressDao = new AddressDao();
        clientDao = new ClientDao();
        listingDao = new ListingDao();
        officeDao = new OfficeDao();
        propertyDao = new PropertyDao();
        saleDao = new SaleDao();

        // Initialize Javalin
        Javalin app = Javalin.create()
                .port(7000)
                .enableStaticFiles("/public")
                .start();

        app.routes(() -> {
            get(Path.Web.INDEX, AgentController.getAgents);
            path(Path.Web.AGENT, () -> {
                get(AgentController.getAgents);
                path(":id", () -> {
                    get(AgentController.getAgent);
                });
            });
            get(Path.Web.MANAGE, ManageController.renderManage);
            post(Path.Web.SEARCH, ManageController.showListings);
            get(Path.Web.SEARCH, SearchController.renderSearch);
            post(Path.Web.SEARCH, SearchController.performSearch);
            get("error", c -> { throw new Exception("error"); });
        });
    }

}
