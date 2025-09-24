package ordering.application.usecase.order;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class OrderTimeoutRegistry {
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

    public void register(String orderId, ScheduledFuture<?> task) {
        taskMap.put(orderId, task);
    }

    public void cancel(String orderId) {
        ScheduledFuture<?> task = taskMap.remove(orderId);
        if (task != null && !task.isDone()) {
            task.cancel(false);
        }
    }

    public boolean contains(String orderId) {
        return taskMap.containsKey(orderId);
    }
}
