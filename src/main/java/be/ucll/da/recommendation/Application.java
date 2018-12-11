package be.ucll.da.recommendation;

import be.ucll.da.recommendation.controllers.RecommendedItem;
import be.ucll.da.recommendation.model.Item;
import be.ucll.da.recommendation.model.User;
import be.ucll.da.recommendation.repository.RecommendedItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@EnableDiscoveryClient
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static UUID Romeinen = UUID.fromString("b4357989-e3b1-42f8-a325-c2591e5c6ad8");
    public static UUID Toverdrank = UUID.fromString("b4357989-e3b1-42f8-a325-c2591e5c6ad9");
    public static UUID Everzwijnen = UUID.fromString("b4357989-e3b1-42f8-a325-c2591e5c6ad7");

    @Bean
    public CommandLineRunner demo(RecommendedItemRepository repository) {
        return (args) -> {
            HashMap<User, Map<Item, Float>> userMap = new HashMap<>();

            HashMap<Item, Float> asterixPreferences = new HashMap<>();
            asterixPreferences.put(new Item(Toverdrank), 4.8f);
            asterixPreferences.put(new Item(Romeinen), 1.2f);
            asterixPreferences.put(new Item(Everzwijnen), 4.6f);
            userMap.put(new User("asterix@ucll.be"), asterixPreferences);

            HashMap<Item, Float> obelixPreferences = new HashMap<>();
            obelixPreferences.put(new Item(Toverdrank), 4.4f);
            obelixPreferences.put(new Item(Romeinen), 3.2f);
            obelixPreferences.put(new Item(Everzwijnen), 5f);
            userMap.put(new User("obelix@ucll.be"), obelixPreferences);

            HashMap<Item, Float> panoramixPreferences = new HashMap<>();
            panoramixPreferences.put(new Item(Toverdrank), 4.2f);
            panoramixPreferences.put(new Item(Romeinen), 2.2f);
            panoramixPreferences.put(new Item(Everzwijnen), 4.1f);
            userMap.put(new User("idefix@ucll.be"), panoramixPreferences);

            HashMap<Item, Float> ronaldPreferences = new HashMap<>();
            ronaldPreferences.put(new Item(Toverdrank), 4.9f);
            userMap.put(new User("ronald.dehuysser@ucll.be"), ronaldPreferences);

            saveUserMapToRepository(repository, userMap);
        };
    }

    private void saveUserMapToRepository(RecommendedItemRepository repository, HashMap<User, Map<Item, Float>> userMap) {
        List<RecommendedItem> recommendedItems = userMap.entrySet().stream()
                .flatMap(entry -> toRecommendedItems(entry))
                .collect(Collectors.toList());

        repository.saveAll(recommendedItems);
    }

    private Stream<RecommendedItem> toRecommendedItems(Map.Entry<User, Map<Item, Float>> entry) {
        List<RecommendedItem> recommendedItems = new ArrayList<>();
        for(Item recommendedItem : entry.getValue().keySet()) {
            RecommendedItem aRecommendedItem = new RecommendedItem();
            aRecommendedItem.setEmailAddress(entry.getKey().toString());
            aRecommendedItem.setRatedItem(UUID.fromString(recommendedItem.toString()));
            aRecommendedItem.setRating(entry.getValue().get(recommendedItem));
            recommendedItems.add(aRecommendedItem);
        }
        return recommendedItems.stream();
    }
}
