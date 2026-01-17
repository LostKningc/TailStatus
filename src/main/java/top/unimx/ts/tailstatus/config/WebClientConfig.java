package top.unimx.ts.tailstatus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.unix.DomainSocketAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.LoopResources;


@Configuration
public class WebClientConfig {

    private static final String TAILSCALE_SOCKET_PATH = "/run/tailscale/tailscaled.sock";

    @Bean
    public HttpClient tailscaleHttpClient() {
        // 1. 启动前自检
        if (!Epoll.isAvailable()) {
            throw new IllegalStateException("Epoll Error");
        }
        //System.out.println("DEBUG: CHANGE APPLIED");

        // 2. 创建 HttpClient (不经过 WebClient 封装)
        return HttpClient.create()
                .protocol(HttpProtocol.HTTP11)
                .httpResponseDecoder(spec -> spec.validateHeaders(false))
                // 开启 Epoll 线程组
                .wiretap(true)
                .followRedirect(false)
                .runOn(LoopResources.create("netty-epoll", 1, true))
                // 【核心】设置远程地址为 Socket 文件
                // 因为我们后续只会发相对路径请求（无 Host），Netty 会直接使用这个地址
                // 并自动切换到底层的 EpollDomainSocketChannel
                .remoteAddress(() -> new DomainSocketAddress(TAILSCALE_SOCKET_PATH));
    }

    // 顺便把 ObjectMapper 暴露出来，后面解析 JSON 用
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
