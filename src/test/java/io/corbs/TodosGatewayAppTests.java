package io.corbs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TodosGatewayAppTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Test
	public void testProxyPaths() throws IOException {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/ops/routes", String.class);
        Map routeMap = mapper.readValue(response.getBody(), Map.class);
        assertThat(routeMap.containsKey("/api/**")).isTrue();
        assertThat(routeMap.containsKey("/ops/**")).isTrue();
        assertThat(routeMap.containsKey("/**")).isTrue();
	}

}
