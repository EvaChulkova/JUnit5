package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionServiceIT extends IntegrationTestBase {
    private SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();
    private SubscriptionService subscriptionService;

    private static final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());


    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                clock
        );
    }

    @Test
    void expire() {
        Subscription expiredSubscription = getExpiredSubscription(1);
        Subscription subscription = subscriptionDao.insert(getActiveSubscription(1));

        subscriptionService.expire(subscription.getId());
        subscription.setStatus(Status.EXPIRED);
        subscription.setExpirationDate(Instant.now(clock));
        Subscription actualResult = subscriptionDao.update(subscription);

        assertThat(actualResult.getStatus()).isEqualTo(expiredSubscription.getStatus());
    }


    @Test
    void cancel() {
        Subscription subscription = subscriptionDao.insert(getActiveSubscription(1));

        subscriptionService.cancel(subscription.getId());
        subscription.setStatus(Status.CANCELED);
        Subscription actualResult = subscriptionDao.update(subscription);

        assertThat(actualResult.getStatus()).isEqualTo(subscription.getStatus());
    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now())
                .build();
    }

    private Subscription getExpiredSubscription(Integer userId) {
        return Subscription.builder()
                .userId(userId)
                .name("Jane")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now())
                .status(Status.EXPIRED)
                .build();
    }

    private Subscription getActiveSubscription(Integer userId) {
        return Subscription.builder()
                .userId(userId)
                .name("Jane")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now())
                .status(Status.ACTIVE)
                .build();
    }

}