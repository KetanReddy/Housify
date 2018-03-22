package app.housify.auth;

import app.housify.Path;
import io.javalin.Handler;

public class AuthController {

    public static Handler ensureLogin = context -> {
        if (!context.path().startsWith("/admin")) {
            return;
        } else if (context.request().getSession().getAttribute("currentUser") == null) {
            context.request().getSession().setAttribute("loginRedirect", context.path());
            context.redirect(Path.Web.LOGIN);
        }
    };

}
