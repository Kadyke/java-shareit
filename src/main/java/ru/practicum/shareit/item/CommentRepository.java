package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select * from comments where item_id = ?;", nativeQuery = true)
    List<Comment> findByItemId(Integer id);
}
