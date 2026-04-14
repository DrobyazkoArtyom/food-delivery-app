package ru.drobyazko.fooddeliveryservice.eventing.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.drobyazko.fooddeliveryservice.configuration.OutboxProperties;
import ru.drobyazko.fooddeliveryservice.eventing.infrastructure.EventEntity;
import ru.drobyazko.fooddeliveryservice.eventing.infrastructure.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class PollingOutboxService {
    private final OutboxProperties outboxProperties;
    private final EventRepository eventRepository;
    private final KafkaSenderService kafkaSenderService;
    private static final Logger logger = LoggerFactory.getLogger(PollingOutboxService.class);

    @Autowired
    public PollingOutboxService(OutboxProperties outboxProperties,
                                EventRepository eventRepository,
                                KafkaSenderService kafkaSenderService) {
        this.outboxProperties = outboxProperties;
        this.eventRepository = eventRepository;
        this.kafkaSenderService = kafkaSenderService;
    }

    @Scheduled(fixedDelayString = "${app.outbox.processing-delay-seconds}", timeUnit = TimeUnit.SECONDS)
    public void processOutboxEvents() {
        Page<EventEntity> eventEntities = eventRepository.findAllByOrderById(Pageable.ofSize(outboxProperties.batchSize()));
        List<CompletableFuture<Long>> eventFutures = new ArrayList<>();
        for (EventEntity eventEntity : eventEntities) {
            var future = kafkaSenderService.send(eventEntity).handle((result, ex) -> {
                if (ex != null) {
                    logger.error("Failed to send outbox with eventId={}", eventEntity.getId(), ex);
                    return null;
                }
                return eventEntity.getId();
            });
            eventFutures.add(future);
        }

        List<Long> successfulEventIds = new ArrayList<>();
        for (var eventFuture : eventFutures) {
            Long eventId = eventFuture.join();
            if (eventId != null) {
                successfulEventIds.add(eventId);
            }
        }
        eventRepository.deleteAllByIdInBatch(successfulEventIds);
    }
}
