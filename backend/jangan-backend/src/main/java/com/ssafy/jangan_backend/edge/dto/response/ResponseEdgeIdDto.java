package com.ssafy.jangan_backend.edge.dto.response;

import com.ssafy.jangan_backend.edge.entity.Edge;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseEdgeIdDto {
    private Integer edgeId;

    public static ResponseEdgeIdDto toDto (Edge newEdge) {
        return ResponseEdgeIdDto.builder()
                .edgeId(newEdge.getId())
                .build();
    }
}
