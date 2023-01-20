package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.security.IAuthenticationFacade;
import ru.practicum.shareit.user.model.User;

import javax.security.sasl.AuthenticationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    private final IAuthenticationFacade authenticationFacade;

    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, IAuthenticationFacade authenticationFacade) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public List<ItemResponseDto> findAll() {
        User currentUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        return StreamSupport
                .stream(itemRepository.findAllAvailableForUser(currentUser).spliterator(), false)
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> findByText(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        return StreamSupport
                .stream(
                        itemRepository.findAllAvailableByNameOrDescriptionContainingCaseInsensitive(text).spliterator(),
                        false
                )
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto findOne(Long itemId) throws AuthenticationException {
        return itemMapper.toItemResponseDto(findOneItemOrThrow(itemId));
    }

    @Override
    public ItemResponseDto create(CreateItemRequestDto createItemRequestDto) {
        User currentUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        return itemMapper.toItemResponseDto(itemRepository.save(itemMapper.toItem(createItemRequestDto, currentUser)));
    }

    @Override
    public ItemResponseDto update(Long itemId, UpdateItemRequestDto updateItemRequestDto) throws AuthenticationException {
        Item item = findOneItemOrThrow(itemId);
        checkItemOwnershipOrThrow(item);

        return itemMapper.toItemResponseDto(
                itemRepository.save(
                        itemMapper.toItem(itemId, updateItemRequestDto, item.getOwner())
                )
        );
    }

    @Override
    public void removeById(Long itemId) throws AuthenticationException {
        Item item = findOneItemOrThrow(itemId);
        checkItemOwnershipOrThrow(item);
        itemRepository.deleteById(itemId);
    }

    private Item findOneItemOrThrow(Long itemId) {
        Item item = itemRepository.findOne(itemId);

        if (item == null) {
            throw new EntityIsNotFoundException(Item.class, itemId);
        }

        return item;
    }

    private void checkItemOwnershipOrThrow(Item item) throws AuthenticationException {
        User currentUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        if (!item.getOwner().getId().equals(currentUser.getId())) {
            throw new AuthenticationException(
                    String.format(
                            "User with id '%d' tried to update item with id '%d'. Rejected.",
                            currentUser.getId(),
                            item.getOwner().getId()
                    )
            );
        }
    }
}
