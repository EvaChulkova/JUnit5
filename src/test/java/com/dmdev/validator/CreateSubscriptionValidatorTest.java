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
                .expirationDate(Instant.now())
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserId() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(0)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now())
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
                .expirationDate(Instant.now())
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

}