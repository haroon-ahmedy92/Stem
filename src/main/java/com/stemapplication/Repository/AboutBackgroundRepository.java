package com.stemapplication.Repository;

import com.stemapplication.Models.AboutBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutBackgroundRepository extends JpaRepository<AboutBackground, Long> {
}
