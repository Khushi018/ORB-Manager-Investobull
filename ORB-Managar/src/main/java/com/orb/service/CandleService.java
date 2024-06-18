package com.orb.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orb.exception.InvalidInputException;
import com.orb.exception.ResourceNotFoundException;
import com.orb.model.Candle;
import com.orb.repo.CandleRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandleService {

    @Autowired
    private CandleRepository candleRepository;

    public void saveAll(List<Candle> candles) {
        if (candles == null || candles.isEmpty()) {
            throw new InvalidInputException("Candle list is empty or null");
        }
        candleRepository.saveAll(candles);
    }

    public List<Candle> findAll() {
        List<Candle> candles = candleRepository.findAll();
        if (candles.isEmpty()) {
            throw new ResourceNotFoundException("No candles found in the database");
        }
        return candles;
    }

    public LocalDateTime getOrbCandleTime(int minutes) {
        if (minutes <= 0) {
            throw new InvalidInputException("Minutes must be greater than zero");
        }
        
        List<Candle> candles = findAll();
        int intervalMinutes = 5;
        int openingRangeIntervals = minutes / intervalMinutes;

        double openingRangeHigh = Double.MIN_VALUE;
        double openingRangeLow = Double.MAX_VALUE;

        for (int i = 0; i < openingRangeIntervals; i++) {
            Candle candle = candles.get(i);
            if (candle.getHigh() > openingRangeHigh) {
                openingRangeHigh = candle.getHigh();
            }
            if (candle.getLow() < openingRangeLow) {
                openingRangeLow = candle.getLow();
            }
        }

        for (int i = openingRangeIntervals; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            if (candle.getHigh() > openingRangeHigh || candle.getLow() < openingRangeLow) {
                return candle.getLastTradeTime();
            }
        }

        throw new ResourceNotFoundException("No ORB candle found within the given minutes");
    }

    public List<Candle> aggregateCandles(int intervalMinutes) {
        if (intervalMinutes <= 0 || intervalMinutes % 5 != 0) {
            throw new InvalidInputException("Interval minutes must be a positive multiple of 5");
        }
        
        return findAll();
    }
}
