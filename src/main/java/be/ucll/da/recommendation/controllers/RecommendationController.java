package be.ucll.da.recommendation.controllers;

import be.ucll.da.recommendation.model.Item;
import be.ucll.da.recommendation.model.SlopeOne;
import be.ucll.da.recommendation.model.User;
import be.ucll.da.recommendation.repository.RecommendedItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;


@RestController
public class RecommendationController {

    private RecommendedItemRepository repository;

    public RecommendationController(RecommendedItemRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public RecommendedItem recommendItem(@RequestBody RecommendedItem recommendedItem) {
        return repository.save(recommendedItem);
    }

    @RequestMapping("/recommend/{emailAddress}")
    public Map<Item, Float> getRecommendedItems(@PathVariable String emailAddress) {
        List<RecommendedItem> recommendedItemsByEmailAddress = repository.findAllByEmailAddress(emailAddress);

        if(recommendedItemsByEmailAddress.isEmpty()) {
            return null;
        }

        SlopeOne slopeOnePredictionMachine = getSlopeOnePredictionMachine();
        Map<Item, Float> userPrefences = mapToSlopeOneInput(recommendedItemsByEmailAddress);

        return slopeOnePredictionMachine.predict(userPrefences);
    }

    private Map<Item, Float> mapToSlopeOneInput(List<RecommendedItem> recommendedItemsByEmailAddress) {
        HashMap<Item, Float> userPreferences = new HashMap<>();

        for (RecommendedItem recommendedItem : recommendedItemsByEmailAddress) {
            userPreferences.put(new Item(recommendedItem.getRatedItem()), recommendedItem.getRating());
        }
        return userPreferences;
    }

    private SlopeOne getSlopeOnePredictionMachine() {
        Map<User, Map<Item, Float>> allSavedPreferences = new HashMap<>();
        StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .forEach(item -> addRecommendedItemToAllSavedPreferences(allSavedPreferences, item));

        return new SlopeOne(allSavedPreferences);
    }

    private void addRecommendedItemToAllSavedPreferences(Map<User, Map<Item, Float>> allSavedPreferences, RecommendedItem item) {
        User user = new User(item.getEmailAddress());
        if(!allSavedPreferences.containsKey(user)) {
            allSavedPreferences.put(user, new HashMap<>());
        }

        Map<Item, Float> userPrefences = allSavedPreferences.get(user);
        userPrefences.put(new Item(item.getRatedItem()), item.getRating());
    }
}
