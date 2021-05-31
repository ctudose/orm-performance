package orm;

import org.junit.jupiter.api.Test;
import orm.Item;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemJPATest {

    @Test
    void saveRetrieveItem() {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("orm");

        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Item item = new Item();
            item.setInfo("Item");

            em.persist(item);

            em.getTransaction().commit();
            //INSERT into ITEM (ID, INFO) values (1, 'Item')

            em.getTransaction().begin();

            List<Item> items =
                    em.createQuery("select i from Item i").getResultList();
            //SELECT * from ITEM

            items.get(items.size() - 1).setInfo("Item from JPA");

            em.getTransaction().commit();
            //UPDATE ITEM set TEXT = 'Item from JPA' where ID = 1

            assertAll(
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("Item from JPA", items.get(0).getInfo())
            );

            em.close();

        } finally {
            emf.close();
        }
    }

}
