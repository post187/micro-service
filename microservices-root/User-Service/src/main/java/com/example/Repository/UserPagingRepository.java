package com.example.Repository;

import com.example.model.Entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserPagingRepository extends PagingAndSortingRepository<User, Long> {
    @Query("SELECT u FROM User u")
    List<User> findAll(Sort sort);
}
