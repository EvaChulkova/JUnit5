package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper createSubscriptionMapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .build();

        Subscription actualResult = createSubscriptionMapper.map(subscriptionDto);

        Subscription expectedResult = Subscription.builder()
                .id(null)
                .userId(1)
                .name("Jane")
                .provider(Provider.APPLE)
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .status(Status.ACTIVE)
                .build();

        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }
}