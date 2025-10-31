package dat.config;

import dat.entities.Guide;
import dat.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

public class Populator {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide g1 = Guide.builder()
                    .name("Ole Knudsen")
                    .email("OleKnudsen@Gmail.com")
                    .phone("+4522457655")
                    .yearsOfExperience(7)
                    .build();
            Guide g2 = Guide.builder()
                    .name("Henrik Nielsen")
                    .email("HenrikNielsen@Gmail.com")
                    .phone("+4532112566")
                    .yearsOfExperience(5)
                    .build();
            Guide g3 = Guide.builder()
                    .name("Lise Tronborg")
                    .email("LiseTronborg@gmail.com")
                    .phone("+4533778833")
                    .yearsOfExperience(15)
                    .build();
            em.persist(g1);
            em.persist(g2);
            em.persist(g3);

            Trip t1 = Trip.builder()
                    .country("Denmark")
                    .name("Good vibes Canoeing")
                    .category("Canoe")
                    .price(1500.0)
                    .start(LocalDateTime.now().plusDays(7))
                    .end(LocalDateTime.now().plusDays(8))
                    .guide(g1)
                    .build();

            Trip t2 = Trip.builder()
                    .country("Sweden")
                    .name("Expert Skier")
                    .category("Ski")
                    .price(4500.0)
                    .start(LocalDateTime.now().plusDays(20))
                    .end(LocalDateTime.now().plusDays(24))
                    .guide(g3)
                    .build();

            Trip t3 = Trip.builder()
                    .country("Norway")
                    .name("Mountain Hiking")
                    .category("Hike")
                    .price(1200.0)
                    .start(LocalDateTime.now().plusDays(2))
                    .end(LocalDateTime.now().plusDays(3))
                    .guide(g2)
                    .build();

            em.persist(t1);
            em.persist(t2);
            em.persist(t3);


            em.getTransaction().commit();
            System.out.println("Testdata inserted successfully.");
        }
    }
}
