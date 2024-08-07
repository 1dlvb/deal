package com.fintech.deal.repository;

import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.DealContractorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link DealContractorRole} entities.
 * @author Matushkin Anton
 */
public interface DealContractorRoleRepository extends JpaRepository<DealContractorRole, UUID> {

    @Query("SELECT cr FROM DealContractorRole dcr " +
            "JOIN dcr.contractorRole cr " +
            "WHERE dcr.dealContractor.id = :contractorId AND dcr.isActive = true")
    List<ContractorRole> findRolesByContractorId(@Param("contractorId") UUID contractorId);

}
