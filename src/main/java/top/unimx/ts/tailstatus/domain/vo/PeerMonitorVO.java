package top.unimx.ts.tailstatus.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record PeerMonitorVO(
        String id,              // 唯一标识 (取 hostname 即可，或者截取 nodekey)
        String hostName,        // 机器名
        String os,              // 系统类型 (前端据此展示 Windows/Linux/Apple 图标)
        String ip,              // 只保留 IPv4，简洁
        @JsonProperty("is_online")
        boolean isOnline,       // 在线状态 (绿灯/红灯)
        String lastSeen,        // 转换成 "2 mins ago" 这种人类可读格式
        String trafficStats,    // 转换成 "1.2 GB" 这种格式
        String connectionType,  // 显示 "Direct" 或 "Relay (地点)"
        boolean hasWarning      // 这是一个衍生字段，如果 LastHandshake 太久远或者 Health 有报错
) implements Serializable {}
