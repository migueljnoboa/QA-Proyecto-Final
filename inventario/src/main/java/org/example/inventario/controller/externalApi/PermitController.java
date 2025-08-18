package org.example.inventario.controller.externalApi;

import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.permit.PermitApi;
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
    public List<PermitApi> listPermit() {
        return PermitApi.from(permitService.findAll());
    }

}
