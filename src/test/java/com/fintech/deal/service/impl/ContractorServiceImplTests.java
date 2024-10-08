package com.fintech.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintech.deal.config.DealConfig;
import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealContractorRoleRepository;
import com.fintech.deal.service.ContractorOutboxService;
import com.fintech.deal.service.DealService;
import com.fintech.deal.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class})
class ContractorServiceImplTests {

    @Mock
    private ContractorRepository repository;

    @Mock
    private RoleService roleService;

    @Mock
    private DealContractorRoleRepository dealContractorRoleRepository;

    @Mock
    private DealService dealService;

    @Mock
    private ContractorOutboxService outboxService;

    @InjectMocks
    private ContractorServiceImpl contractorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        DealType dealType = new DealType("TYPE", "type", true);
        DealStatus dealStatus = new DealStatus("ACTIVE", "active", true);

        Deal deal = Deal.builder()
                .id(UUID.fromString("2ef58289-8732-41df-8066-ddec89b2464c"))
                .description("Test Deal")
                .agreementNumber("Agreement123")
                .agreementDate(LocalDate.of(2023, 1, 1))
                .availabilityDate(LocalDate.of(2023, 12, 31))
                .type(dealType)
                .status(dealStatus)
                .closeDt(LocalDateTime.of(2023, 6, 1, 10, 0))
                .isActive(true)
                .build();

        DealContractor contractor = new DealContractor();
        contractor.setId(UUID.fromString("2ef58289-8732-41df-8066-ddec89b2464c"));
        contractor.setMain(false);
        contractor.setDeal(deal);
    }

    @Test
    void testDeleteContractor() throws NotActiveException, JsonProcessingException {
        UUID contractorId = UUID.randomUUID();
        DealContractor contractor = new DealContractor();
        contractor.setId(contractorId);
        contractor.setActive(true);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(contractor));

        contractorService.deleteContractor(contractorId);

        verify(repository).save(contractor);
        assertFalse(contractor.isActive());
    }

    @Test
    void testDeleteContractorNotActive() {
        UUID contractorId = UUID.randomUUID();
        DealContractor contractor = new DealContractor();
        contractor.setId(contractorId);
        contractor.setActive(false);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(contractor));

        NotActiveException thrown = assertThrows(NotActiveException.class, () ->
                contractorService.deleteContractor(contractorId));
        assertEquals("Contractor is not active", thrown.getMessage());
    }

    @Test
    void testAddRole() {

        UUID contractorId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Deal deal = new Deal();
        deal.setId(UUID.randomUUID());

        DealContractor contractor = new DealContractor();
        contractor.setId(contractorId);
        contractor.setDeal(deal);

        ContractorRole role = new ContractorRole();
        role.setId(roleId.toString());

        DealContractorRole dealContractorRole = new DealContractorRole();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(contractor));
        when(roleService.findRoleById(any(String.class))).thenReturn(role);
        when(dealContractorRoleRepository.save(any(DealContractorRole.class))).thenReturn(dealContractorRole);
        when(dealContractorRoleRepository.findRolesByContractorId(any(UUID.class))).thenReturn(Collections.singletonList(role));

        ContractorDTO result = contractorService.addRole(contractorId, new RoleDTO(roleId.toString()));

        verify(dealContractorRoleRepository).save(any(DealContractorRole.class));
        assertNotNull(result);
        assertTrue(result.getRoles().contains(role));
    }

    @Test
    void testDeleteRoleByDealContractorRoleId() {
        UUID roleId = UUID.randomUUID();
        DealContractorRole role = new DealContractorRole();
        role.setId(roleId);
        when(dealContractorRoleRepository.findById(any(UUID.class))).thenReturn(Optional.of(role));

        contractorService.deleteRoleByDealContractorRoleId(roleId);

        verify(dealContractorRoleRepository).save(role);
        assertFalse(role.isActive());
    }

    @Test
    void testDeleteRoleByDealContractorRoleIdNotFound() {
        UUID roleId = UUID.randomUUID();
        when(dealContractorRoleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> contractorService.deleteRoleByDealContractorRoleId(roleId));
        assertEquals("Role of contractor with ID: " + roleId + " not found.", thrown.getMessage());
    }

    @Test
    void testUpdateContractorByReceivedMessageUpdatesContractor() {
        UUID dealId = UUID.randomUUID();
        Deal deal = new Deal();
        deal.setId(dealId);

        Map<String, String> contractorMap = new HashMap<>();
        UUID id = UUID.randomUUID();
        contractorMap.put("id", String.valueOf(id));
        contractorMap.put("name", "new contractor name");
        contractorMap.put("inn", "1234567890");
        contractorMap.put("timestamp", String.valueOf(Instant.now().toEpochMilli()));

        DealContractor existingContractor = new DealContractor();
        existingContractor.setId(id);
        existingContractor.setName("old contractor name");
        existingContractor.setInn("0987654321");
        existingContractor.setDeal(deal);
        existingContractor.setModifyDateFromContractorMicroservice(LocalDateTime.now().minusDays(1));

        when(repository.findAllByContractorId(String.valueOf(id)))
                .thenReturn(Collections.singletonList(existingContractor));
        when(dealService.getDealById(deal.getId())).thenReturn(deal);
        when(repository.save(any(DealContractor.class))).thenReturn(existingContractor);

        contractorService.updateContractorByReceivedMessage(contractorMap);

        assertEquals("new contractor name", existingContractor.getName());
        assertEquals("1234567890", existingContractor.getInn());

        LocalDateTime messageCreationDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Long.parseLong(contractorMap.get("timestamp"))),
                ZoneId.systemDefault());
        assertEquals(messageCreationDateTime, existingContractor.getModifyDateFromContractorMicroservice());

        verify(repository, times(1)).save(any(DealContractor.class));
    }

    @Test
    void testUpdateContractorByReceivedMessageWhenDateIsOlderNotUpdates() {
        UUID dealId = UUID.randomUUID();
        Deal deal = new Deal();
        deal.setId(dealId);

        Map<String, String> contractorMap = new HashMap<>();
        UUID id = UUID.randomUUID();
        contractorMap.put("id", String.valueOf(id));
        contractorMap.put("name", "new contractor name");
        contractorMap.put("inn", "1234567890");
        contractorMap.put("timestamp", String.valueOf(Instant.now().minusSeconds(3600).toEpochMilli()));

        DealContractor existingContractor = new DealContractor();
        existingContractor.setId(id);
        existingContractor.setName("old contractor name");
        existingContractor.setInn("0987654321");
        existingContractor.setDeal(deal);
        existingContractor.setModifyDateFromContractorMicroservice(LocalDateTime.now());

        when(repository.findAllByContractorId(String.valueOf(id)))
                .thenReturn(Collections.singletonList(existingContractor));
        when(dealService.getDealById(deal.getId())).thenReturn(deal);
        when(repository.save(any(DealContractor.class))).thenReturn(existingContractor);

        contractorService.updateContractorByReceivedMessage(contractorMap);

        assertEquals("old contractor name", existingContractor.getName());
        assertEquals("0987654321", existingContractor.getInn());

        verify(repository, times(0)).save(any(DealContractor.class));
    }

}
