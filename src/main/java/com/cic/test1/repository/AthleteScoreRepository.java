package com.cic.test1.repository;

import com.cic.test1.entity.AthleteScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteScoreRepository extends JpaRepository<AthleteScore, Integer> {
}
