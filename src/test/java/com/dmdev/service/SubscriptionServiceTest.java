package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    /*@Mock
    private Clock clock;*/
    @InjectMocks
    private SubscriptionService subscriptionService;

    private static final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                createSubscriptionMapper,
                createSubscriptionValidator,
                clock
        );
    }

    @Test
    void shouldThrowExceptionIfSubscriptionDtoIsInvalid() {
        CreateSubscriptionDto subscriptionDto = getCreateSubscriptionDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "userId is invalid"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(subscriptionDto);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(subscriptionDto));
        verifyNoMoreInteractions(subscriptionDao, createSubscriptionMapper);
    }

    @Test
    void upsert() {
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscriptionDto();
        Subscription subscription = getSubscription();
        List<Subscription> subscriptionList = doReturn(Optional.of(subscription)).when(subscriptionDao).findByUserId(99);
        doReturn(subscription).when(subscription).getName().equals(createSubscriptionDto.getName());
    }

    private static CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Jane")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now())
                .build();
    }

    private static Subscription getSubscription() {
        return Subscription.builder()
                .id(99)
                .userId(99)
                .name("Jane")
                .provider(Provider.APPLE)
                .expirationDate(Instant.parse("2023-10-10T10:15:30.345Z"))
                .status(Status.ACTIVE)
                .build();
    }

    @Nested
    class TestCancel {
        private static final int SUB_ID = 1;

        @Test
        void whenNotFoundByIdShouldThrowException() {
            doReturn(Optional.empty()).when(subscriptionDao).findById(SUB_ID);

            assertThrows(IllegalArgumentException.class, () -> subscriptionService.cancel(SUB_ID));
            verify(subscriptionDao).findById(SUB_ID);
            verifyNoMoreInteractions(subscriptionDao);
        }

        @Test
        void whenStatusNotActive_Cancelled_ShouldThrowException() {
            var subscription = Subscription.builder().status(Status.CANCELED).build();
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(SUB_ID);

            var exception = assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(SUB_ID));
            assertEquals("Only active subscription 1 can be canceled", exception.getMessage());
        }

        @Test
        void whenStatusNotActive_Expired_ShouldThrowException() {
            var subscription = Subscription.builder().status(Status.EXPIRED).build();
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(SUB_ID);

            var exception = assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(SUB_ID));
            assertEquals("Only active subscription 1 can be canceled", exception.getMessage());
        }

        @Nested
        class WhenActive {
            private Subscription subscription;

            @BeforeEach
            void setUp() {
                subscription = mock(Subscription.class);
                when(subscriptionDao.findById(SUB_ID)).thenReturn(Optional.of(subscription));
                when(subscription.getStatus()).thenReturn(Status.ACTIVE);
            }

            @Test
            void shouldSetStatusCancelled() {
                subscriptionService.cancel(SUB_ID);
                verify(subscription).setStatus(Status.CANCELED);
            }


            @Test
            void shouldUpdateSubscription() {
                subscriptionService.cancel(SUB_ID);
                verify(subscriptionDao).update(subscription);
            }
        }
    }


    @Nested
    class TestExpire {
        private static final int SUB_ID = 1;

        @Test
        void whenNotFoundByIdShouldThrowException() {
            doReturn(Optional.empty()).when(subscriptionDao).findById(SUB_ID);

            assertThrows(IllegalArgumentException.class, () -> subscriptionService.expire(SUB_ID));
            verify(subscriptionDao).findById(SUB_ID);
            verifyNoMoreInteractions(subscriptionDao);
        }

        @Test
        void whenStatusExpiredShouldThrowException() {
            var subscription = Subscription.builder().status(Status.EXPIRED).build();
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(SUB_ID);

            var exception = assertThrows(SubscriptionException.class, () -> subscriptionService.expire(SUB_ID));
            assertEquals("Subscription 1 has already expired", exception.getMessage());
        }

        @Nested
        class WhenActive {
            private Subscription subscription;

            @BeforeEach
            void setUp() {
                subscription = mock(Subscription.class);
                when(subscriptionDao.findById(SUB_ID)).thenReturn(Optional.of(subscription));
                when(subscription.getStatus()).thenReturn(Status.ACTIVE);
            }

            @Test
            void shouldSetExpirationDate() {
                subscriptionService.expire(SUB_ID);

                verify(subscription).setExpirationDate(clock.instant());
            }

            @Test
            void shouldSetStatusExpired() {
                subscriptionService.expire(SUB_ID);

                verify(subscription).setStatus(Status.EXPIRED);
            }


            @Test
            void shouldUpdateSubscription() {
                subscriptionService.expire(SUB_ID);

                verify(subscriptionDao).update(subscription);
            }

        }

    }
}