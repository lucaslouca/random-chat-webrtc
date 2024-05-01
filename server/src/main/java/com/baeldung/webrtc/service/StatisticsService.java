package com.baeldung.webrtc.service;

import com.baeldung.webrtc.dao.request.CaptureDTO;

import java.util.List;

public interface StatisticsService {

    void capture(String key, String data);

    void removeCapture(String key);

    List<CaptureDTO> captures();

}
