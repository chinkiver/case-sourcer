package com.lawfirm.caseledger.controller;

import com.lawfirm.caseledger.common.Result;
import com.lawfirm.caseledger.dto.ClientRequest;
import com.lawfirm.caseledger.entity.Client;
import com.lawfirm.caseledger.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public Result<List<Client>> list(@RequestParam(required = false) Integer type) {
        return Result.success(clientService.listAll(type));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('client:add')")
    public Result<Client> create(@Valid @RequestBody ClientRequest request) {
        return Result.success(clientService.createClient(request));
    }
}