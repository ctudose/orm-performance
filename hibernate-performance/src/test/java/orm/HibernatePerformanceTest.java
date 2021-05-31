package orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;
import orm.Item;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HibernatePerformanceTest {

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure().addAnnotatedClass(Item.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Test
    void saveUpdateRetrieveDelete() {
        List<Item> items = buildItemsList();

        try (SessionFactory sessionFactory = createSessionFactory();
             Session session = sessionFactory.openSession()) {

            long time1 = System.nanoTime();
            session.beginTransaction();
            for (Item item : items) {
                session.persist(item);
            }
            session.getTransaction().commit();
            long time2 = System.nanoTime();
            long timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Insert execution time: " + timeDiff);

            for (Item item : items) {
                item.setInfo("Updated " + item.getInfo());
            }

            time1 = System.nanoTime();
            session.beginTransaction();
            for (Item item : items) {
                session.update(item);
            }
            session.getTransaction().commit();
            time2 = System.nanoTime();
            timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Update execution time: " + timeDiff);

            time1 = System.nanoTime();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
            criteria.from(Item.class);
            session.createQuery(criteria).getResultList();
            time2 = System.nanoTime();
            timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Retrieve execution time: " + timeDiff);

            time1 = System.nanoTime();
            session.beginTransaction();
            for (Item item : items) {
                session.remove(item);
            }
            session.getTransaction().commit();
            time2 = System.nanoTime();
            timeDiff = (time2 - time1) / 1000_000;
            System.out.println("Delete execution time: " + timeDiff);
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
