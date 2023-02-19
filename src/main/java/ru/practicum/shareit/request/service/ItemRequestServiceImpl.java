package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.security.facade.IAuthenticationFacade;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final ModelMapper mapper;

    private final IAuthenticationFacade authenticationFacade;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ModelMapper mapper, IAuthenticationFacade authenticationFacade) {
        this.itemRequestRepository = itemRequestRepository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestWithItemsResponseDto findById(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository
                .findById(itemRequestId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(ItemRequest.class, itemRequestId));

        return mapper.toItemRequestWithItemsResponseDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsResponseDto> findAllForCurrentUser() {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();

        return itemRequestRepository.findAllByRequestor_Id(currentUserDetails.getId())
                .stream()
                .map(mapper::toItemRequestWithItemsResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsResponseDto> findAllCreatedByOthers(CustomPageableParameters customPageableParameters) {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();
        Sort sortByCreatedDesc = Sort.by(new Sort.Order(Sort.Direction.DESC, "created"));

        List<ItemRequest> items = customPageableParameters.isCompleted()
                ? itemRequestRepository.findAllByRequestor_IdNot(currentUserDetails.getId(), customPageableParameters.toPageable(sortByCreatedDesc))
                : itemRequestRepository.findAllByRequestor_IdNot(currentUserDetails.getId(), sortByCreatedDesc
        );

        return items
                .stream()
                .map(mapper::toItemRequestWithItemsResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestResponseDto create(CreateItemRequestRequestDto createItemRequestRequestDto) {
        User currentUser = authenticationFacade.getCurrentUser();

        return mapper.toItemRequestResponseDto(
                itemRequestRepository.save(
                        mapper.toItemRequest(createItemRequestRequestDto, currentUser)
                )
        );
    }
}
