package com.stemapplication.Repository;

import com.stemapplication.Models.AboutJustification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutJustificationRepository extends JpaRepository<AboutJustification, Long> {
}
