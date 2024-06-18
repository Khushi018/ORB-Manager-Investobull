package com.orb.controller;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orb.exception.InvalidInputException;
import com.orb.model.Candle;
import com.orb.service.CandleService;

@RestController
@RequestMapping("/api/candles")
public class CandleController {

    @Autowired
    private CandleService candleService;

    @PostMapping("/upload")
    public String uploadCandles(@RequestParam("file") MultipartFile file) {
    	if (file.isEmpty()) {
            throw new InvalidInputException("File is empty");
        }
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Candle> candles = objectMapper.readValue(inputStream, new TypeReference<List<Candle>>() {});
            candleService.saveAll(candles);
            return "Candles uploaded successfully.";
        } catch (IOException e) {
            throw new InvalidInputException("Failed to upload candles: " + e.getMessage());
        }
    }

    @GetMapping("/orb-time")
    public String getOrbCandleTime(@RequestParam("minutes") int minutes) {
        LocalDateTime orbTime = candleService.getOrbCandleTime(minutes);
        return "ORB candle generated at " + orbTime.toString();
    }

    @GetMapping("/aggregate")
    public List<Candle> aggregateCandles(@RequestParam("intervalMinutes") int intervalMinutes) {
        return candleService.aggregateCandles(intervalMinutes);
    }
}
