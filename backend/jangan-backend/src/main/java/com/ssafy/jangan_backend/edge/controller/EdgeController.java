package com.ssafy.jangan_backend.edge.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.common.util.AuthUtil;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestDeleteEdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestRegisterEdgeDto;
import com.ssafy.jangan_backend.edge.dto.response.ResponseEdgeIdDto;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "간선(비콘과 비콘 사이 거리)")
@RestController
@RequestMapping("/api/edge")
@RequiredArgsConstructor
public class EdgeController {
    private final EdgeService edgeService;

    @Operation(
            summary = "간선 등록",
            description = "관리자가 간선을 새로 등록."
//            security = @SecurityRequirement(name = "BearerAuth") // JWT 인증 적용
    )
    @PostMapping()
    public BaseResponse saveEdge(HttpSession session, @RequestBody RequestRegisterEdgeDto dto) {
        AuthUtil.authCheck(session);

        ResponseEdgeIdDto responseEdgeIdDto = edgeService.saveEdge(dto);
        return BaseResponse.ok(responseEdgeIdDto);
    }

    @Operation(
            summary = "간선 삭제",
            description = "관리자가 등록된 간선을 삭제"
//            security = @SecurityRequirement(name = "BearerAuth") // JWT 인증 적용
    )
    @DeleteMapping()
    public BaseResponse deleteEdge(HttpSession session, @RequestBody RequestDeleteEdgeDto dto) {
        AuthUtil.authCheck(session);

        edgeService.deleteEdge(dto);
        return BaseResponse.ok();
    }
}
