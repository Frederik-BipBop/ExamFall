package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.GuidePriceDTO;
import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TripDAO implements IDAO<Trip, Long> {

    private final EntityManagerFactory emf;

    public TripDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Trip read(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Trip.class, id);
        }
    }

    @Override
    public List<Trip> readAll() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("SELECT t FROM Trip t", Trip.class)
                    .getResultList();
        }
    }

    @Override
    public Trip create(Trip trip) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.persist(trip);
            tx.commit();
            return trip;
        }
    }

    @Override
    public Trip update(Long id, Trip changes) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Trip existing = em.find(Trip.class, id);
            if (existing == null) {
                tx.rollback();
                return null;
            }
            // opdater felter
            if (changes.getName() != null) existing.setName(changes.getName());
            if (changes.getCountry() != null) existing.setCountry(changes.getCountry());
            if (changes.getCategory() != null) existing.setCategory(changes.getCategory());
            existing.setPrice(changes.getPrice());
            if (changes.getStart() != null) existing.setStart(changes.getStart());
            if (changes.getEnd() != null) existing.setEnd(changes.getEnd());
            if (changes.getGuide() != null) existing.setGuide(changes.getGuide());
            tx.commit();
            // Hent igen med fetch join, så guide er initialiseret
            return em.createQuery(
                    "SELECT t FROM Trip t LEFT JOIN FETCH t.guide WHERE t.id = :id",
                    Trip.class
            ).setParameter("id", id).getSingleResult();
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Trip t = em.find(Trip.class, id);
            if (t != null) em.remove(t);
            tx.commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Trip.class, id) != null;
        }
    }

    public Trip linkGuideToTrip(Long tripId, Long guideId) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);
            if (trip == null || guide == null)
                throw new IllegalArgumentException("Trip or guide not found");
            trip.setGuide(guide);
            tx.commit();
            return trip;
        }
    }

    public List<Trip> findByCategory(TripCategory category) {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("SELECT t FROM Trip t WHERE t.category = :c", Trip.class)
                    .setParameter("c", category)
                    .getResultList();
        }
    }

}