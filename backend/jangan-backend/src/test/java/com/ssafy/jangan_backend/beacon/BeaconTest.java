package com.ssafy.jangan_backend.beacon;

import com.ssafy.jangan_backend.beacon.dto.request.RequestDeleteBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseBeaconIdDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.beacon.service.BeaconService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BeaconTest {
    @Autowired
    private BeaconService beaconService;
    @Autowired
    private BeaconRepository beaconRepository;
    @Test
    @Transactional
    @Rollback
    @DisplayName("비콘 등록")
    public void registerBeacon() {
        //GIVEN
        RequestRegisterBeaconDto dto = RequestRegisterBeaconDto.builder()
                .stationId(222)
                .floor(1001)
                .beaconCode(1010)
                .name("만남의 광장10")
                .coordX(100)
                .coordY(100)
                .isExit(true)
                .isCctv(false)
                .build();
        //WHEN
        ResponseBeaconIdDto responseBeaconIdDto = beaconService.saveBeacon(dto);
        //THEN
        assertFalse(responseBeaconIdDto.getBeaconId()==null, "등록된 비콘 아이디가 없습니다.");
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("특정 id 비콘 삭제")
    public void removeBeacon() {
        //GIVEN
        int deletedBeaconId = 1;
        RequestDeleteBeaconDto dto = RequestDeleteBeaconDto.builder()
                .beaconId(deletedBeaconId)
                .build();

        //WHEN
         beaconService.deleteBeacon(dto);

        //THEN
        //비콘을 삭제 후 다시 조회했을 때, 존재하면 에러발생
        Optional<Beacon> deletedBeacon = beaconRepository.findById(deletedBeaconId);
        assertEquals(deletedBeacon.isEmpty(),true, "삭제된 비콘은 조회되지 않아야 합니다");
    }
}
