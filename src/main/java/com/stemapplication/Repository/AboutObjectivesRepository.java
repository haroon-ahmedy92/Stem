package com.stemapplication.Repository;

import com.stemapplication.Models.AboutObjectives;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutObjectivesRepository extends JpaRepository<AboutObjectives, Long> {
}
