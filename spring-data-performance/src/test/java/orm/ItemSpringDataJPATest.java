package orm;

import orm.configuration.SpringDataConfiguration;
import orm.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class ItemSpringDataJPATest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void storeUpdateRetrieveDelete() {
        List<Item> items = buildItemsList();

        long time1 = System.nanoTime();
        itemRepository.saveAll(items);
        long time2 = System.nanoTime();
        long timeDiff = (time2 - time1) / 1000_000;
        System.out.println("Insert execution time: " + timeDiff);

        for (Item item : items) {
            item.setInfo("Updated " + item.getInfo());
        }

        time1 = System.nanoTime();
        itemRepository.saveAll(items);
        time2 = System.nanoTime();
        timeDiff = (time2 - time1) / 1000_000;
        System.out.println("Update execution time: " + timeDiff);

        time1 = System.nanoTime();
        itemRepository.findAll();
        time2 = System.nanoTime();
        timeDiff = (time2 - time1) / 1000_000;
        System.out.println("Retrieve execution time: " + timeDiff);

        time1 = System.nanoTime();
        itemRepository.deleteAll(items);
        time2 = System.nanoTime();
        timeDiff = (time2 - time1) / 1000_000;
        System.out.println("Delete execution time: " + timeDiff);
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
