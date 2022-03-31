package uz.apextech.fbs.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.apextech.fbs.web.rest.TestUtil;

class ExchangeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exchange.class);
        Exchange exchange1 = new Exchange();
        exchange1.setId(1L);
        Exchange exchange2 = new Exchange();
        exchange2.setId(exchange1.getId());
        assertThat(exchange1).isEqualTo(exchange2);
        exchange2.setId(2L);
        assertThat(exchange1).isNotEqualTo(exchange2);
        exchange1.setId(null);
        assertThat(exchange1).isNotEqualTo(exchange2);
    }
}
