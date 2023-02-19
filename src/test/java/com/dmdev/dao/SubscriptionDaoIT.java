package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionDaoIT extends IntegrationTestBase {
    private static final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription(1));
        Subscription subscription2 = subscriptionDao.insert(getSubscription(2));
        Subscription subscription3 = subscriptionDao.insert(getSubscription(3));

        List<Subscription> actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> subscriptionIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();

        assertThat(subscriptionIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void deleteExistingSubscription() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingSubscription() {
        boolean actualResult = subscriptionDao.delete(100500);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        Subscription subscription = getSubscription(1);
        subscriptionDao.insert(subscription);
        subscription.setName("new_name");

        subscriptionDao.update(subscription);


        Subscription updatedSubscription = subscriptionDao.findById(subscription.getId()).get();
        assertThat(updatedSubscription).isEqualTo(subscription);
    }

    @Test
    void insert() {
        Subscription subscription = getSubscription(1);
        Subscription actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1));

        List<Subscription> actualResult = subscriptionDao.findByUserId(subscription.getUserId());
        assertThat(actualResult).hasSize(1);

        List<Integer> subsByUserId = actualResult.stream()
                .map(Subscription::getUserId)
                .toList();

        assertThat(subsByUserId).contains(subscription.getUserId());
    }

    private Subscription getSubscription(Integer userId) {
        return Subscription.builder()
                .userId(userId)
                .name("Jane")
                .provider(Provider.APPLE)
                //.expirationDate(Instant.now())
                .expirationDate(Instant.ofEpochSecond(169999999))
                //.expirationDate(clock.instant())
                //.expirationDate(Instant.now(clock))
                .status(Status.ACTIVE)
                .build();
    }
}