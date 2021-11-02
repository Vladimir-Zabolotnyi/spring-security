package zabolotnyi.springsecurity.accountancy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;
import zabolotnyi.springsecurity.OrderDetails;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8080)
public class AccountancyServiceTest {

    @Autowired
    AccountancyService accountancyService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldGetMoney() throws JsonProcessingException {

        stubFor(get("/api/v1/orders/get/1").withHeader("Content-Type", new ContainsPattern(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse().withBody(objectMapper.writeValueAsString(new OrderDetails(1L,"test",1,1,1))).withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        // when
        Integer money = accountancyService.moneyToPay(1L);

        assertThat(money).isEqualTo(1);
    }
}
