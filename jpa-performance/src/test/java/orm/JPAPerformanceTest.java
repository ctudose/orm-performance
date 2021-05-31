package orm;

import org.junit.jupiter.api.Test;
import orm.Item;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JPAPerformanceTest {

    @Test
    void saveUpdateRetrieveDelete() {
        List<Item> items = buildItemsList();
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("orm");

        try {
            EntityManager em = emf.createEntityManager();

            long time1 = System.nanoTime();
            em.getTransaction().begin();
            for (Item item : items) {
                em.persist(item);
            }
            em.getTransaction().commit();
            long time2 = System.nanoTime();
            long timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Insert execution time: " + timeDiff);

            for (Item item : items) {
                item.setInfo("Updated " + item.getInfo());
            }

            time1 = System.nanoTime();
            em.getTransaction().begin();
            for (Item item : items) {
                em.merge(item);
            }
            em.getTransaction().commit();
            time2 = System.nanoTime();
            timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Update execution time: " + timeDiff);

            time1 = System.nanoTime();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Item> cq = cb.createQuery(Item.class);
            Root<Item> rootEntry = cq.from(Item.class);
            CriteriaQuery<Item> all = cq.select(rootEntry);
            TypedQuery<Item> allQuery = em.createQuery(all);
            allQuery.getResultList();
            time2 = System.nanoTime();
            timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Retrieve execution time: " + timeDiff);

            time1 = System.nanoTime();
            em.getTransaction().begin();
            for (Item item : items) {
                em.remove(item);
            }
            em.getTransaction().commit();
            time2 = System.nanoTime();
            timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Delete execution time: " + timeDiff);

            em.close();

        } finally {
            emf.close();
        }
    }

    private int getIterations() {
        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {

            Properties prop = new Properties();

            prop.load(input);

            return Integer.parseInt(prop.getProperty("iterations"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Item> buildItemsList() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < getIterations(); i++) {
            Item item = new Item();
            item.setInfo("Item" + i);
            items.add(item);
        }
        return items;
    }

}
