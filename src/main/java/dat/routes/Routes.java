package dat.routes;

import dat.security.routes.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class Routes {
    private static final Logger LOG = LoggerFactory.getLogger(Routes.class);

    public EndpointGroup getRoutes() {
        LOG.info("Registering API routes...");
        return () -> {
            path("/auth",  SecurityRoutes.getSecurityRoutes()); // skal returnere EndpointGroup
            path("/trips", TripRoutes.getRoutes());             // static metode, returnerer EndpointGroup
        }; // <- semikolon her!
    }
}
