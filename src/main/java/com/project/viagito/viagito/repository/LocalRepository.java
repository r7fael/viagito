package com.project.viagito.viagito.repository;

import com.project.viagito.viagito.model.Category;
import com.project.viagito.viagito.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
    List<Local> findByCategoryIn(Collection<Category> categories);
}
