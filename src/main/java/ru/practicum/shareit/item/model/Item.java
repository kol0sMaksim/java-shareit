package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private Long owner;
    private String request;
}
