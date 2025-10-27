package dat.security.routes;

import com.fasterxml.jackson.databind.ObjectMapper;

import dat.utils.Utils;
import dat.security.controllers.SecurityController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To handle security in the API
 *  Author: Thomas Hartmann
 */
public class SecurityRoutes {
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController = SecurityController.getInstance();
    public static EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", ()->{
                get("/healthcheck", securityController::healthCheck, Role.ANYONE);
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello World!")), Role.ANYONE);
                post("/register", securityController.register(), Role.ANYONE);
                post("/login", securityController.login(), Role.ANYONE);
                post("/user/role", securityController.addRole(), Role.USER);
            });
        };
    }
}
