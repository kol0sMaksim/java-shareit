package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private long currentMaxId;

    public Item addItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.debug("Добавлена новая вещь: {}", item);
        return item;
    }

    public Item updateItem(Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            log.warn("Попытка обновления несуществующей вещи с id: {}", itemId);
            throw new NotFoundException("Вещь не найдена");
        }
        item.setId(itemId);
        items.put(itemId, item);
        log.debug("Обновлена вещь с id: {}. Новые данные: {}", itemId, item);
        return item;
    }

    public Optional<Item> getItem(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> getItemsByOwner(Long ownerId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(ownerId)) {
                userItems.add(item);
            }
        }
        log.debug("Найдено {} вещей для владельца с id: {}", userItems.size(), ownerId);
        return userItems;
    }

    public List<Item> searchItems(String text) {
        List<Item> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (Boolean.TRUE.equals(item.getAvailable()) &&
                    ((item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                            (item.getDescription() != null &&
                                    item.getDescription().toLowerCase().contains(text.toLowerCase())))) {
                foundItems.add(item);
            }
        }
        log.debug("Поиск по тексту '{}', найдено {} вещей", text, foundItems.size());
        return foundItems;
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
