package orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import orm.Item;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemJPAToHibernateTest {

    private static SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(SessionFactory.class);
    }

    @Test
    void saveRetrieveItem() {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("orm");

        try (SessionFactory sessionFactory = getSessionFactory(emf)) {
            Session session = sessionFactory.openSession();

            session.beginTransaction();

            Item item = new Item();
            item.setInfo("Item from JPA to Hibernate");

            session.persist(item);

            session.getTransaction().commit();
            // INSERT into ITEM (ID, INFO)
            // values (1, 'Item from JPA to Hibernate')
            session.beginTransaction();

            CriteriaQuery<Item> criteriaQuery = session.getCriteriaBuilder().createQuery(Item.class);
            criteriaQuery.from(Item.class);

            List<Item> items = session.createQuery(criteriaQuery).getResultList();
            // SELECT * from ITEM

            session.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("Item from JPA to Hibernate", items.get(0).getInfo())
            );

            session.close();

        }
    }

}
