package dat.routes;

import dat.config.HibernateConfig;
import dat.controllers.impl.TripController;
import dat.daos.impl.TripDAO;
import dat.daos.impl.GuideDAO;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import dat.security.enums.Role;

public final class TripRoutes {
    private TripRoutes() {}

    public static EndpointGroup getRoutes() {
        var emf  = HibernateConfig.getEntityManagerFactory();
        var ctrl = new TripController(new TripDAO(emf), new GuideDAO(emf));
        return () -> {
            get("/",        ctrl::readAll, Role.ANYONE);
            get("/{id}",    ctrl::read,    Role.ANYONE);
            post("/",       ctrl::create,  Role.USER);
            put("/{id}",    ctrl::update,  Role.USER);
            delete("/{id}", ctrl::delete,  Role.USER);
            put("/{tripId}/guides/{guideId}", ctrl::linkGuide, Role.USER);
        };
    }
}