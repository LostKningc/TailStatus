package top.unimx.ts.tailstatus.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record PeerDetailDTO(
        @JsonProperty("HostName") String hostName,
        @JsonProperty("OS") String os,
        @JsonProperty("TailscaleIPs") List<String> ips,
        @JsonProperty("Online") boolean online,
        @JsonProperty("LastHandshake") String lastHandshake, // ISO8601 时间字符串
        @JsonProperty("Relay") String relay,
        @JsonProperty("RxBytes") long rxBytes,
        @JsonProperty("TxBytes") long txBytes
) {}
