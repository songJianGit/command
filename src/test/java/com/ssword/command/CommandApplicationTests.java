package com.ssword.command;

import com.ssword.command.utils.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommandApplicationTests {

    @Test
    void contextLoads() {
        JacksonUtil.getJsonNode("{\"model\":\"A\",\"fileurl\":\"/opt/tcpdumpInfo/packinfo_18081.pcap\"}");
    }

}
