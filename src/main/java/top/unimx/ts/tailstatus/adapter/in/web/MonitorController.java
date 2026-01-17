package top.unimx.ts.tailstatus.adapter.in.web;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.unimx.ts.tailstatus.adapter.out.TailscaleClient;
import top.unimx.ts.tailstatus.domain.vo.PeerMonitorVO;
import top.unimx.ts.tailstatus.service.DataTransformService;

import java.util.List;

@RestController
@RequestMapping("/api")
// 允许跨域，方便你在本地开发调试 (生产环境通常由 Nginx 反代解决，但加这个不影响)
@CrossOrigin(origins = "*")
public class MonitorController {

    private final TailscaleClient tailscaleClient;
    private final DataTransformService dataTransformService;

    // 构造器注入 (GraalVM 推荐方式)
    public MonitorController(TailscaleClient tailscaleClient,
                             DataTransformService dataTransformService) {
        this.tailscaleClient = tailscaleClient;
        this.dataTransformService = dataTransformService;
    }

    /**
     * 前端轮询接口
     * GET /api/status
     */
    @GetMapping("/status")
    public Mono<List<PeerMonitorVO>> getMonitorStatus() {
        return tailscaleClient.getStatus()
                // 响应式流处理：拿到原始数据 -> 转换成 VO List
                .map(dataTransformService::transform);
    }
}
