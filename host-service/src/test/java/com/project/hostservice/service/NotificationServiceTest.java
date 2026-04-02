package com.project.hostservice.service;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.Notification;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.NotificationRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.notification.NotificationResponseDTO;
import com.project.hostservice.mapper.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendToAllTenantsByHostSavesDistinctNotificationsInBatch() {
        User tenantOne = new User();
        tenantOne.setUserId(101L);

        User tenantTwo = new User();
        tenantTwo.setUserId(202L);

        Contract firstContract = new Contract();
        firstContract.setTenant(tenantOne);

        Contract duplicateTenantContract = new Contract();
        duplicateTenantContract.setTenant(tenantOne);

        Contract secondContract = new Contract();
        secondContract.setTenant(tenantTwo);

        when(contractRepository.findWithRelationsByHostIdAndStatus(1L, "ACTIVE"))
                .thenReturn(List.of(firstContract, duplicateTenantContract, secondContract));
        when(userRepository.findAllById(List.of(101L, 202L))).thenReturn(List.of(tenantOne, tenantTwo));

        notificationService.sendToAllTenantsByHost(1L, "HOST_ANNOUNCEMENT", "Title", "Body", "ROOM", 55L);

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(captor.capture());

        List<Notification> notifications = captor.getValue();
        assertEquals(2, notifications.size());
        assertEquals(List.of(101L, 202L), notifications.stream().map(n -> n.getUser().getUserId()).toList());
        assertEquals(List.of("HOST_ANNOUNCEMENT", "HOST_ANNOUNCEMENT"),
                notifications.stream().map(Notification::getType).toList());
        assertEquals(List.of("Title", "Title"), notifications.stream().map(Notification::getTitle).toList());
        assertFalse(notifications.get(0).isRead());
        assertFalse(notifications.get(1).isRead());
    }

    @Test
    void getNotificationsPageByUserReturnsMappedPagedResponse() {
        Notification first = new Notification();
        first.setNotificationId(1L);
        Notification second = new Notification();
        second.setNotificationId(2L);

        Page<Notification> page = new PageImpl<>(
                List.of(first, second),
                PageRequest.of(1, 2),
                5
        );

        NotificationResponseDTO firstDto = new NotificationResponseDTO();
        firstDto.setNotificationId(1L);
        NotificationResponseDTO secondDto = new NotificationResponseDTO();
        secondDto.setNotificationId(2L);

        when(notificationRepository.findPageByUserId(eq(9L), eq(false), eq("rent"), any(Pageable.class)))
                .thenReturn(page);
        when(notificationMapper.toDTO(first)).thenReturn(firstDto);
        when(notificationMapper.toDTO(second)).thenReturn(secondDto);

        var response = notificationService.getNotificationsPageByUser(9L, false, " rent ", 1, 2, "createdAt,desc");

        assertEquals(2, response.getItems().size());
        assertSame(firstDto, response.getItems().get(0));
        assertSame(secondDto, response.getItems().get(1));
        assertEquals(5L, response.getTotalItems());
        assertEquals(1, response.getPage());
        assertEquals(2, response.getSize());
        assertTrue(response.isHasNext());
    }

    @Test
    void countUnreadByUserDelegatesToRepository() {
        when(notificationRepository.countByUser_UserIdAndIsReadFalse(9L)).thenReturn(6L);

        long unreadCount = notificationService.countUnreadByUser(9L);

        assertEquals(6L, unreadCount);
    }
}
