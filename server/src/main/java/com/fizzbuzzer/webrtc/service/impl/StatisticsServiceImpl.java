package com.fizzbuzzer.webrtc.service.impl;

import com.fizzbuzzer.webrtc.dao.request.CaptureDTO;
import com.fizzbuzzer.webrtc.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    Map<String, String> captures = new ConcurrentHashMap<>();

    @Override
    public void capture(String key, String data) {
        captures.put(key, data);
    }

    @Override
    public void removeCapture(String key) {
        if (captures.containsKey(key)) {
            captures.remove(key);
        }
    }

    @Override
    public List<CaptureDTO> captures() {
        List<CaptureDTO> result = new LinkedList<>();
        for (String key : captures.keySet()) {
            result.add(new CaptureDTO(key, captures.get(key)));
        }
        return result;
    }
}
