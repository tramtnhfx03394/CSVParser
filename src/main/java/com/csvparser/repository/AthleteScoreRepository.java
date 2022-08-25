package com.csvparser.repository;

import com.csvparser.entity.AthleteScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteScoreRepository extends JpaRepository<AthleteScore, Integer> {
}
