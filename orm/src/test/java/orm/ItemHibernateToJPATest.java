package orm;

import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import orm.Item;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemHibernateToJPATest {

    private static EntityManagerFactory createEntityManagerFactory() {
        Configuration configuration = new Configuration();
        configuration.configure().addAnnotatedClass(Item.class);

        Map<String, String> properties = new HashMap<>();
        Enumeration<?> propertyNames = configuration.getProperties().propertyNames();
        while (propertyNames.hasMoreElements()) {
            String element = (String) propertyNames.nextElement();
            properties.put(element, configuration.getProperties().getProperty(element));
        }

        return Persistence.createEntityManagerFactory("orm", properties);
    }

    @Test
    void saveRetrieveItem() {

        EntityManagerFactory emf = createEntityManagerFactory();

        try {

            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Item item = new Item();
            item.setInfo("Item from Hibernate to JPA");

            em.persist(item);

            em.getTransaction().commit();
            //INSERT into ITEM (ID, INFO) values (1, 'Item from Hibernate to JPA')

            List<Item> items =
                    em.createQuery("select i from Item i").getResultList();
            //SELECT * from ITEM

            assertAll(
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("Item from Hibernate to JPA", items.get(0).getInfo())
            );

            em.close();

        } finally {
            emf.close();
        }
    }

}
