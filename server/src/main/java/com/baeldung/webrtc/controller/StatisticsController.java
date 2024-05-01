package com.baeldung.webrtc.controller;

import com.baeldung.webrtc.dao.request.CaptureDTO;
import com.baeldung.webrtc.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @PostMapping(value = "/capture")
    public void capture(@RequestBody CaptureDTO captureDto) {
        statisticsService.capture(captureDto.getClientId(), captureDto.getData());
    }

    @RequestMapping(value = "/captures")
    public List<CaptureDTO> getCaptures() {
        return statisticsService.captures();
    }
}
