package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId) throws ValidationException;

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getItemsByOwner(Long ownerId);

    Collection<ItemDto> searchItems(String text);
}
