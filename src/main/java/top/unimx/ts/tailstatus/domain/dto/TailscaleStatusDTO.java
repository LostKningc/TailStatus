package top.unimx.ts.tailstatus.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TailscaleStatusDTO(
        @JsonProperty("Peer") Map<String, PeerDetailDTO> peers, // 注意：JSON里是 Map 结构
        @JsonProperty("Health") List<String> healthWarnings
) {}
