package dat.config;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;

public class Populator {

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // === GUIDES ===
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
            // === TRIPS ===
            Trip t1 = Trip.builder()
                    .country("Denmark")
                    .name("Good Vibes Canoeing")
                    .category(TripCategory.LAKE)
                    .price(1500.0)
                    .start(LocalDateTime.now().plusDays(7))
                    .end(LocalDateTime.now().plusDays(8))
                    .guide(g1)
                    .build();
            Trip t2 = Trip.builder()
                    .country("Spain")
                    .name("Sunny Beach Getaway")
                    .category(TripCategory.BEACH)
                    .price(3200.0)
                    .start(LocalDateTime.now().plusDays(14))
                    .end(LocalDateTime.now().plusDays(17))
                    .guide(g1)
                    .build();
            Trip t3 = Trip.builder()
                    .country("Norway")
                    .name("Mountain Hiking Adventure")
                    .category(TripCategory.FOREST)
                    .price(1200.0)
                    .start(LocalDateTime.now().plusDays(3))
                    .end(LocalDateTime.now().plusDays(5))
                    .guide(g2)
                    .build();
            Trip t4 = Trip.builder()
                    .country("Sweden")
                    .name("Urban Explorer Tour")
                    .category(TripCategory.CITY)
                    .price(2200.0)
                    .start(LocalDateTime.now().plusDays(10))
                    .end(LocalDateTime.now().plusDays(12))
                    .guide(g2)
                    .build();
            Trip t5 = Trip.builder()
                    .country("Iceland")
                    .name("North Sea Expedition")
                    .category(TripCategory.SEA)
                    .price(5300.0)
                    .start(LocalDateTime.now().plusDays(21))
                    .end(LocalDateTime.now().plusDays(25))
                    .guide(g3)
                    .build();
            Trip t6 = Trip.builder()
                    .country("Austria")
                    .name("Expert Skier Deluxe")
                    .category(TripCategory.SNOW)
                    .price(4600.0)
                    .start(LocalDateTime.now().plusDays(30))
                    .end(LocalDateTime.now().plusDays(35))
                    .guide(g3)
                    .build();
            em.persist(t1);
            em.persist(t2);
            em.persist(t3);
            em.persist(t4);
            em.persist(t5);
            em.persist(t6);
            em.getTransaction().commit();
            System.out.println(" Test data inserted successfully ;)");

        }
    }
}
