package com.stemapplication.Repository;

import com.stemapplication.Models.StemBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StemBenefitRepository extends JpaRepository<StemBenefit, Long> {
    
    @Query("SELECT sb FROM StemBenefit sb WHERE sb.isActive = true ORDER BY sb.displayOrder ASC")
    List<StemBenefit> findActiveOrderByDisplayOrder();
    
    @Query("SELECT sb FROM StemBenefit sb ORDER BY sb.displayOrder ASC")
    List<StemBenefit> findAllOrderByDisplayOrder();
}
