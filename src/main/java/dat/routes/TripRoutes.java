package dat.routes;

import dat.config.HibernateConfig;
import dat.controllers.impl.TripController;
import dat.daos.impl.TripDAO;
import dat.daos.impl.GuideDAO;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

import dat.security.enums.Role;

public final class TripRoutes {
    private TripRoutes() {
    }

    public static EndpointGroup getRoutes() {
        var emf = HibernateConfig.getEntityManagerFactory();
        var ctrl = new TripController(new TripDAO(emf), new GuideDAO(emf));
        return () -> {
            // Collection (kun ÉN på "/")
            get("/", ctrl::getByCategory, Role.ANYONE);              // /trips  og /trips?category=forest
            get("/category/{category}", ctrl::getByCategory, Role.ANYONE); // /trips/category/forest
            // Aggregates før dynamiske id’er
            get("/guides/totalprice", ctrl::getGuideTotals, Role.ANYONE);
            // Item
            get("/{id}", ctrl::read, Role.ANYONE);
            get("/{id}/packing", ctrl::getTripItems, Role.ANYONE);
            get("/{id}/packing/weight", ctrl::getTripItemWeights, Role.ANYONE);
            // Mutations
            post("/", ctrl::create, Role.ADMIN);
            put("/{id}", ctrl::update, Role.ADMIN);
            delete("/{id}", ctrl::delete, Role.ADMIN);
            put("/{tripId}/guides/{guideId}", ctrl::linkGuide, Role.ADMIN);
        };
    }
}