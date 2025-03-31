package com.ssafy.jangan_backend.map;

import com.ssafy.jangan_backend.map.dto.ResponseMobileMapDto;
import com.ssafy.jangan_backend.map.dto.ResponseWebAdminMapDto;
import com.ssafy.jangan_backend.map.service.MapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MapTest {
    @Autowired
    private MapService mapService;
    @Test
    @DisplayName("관리자 맵 조회")
    public void getMapForAdmin() {
        //GIVEN
        int stationId = 222;
        int expectedSize = 9;

        //WHEN
        List<ResponseWebAdminMapDto> dtoList = mapService.getMapsForWebAdmin(stationId);

        //THEN
        assertFalse(dtoList.isEmpty(),"반환된 리스트가 비어있습니다.");
        assertEquals(expectedSize, dtoList.get(0).getBeaconList().size(), "비콘리스트의 수가 예상값과 다릅니다.");
    }

    @Test
    @DisplayName("모바일 시용자 맵 조회")
    public void getMapsForMobile() {
        //GIVEN
        int stationId = 222;
        int expectedSize = 3;

        //WHEN
        List<ResponseMobileMapDto> dtoList = mapService.getMapsForMobile(stationId);

        //THEN
        assertFalse(dtoList.isEmpty(), "반환된 리스트가 비어있습니다.");
        assertEquals(expectedSize, dtoList.size());
    }
}
