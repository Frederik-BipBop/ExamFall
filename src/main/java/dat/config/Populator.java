package dat.config;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import dat.utils.TimeMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Populator {

    public static void populate(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide g1 = Guide.builder()
                    .name("Ole Knudsen")
                    .email("OleKnudsen@gmail.com")
                    .phone("+45 2245 7655")
                    .yearsOfExperience(7)
                    .build();
            Guide g2 = Guide.builder()
                    .name("Henrik Nielsen")
                    .email("HenrikNielsen@gmail.com")
                    .phone("+45 3211 2566")
                    .yearsOfExperience(5)
                    .build();
            Guide g3 = Guide.builder()
                    .name("Lise Tronborg")
                    .email("LiseTronborg@gmail.com")
                    .phone("+45 3377 8833")
                    .yearsOfExperience(15)
                    .build();
            em.persist(g1);
            em.persist(g2);
            em.persist(g3);
            Trip t1 = Trip.builder()
                    .country("Denmark")
                    .name("Good Vibes Canoeing")
                    .category(TripCategory.LAKE)
                    .price(1500.0)
                    .start(TimeMapper.StringToLocalDateTime("2025-11-15 09:00"))
                    .end(TimeMapper.StringToLocalDateTime("2025-11-16 18:00"))
                    .build();
            t1.assignGuide(g1);
            Trip t2 = Trip.builder()
                    .country("Spain")
                    .name("Sunny Beach Getaway")
                    .category(TripCategory.BEACH)
                    .price(3200.0)
                    .start(TimeMapper.StringToLocalDateTime("2025-12-05 09:00"))
                    .end(TimeMapper.StringToLocalDateTime("2025-12-07 18:00"))
                    .build();
            t2.assignGuide(g1);
            Trip t3 = Trip.builder()
                    .country("Norway")
                    .name("Mountain Hiking Adventure")
                    .category(TripCategory.FOREST)
                    .price(1200.0)
                    .start(TimeMapper.StringToLocalDateTime("2025-11-20 08:00"))
                    .end(TimeMapper.StringToLocalDateTime("2025-11-22 18:00"))
                    .build();
            t3.assignGuide(g2);
            Trip t4 = Trip.builder()
                    .country("Sweden")
                    .name("Urban Explorer Tour")
                    .category(TripCategory.CITY)
                    .price(2200.0)
                    .start(TimeMapper.StringToLocalDateTime("2025-11-28 10:00"))
                    .end(TimeMapper.StringToLocalDateTime("2025-11-30 17:00"))
                    .build();
            t4.assignGuide(g2);
            Trip t5 = Trip.builder()
                    .country("Iceland")
                    .name("North Sea Expedition")
                    .category(TripCategory.SEA)
                    .price(5300.0)
                    .start(TimeMapper.StringToLocalDateTime("2025-12-12 09:00"))
                    .end(TimeMapper.StringToLocalDateTime("2025-12-16 18:00"))
                    .build();
            t5.assignGuide(g3);
            Trip t6 = Trip.builder()
                    .country("Austria")
                    .name("Expert Skier Deluxe")
                    .category(TripCategory.SNOW)
                    .price(4600.0)
                    .start(TimeMapper.StringToLocalDateTime("2026-01-10 09:00"))
                    .end(TimeMapper.StringToLocalDateTime("2026-01-15 18:00"))
                    .build();
            t6.assignGuide(g3);
            em.persist(t1);
            em.persist(t2);
            em.persist(t3);
            em.persist(t4);
            em.persist(t5);
            em.persist(t6);
            em.getTransaction().commit();
        }
    }
}