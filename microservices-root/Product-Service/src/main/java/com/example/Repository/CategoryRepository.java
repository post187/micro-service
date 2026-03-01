package com.example.Repository;

import com.example.Model.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAll(Pageable pageable);

    // Thêm các phương thức tìm kiếm hoặc lọc tại đây
    Page<Category> findByCategoryTitleContaining(String categoryTitle, Pageable pageable);

}
