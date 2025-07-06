package org.example.inventario.controller;

import lombok.RequiredArgsConstructor;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.security.PermitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/permit")
@RequiredArgsConstructor
public class PermitController {
    private final PermitService permitService;
    @GetMapping("")
    public List<Permit> listPermit() {
        return permitService.findAll();
    }

}
