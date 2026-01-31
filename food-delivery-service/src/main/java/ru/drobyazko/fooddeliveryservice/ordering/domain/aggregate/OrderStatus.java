package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import ru.drobyazko.fooddeliveryservice.exceptions.MappingNotFoundException;

import java.util.HashMap;
import java.util.Map;

// needs constant synchronization with database (see order_status table)
public enum OrderStatus {
    CREATED(1L),
    PREPARED(2L);

    private static final Map<Long, OrderStatus> idToOrderStatusMap = new HashMap<>();

    static {
        for (OrderStatus orderStatus : values()) {
            idToOrderStatusMap.put(orderStatus.id, orderStatus);
        }
    }

    private final Long id;

    OrderStatus(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static OrderStatus valueFromId(Long id) {
        if (!idToOrderStatusMap.containsKey(id)) {
            throw new MappingNotFoundException();
        }
        return idToOrderStatusMap.get(id);
    }
}
