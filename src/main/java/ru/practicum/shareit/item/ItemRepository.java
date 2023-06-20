package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(String name,
                                                                                                     String description);

    @Query(value = "select * from items where user_id = ?;", nativeQuery = true)
    List<Item> findByOwnerId(Integer id);

}

