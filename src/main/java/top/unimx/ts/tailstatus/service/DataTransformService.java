package top.unimx.ts.tailstatus.service;


import org.springframework.stereotype.Service;
import top.unimx.ts.tailstatus.domain.dto.PeerDetailDTO;
import top.unimx.ts.tailstatus.domain.dto.TailscaleStatusDTO;
import top.unimx.ts.tailstatus.domain.vo.PeerMonitorVO;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataTransformService {

    public List<PeerMonitorVO> transform(TailscaleStatusDTO rawData) {
        if (rawData == null || rawData.peers() == null) {
            return Collections.emptyList();
        }

        return rawData.peers().values().stream()
                .map(this::convertToVO)
                // 建议按在线状态和机器名排序：在线的排前面
                .sorted((a, b) -> {
                    if (a.isOnline() != b.isOnline()) return a.isOnline() ? -1 : 1;
                    return a.hostName().compareToIgnoreCase(b.hostName());
                })
                .collect(Collectors.toList());
    }

    private PeerMonitorVO convertToVO(PeerDetailDTO dto) {
        // 1. 提取 IPv4 (通常是列表第一个，或者包含 ".")
        String ipv4 = dto.ips().stream()
                .filter(ip -> ip.contains("."))
                .findFirst()
                .orElse("Unknown");

        // 2. 格式化流量
        String totalTraffic = formatBytes(dto.rxBytes() + dto.txBytes());

        // 3. 计算“上次活跃时间” (Human Readable)
        String timeAgo = calculateTimeAgo(dto.lastHandshake());

        // 4. 判断连接类型
        String connType = (dto.relay() == null || dto.relay().isEmpty())
                ? "Direct"
                : "Relay (" + dto.relay() + ")";

        return new PeerMonitorVO(
                dto.hostName(), // 用 HostName 做 ID 简单点，也可以透传 NodeKey
                dto.hostName(),
                dto.os(),
                ipv4,
                dto.online(),
                timeAgo,
                totalTraffic,
                connType,
                !dto.online() // 简单的告警逻辑：不在线就是 Warning
        );
    }

    // --- 辅助工具方法 ---

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String calculateTimeAgo(String isoTime) {
        try {
            ZonedDateTime handshakeTime = ZonedDateTime.parse(isoTime);
            Duration diff = Duration.between(handshakeTime, ZonedDateTime.now());

            long seconds = diff.getSeconds();
            if (seconds < 60) return "Just now";
            if (seconds < 3600) return (seconds / 60) + "m ago";
            if (seconds < 86400) return (seconds / 3600) + "h ago";
            return (seconds / 86400) + "d ago";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
