package com.orb.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.orb.model.Candle;

public interface CandleRepository extends JpaRepository<Candle, Long> {
}
