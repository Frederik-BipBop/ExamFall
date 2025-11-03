package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.GuidePriceDTO;
import dat.entities.Guide;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideDAO implements IDAO<Guide, Long> {

    private final EntityManagerFactory emf;

    public GuideDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Guide read(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Guide.class, id);
        }
    }

    @Override
    public List<Guide> readAll() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("SELECT g FROM Guide g", Guide.class)
                    .getResultList();
        }
    }

    @Override
    public Guide create(Guide guide) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.persist(guide);
            tx.commit();
            return guide;
        }
    }

    @Override
    public Guide update(Long id, Guide changes) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Guide existing = em.find(Guide.class, id);
            if (existing == null) {
                tx.rollback();
                return null;
            }
            // opdater kun felter du tillader at ændre:
            existing.setName(changes.getName());
            existing.setEmail(changes.getEmail());
            existing.setPhone(changes.getPhone());
            existing.setYearsOfExperience(changes.getYearsOfExperience());
            tx.commit();
            return existing;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Guide g = em.find(Guide.class, id);
            if (g != null) em.remove(g);
            tx.commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Guide.class, id) != null;
        }
    }
    public List<GuidePriceDTO> getTotalPricePerGuide() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT new dat.dtos.GuidePriceDTO(g.id, COALESCE(SUM(t.price), 0)) " +
                            "FROM Guide g LEFT JOIN g.trips t " +
                            "GROUP BY g.id " +
                            "ORDER BY g.id ASC",
                    GuidePriceDTO.class
            ).getResultList();
        }
    }
}


