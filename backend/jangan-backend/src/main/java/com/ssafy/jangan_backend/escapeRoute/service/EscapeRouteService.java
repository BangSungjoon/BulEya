package com.ssafy.jangan_backend.escapeRoute.service;

import com.ssafy.jangan_backend.beacon.service.BeaconService;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import com.ssafy.jangan_backend.escapeRoute.dto.EscapeRouteDto;
import com.ssafy.jangan_backend.firelog.entity.EscapeRoute;
import com.ssafy.jangan_backend.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EscapeRouteService {
    private final MapService mapService;
    private final EdgeService edgeService;
    private final BeaconService  beaconService;
    public EscapeRouteDto getEscapeRoute() {


        return null;
    }
}
