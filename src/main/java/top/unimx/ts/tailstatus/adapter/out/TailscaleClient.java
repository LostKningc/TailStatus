package top.unimx.ts.tailstatus.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import top.unimx.ts.tailstatus.domain.dto.TailscaleStatusDTO;

import reactor.netty.http.client.HttpClient;
import java.util.Collections;

@Component
public class TailscaleClient {

    private static final Logger log = LoggerFactory.getLogger(TailscaleClient.class);

    private HttpClient tailscaleHttpClient; // 注入刚才定义的 HttpClient

    private ObjectMapper objectMapper; // 注入 Jackson 解析器

    public TailscaleClient(HttpClient tailscaleHttpClient, ObjectMapper objectMapper) {
        this.tailscaleHttpClient = tailscaleHttpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取 Tailscale 状态
     * 路径：http://localhost/localapi/v0/status
     * @return Mono 包装的 TailscaleStatusDTO 对象
     */
    public Mono<TailscaleStatusDTO> getStatus() {
        return tailscaleHttpClient
                // 欺骗 Tailscale 服务端
                .headers(h -> h.add("Host", "local-tailscaled.sock"))
                // 【绝对核心】这里只写相对路径！不要写 http://...
                // 因为没有 Host，Netty 不会走 TCP，直接走我们配置的 Unix Socket
                .get()
                .uri("/localapi/v0/status")
                .responseSingle((response, byteBufMono) -> {
                    // 检查状态码
                    if (response.status().code() != 200) {
                        return Mono.error(new RuntimeException("Tailscale return error: " + response.status()));
                    }
                    // 聚合响应体为字符串
                    return byteBufMono.asString();
                })
                // 手动反序列化 JSON
                .flatMap(json -> {
                    try {
                        return Mono.just(objectMapper.readValue(json, TailscaleStatusDTO.class));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                // 错误处理
                .onErrorResume(ex -> {
                    log.error("error fetching Tailscale status", ex);
                    return Mono.just(emptyStatus()); // 返回空对象或 null
                });
    }

    /**
     * 创建一个空的安全对象，防止空指针异常
     */
    private TailscaleStatusDTO emptyStatus() {
        return new TailscaleStatusDTO(Collections.emptyMap(), Collections.emptyList());
    }
}
