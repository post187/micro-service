package com.example.Repository;

import com.example.Model.Entity.Favourite;
import com.example.Model.Entity.Id.FavouriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, FavouriteId> {
}
