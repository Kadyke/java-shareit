package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query(value = "select * from requests where user_id = ? order by created_time desc;", nativeQuery = true)
    List<ItemRequest> findByUserId(Integer id);

    Page<ItemRequest> findByRequesterNot(User user, Pageable pageable);

    List<ItemRequest> findByRequesterNot(User user, Sort sort);
}
