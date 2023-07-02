package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(String name,
                                                                                                     String description);

    Page<Item> findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(String name,
                                                                                                     String description,
                                                                                                     Pageable pageable);

    @Query(value = "select * from items where user_id = ? order by id;", nativeQuery = true)
    List<Item> findByOwnerId(Integer id);

    Page<Item> findByOwner(User user, Pageable pageable);

    @Query(value = "select * from items where request_id = ?;", nativeQuery = true)
    List<Item> findByRequestId(Integer id);
}

