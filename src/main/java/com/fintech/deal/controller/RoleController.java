package com.fintech.deal.controller;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.service.ContractorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/contractor-to-role")
@RequiredArgsConstructor
public class RoleController {

    @NonNull
    private final ContractorService service;
    @PostMapping("/add/{id}")
    public ResponseEntity<ContractorDTO> addRole(@PathVariable UUID id, @RequestBody RoleDTO roleDTO) {
        try {
            return ResponseEntity.ok(service.addRole(id, roleDTO));
        } catch (NotActiveException e) {
            return ResponseEntity.notFound().build();
        }
    }

}