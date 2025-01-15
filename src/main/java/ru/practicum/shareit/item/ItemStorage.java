package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Long itemId, Item item);

    Optional<Item> getItem(Long itemId);

    Collection<Item> getItemsByOwner(Long ownerId);

    Collection<Item> searchItems(String text);
}
