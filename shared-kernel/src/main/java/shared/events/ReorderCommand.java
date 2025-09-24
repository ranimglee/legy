package shared.events;


import java.util.List;

/**
 * Command to re-populate a user’s cart from a past order.
 */
public record ReorderCommand(
        String userId,
        List<Item> items
) {
    /**
     * A single product + quantity from the original order.
     * As a record, Jackson can automatically deserialize it.
     */
    public record Item(
            String productId,
            int quantity
    ) {
    }
}
