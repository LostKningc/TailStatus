package top.unimx.ts.tailstatus;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import top.unimx.ts.tailstatus.domain.dto.PeerDetailDTO;
import top.unimx.ts.tailstatus.domain.dto.TailscaleStatusDTO;
import top.unimx.ts.tailstatus.domain.vo.PeerMonitorVO;

@SpringBootApplication
@RegisterReflectionForBinding({
        TailscaleStatusDTO.class,
        PeerDetailDTO.class,
        PeerMonitorVO.class,
        java.util.ArrayList.class,
        java.util.HashMap.class
})
public class TailscaleAdapterApplication {
    public static void main(String[] args) {
        SpringApplication.run(TailscaleAdapterApplication.class, args);
    }
}
