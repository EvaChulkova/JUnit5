package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionValidatorTest {
    private final CreateSubscriptionValidator createSubscriptionValidator = CreateSubscriptionValidator.getInstance();

    @Test
    void shouldPassValidationForSubscriber() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserId() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @Test
    void invalidName() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    void invalidProvider() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Jane")
                .provider("")
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
    }

    @Test
    void invalidExpirationDate() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2022-10-10T10:15:30.345Z"))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

}