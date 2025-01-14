package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemStorage itemStorage;
    private UserService userService;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (userService.getUserById(userId) == null) {
            log.warn("Попытка добавления вещи пользователем с id: {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        if (itemDto.getAvailable() == null) {
            log.warn("Пользователь с id {}: поле 'available' обязательно для заполнения", userId);
            throw new ValidationException("Поле 'available' обязательно для заполнения");
        }

        Item item = new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(), userId,
                itemDto.getRequest());

        Item savedItem = itemStorage.addItem(item);
        log.info("Пользователь с id {} добавил новую вещь: {}", userId, savedItem);
        return new ItemMapper().toItemDto(savedItem);
    }

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item existingItem = itemStorage.getItem(itemId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id {}: обновление несуществующей вещи с id {}", userId, itemId);
                    return new NotFoundException("Вещь не найдена");
                });

        if (!existingItem.getOwner().equals(userId)) {
            log.warn("Пользователь с id {}: попытка обновления элемента с id {} без прав", userId, itemId);
            throw new ForbiddenException("Пользователь не владелец данной вещи, обновить её нельзя");
        }

        existingItem.setName(itemDto.getName());
        existingItem.setDescription(itemDto.getDescription());
        existingItem.setAvailable(itemDto.getAvailable());

        Item updatedItem = itemStorage.updateItem(itemId, existingItem);
        log.info("Пользователь с id {} обновил элемент с id {}: {}", userId, itemId, updatedItem);

        return new ItemMapper().toItemDto(updatedItem);
    }

    public ItemDto getItem(Long itemId) {
        Item item = itemStorage.getItem(itemId)
                .orElseThrow(() -> {
                    log.warn("Попытка получения несуществующей вещи с id {}", itemId);
                    return new NotFoundException("Вещь не найдена");
                });
        return new ItemMapper().toItemDto(item);
    }

    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        log.info("Получение списка вещей для юзера с id {}", ownerId);
        Collection<Item> items = itemStorage.getItemsByOwner(ownerId);
        return items.stream()
                .map(new ItemMapper()::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> searchItems(String text) {
        log.info("Поиск элементов по тексту: '{}'", text);
        Collection<Item> items = itemStorage.searchItems(text);
        log.info("Найдено {} элементов по запросу: '{}'", items.size(), text);
        return items.stream()
                .map(new ItemMapper()::toItemDto)
                .collect(Collectors.toList());
    }
}
