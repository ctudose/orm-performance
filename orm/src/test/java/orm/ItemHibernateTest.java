package orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;
import orm.Item;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ItemHibernateTest {

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure().addAnnotatedClass(Item.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Test
    void saveRetrieveItem() {

        try (SessionFactory sessionFactory = createSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Item item = new Item();
            item.setInfo("Item from Hibernate");

            session.persist(item);

            session.getTransaction().commit();
            // INSERT into ITEM (ID, INFO)
            // values (1, 'Item from Hibernate')
            session.beginTransaction();

            CriteriaQuery<Item> criteriaQuery = session.getCriteriaBuilder().createQuery(Item.class);
            criteriaQuery.from(Item.class);

            List<Item> items = session.createQuery(criteriaQuery).getResultList();
            // SELECT * from ITEM

            session.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("Item from Hibernate", items.get(0).getInfo())
            );
        }
    }

}
