package orm;

import orm.configuration.SpringDataConfiguration;
import orm.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class ItemSpringDataJPATest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void saveRetrieveItem() {
        Item item = new Item();
        item.setInfo("Item from Spring Data JPA");

        System.out.println(itemRepository.getClass());

        itemRepository.save(item);

        List<Item> items = (List<Item>) itemRepository.findByInfo("Item from Spring Data JPA");

        assertAll(
                () -> assertEquals(1, items.size()),
                () -> assertEquals("Item from Spring Data JPA", items.get(0).getInfo())
        );

    }

}
