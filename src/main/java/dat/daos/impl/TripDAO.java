package dat.daos.impl;

import dat.daos.IDAO;
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
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Trip existing = em.find(Trip.class, id);
            if (existing == null) {
                tx.rollback();
                return null;
            }
            // if statements gør her at det ikke overskriver den gamle værdi med null,
            // hvis du ikke har lavet om på den værdi
            if (changes.getCountry() != null)
                existing.setCountry(changes.getCountry());
            if (changes.getName() != null)
                existing.setName(changes.getName());
            if (changes.getCategory() != null)
                existing.setCategory(changes.getCategory());
            // bemærk: hvis du ikke vil tillade at prisen bliver sat til 0 ved null, brug Double i stedet for double i DTO’en
            existing.setPrice(changes.getPrice());
            if (changes.getStart() != null)
                existing.setStart(changes.getStart());
            if (changes.getEnd() != null)
                existing.setEnd(changes.getEnd());
            // håndter relationen til guide — optional
            if (changes.getGuide() != null)
                existing.assignGuide(changes.getGuide());
            tx.commit();
            return existing;
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
}