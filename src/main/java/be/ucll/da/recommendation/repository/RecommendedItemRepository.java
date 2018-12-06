package be.ucll.da.recommendation.repository;

import be.ucll.da.recommendation.controllers.RecommendedItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RecommendedItemRepository extends CrudRepository<RecommendedItem, UUID> {

    List<RecommendedItem> findAllByEmailAddress(String emailAddress);

}
