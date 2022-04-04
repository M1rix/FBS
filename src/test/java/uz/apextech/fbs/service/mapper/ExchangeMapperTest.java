package uz.apextech.fbs.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExchangeMapperTest {

    private ExchangeMapper exchangeMapper;

    @BeforeEach
    public void setUp() {
        exchangeMapper = new ExchangeMapperImpl();
    }
}
