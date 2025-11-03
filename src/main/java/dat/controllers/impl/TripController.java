package dat.controllers.impl;

import dat.services.FetchTools;
import dat.services.ItemService;
import io.javalin.http.Context;
import dat.daos.impl.TripDAO;
import dat.daos.impl.GuideDAO;
import dat.entities.Trip;
import dat.entities.Guide;
import dat.enums.TripCategory;
import dat.dtos.TripDTO;
import dat.exceptions.ApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TripController {

    private final TripDAO tripDao;
    private final GuideDAO guideDao;
    private final ItemService itemService;


    public TripController(TripDAO tripDao, GuideDAO guideDao) {
        this.tripDao = tripDao;
        this.guideDao = guideDao;
        this.itemService = new ItemService(new FetchTools());

    }

    // GET /trips/:id
    public void read(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Trip t = tripDao.read(id);
        if (t == null) throw new ApiException(404, "Trip " + id + " not found");
        ctx.status(200).json(new TripDTO(t));
    }

    // GET /Trips
    public void readAll(Context ctx) {
        var list = tripDao.readAll().stream()
                .map(TripDTO::new)
                .toList();
        ctx.status(200).json(list);
    }

    // POST /trips  (DTO bruger TripCategory direkte)
    public void create(Context ctx) {
        TripDTO in = ctx.bodyAsClass(TripDTO.class);
        validateCreate(in);
        Trip toSave = Trip.builder()
                .country(in.getCountry())
                .name(in.getName())
                .category(in.getCategory())   // enum direkte
                .price(in.getPrice())
                .start(in.getStart())
                .end(in.getEnd())
                .build();
        if (in.getGuideId() != null) {
            Guide g = guideDao.read(in.getGuideId());
            if (g == null) throw new ApiException(404, "Guide " + in.getGuideId() + " not found");
            toSave.setGuide(g);
        }
        Trip saved = tripDao.create(toSave);
        ctx.status(201).json(new TripDTO(saved));
    }

    // PUT /trips/:id
    public void update(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Trip existing = tripDao.read(id);
        if (existing == null) throw new ApiException(404, "Trip " + id + " not found");
        TripDTO in = ctx.bodyAsClass(TripDTO.class);
        validateUpdate(in);
        if (in.getCountry() != null) existing.setCountry(in.getCountry());
        if (in.getName() != null) existing.setName(in.getName());
        if (in.getCategory() != null) existing.setCategory(in.getCategory()); // enum direkte
        // price er primitive double i din DTO – overvej at gøre den til Double hvis du vil gøre den valgfri
        existing.setPrice(in.getPrice());
        if (in.getStart() != null) existing.setStart(in.getStart());
        if (in.getEnd() != null) existing.setEnd(in.getEnd());
        if (in.getGuideId() != null) {
            Guide g = guideDao.read(in.getGuideId());
            if (g == null) throw new ApiException(404, "Guide " + in.getGuideId() + " not found");
            existing.setGuide(g);
        }
        Trip updated = tripDao.update(id, existing);
        ctx.status(200).json(new TripDTO(updated));
    }

    public void delete(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Trip existing = tripDao.read(id);
        if (existing == null) throw new ApiException(404, "Trip " + id + " not found");
        tripDao.delete(id);
        ctx.status(204);
    }

    // PUT /trips/:tripId/guides/:guideId
    public void linkGuide(Context ctx) {
        long tripId = ctx.pathParamAsClass("tripId", Long.class).get();
        long guideId = ctx.pathParamAsClass("guideId", Long.class).get();
        Trip updated = tripDao.linkGuideToTrip(tripId, guideId);
        if (updated == null) {
            throw new ApiException(404, "Trip or Guide not found");
        }
        ctx.status(200).json(new TripDTO(updated));
    }

    // GET /trips?category=forest  OG  /trips/category/forest
    public void getByCategory(Context ctx) {
        // 1) path-param hvis til stede
        String catParam = ctx.pathParamMap().getOrDefault("category", null);
        // 2) ellers query-param
        if (catParam == null || catParam.isBlank()) catParam = ctx.queryParam("category");
        if (catParam == null || catParam.isBlank()) {
            ctx.status(200).json(tripDao.readAll().stream().map(TripDTO::new).toList());
            return;
        }
        TripCategory cat;
        try {
            cat = TripCategory.fromValue(catParam); // håndterer case/trim i din enum
        } catch (IllegalArgumentException ex) {
            throw new ApiException(400, "Unknown category: " + catParam);
        }
        var list = tripDao.findByCategory(cat);
        ctx.status(200).json(list.stream().map(TripDTO::new).toList());
    }


    // GET /trips/guides/totalprice
    public void getGuideTotals(Context ctx) {
        var totals = guideDao.getTotalPricePerGuide(); // returnerer List<GuidePriceDTO>
        ctx.status(200).json(totals);
    }


    // GET /trips/{id}/packing  -> { "items": [...] }
    public void getTripItems(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Trip trip = tripDao.read(id);
        if (trip == null) throw new ApiException(404, "Trip " + id + " not found");
        var resp = itemService.getItems(trip.getCategory().name().toLowerCase()); // API'en forventer lower-case
        var items = (resp == null || resp.getItems() == null) ? List.of() : List.of(resp.getItems());
        ctx.status(200).json(Map.of("items", items));
    }

    // GET /trips/{id}/packing/weight  ->  number
    public void getTripItemWeights(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        var trip = tripDao.read(id);
        if (trip == null) throw new ApiException(404, "Trip " + id + " not found");
        var resp = itemService.getItems(trip.getCategory().name().toLowerCase());
        double grams = Arrays.stream(resp.getItems())
                .mapToDouble(i -> i.getWeightInGrams() * Math.max(i.getQuantity(), 1))
                .sum();
        ctx.status(200).json(grams); // kun tallet
    }




    /* ---------- Simple validering ---------- */

    private void validateCreate(TripDTO dto) {
        if (dto == null) throw new ApiException(400, "Missing body");
        if (dto.getName() == null || dto.getName().isBlank())
            throw new ApiException(400, "Field 'name' is required");
        if (dto.getCountry() == null || dto.getCountry().isBlank())
            throw new ApiException(400, "Field 'country' is required");
        if (dto.getCategory() == null) // enum må ikke være null ved create
            throw new ApiException(400, "Field 'category' is required");
        if (dto.getStart() == null || dto.getEnd() == null || dto.getStart().isAfter(dto.getEnd()))
            throw new ApiException(400, "Field 'start' must be before 'end'");
        if (dto.getPrice() < 0) throw new ApiException(400, "price must be >= 0");
    }

    private void validateUpdate(TripDTO dto) {
        if (dto == null) throw new ApiException(400, "Missing body");
        if (dto.getStart() != null && dto.getEnd() != null && dto.getStart().isAfter(dto.getEnd()))
            throw new ApiException(400, "start must be before end");
    }
}
