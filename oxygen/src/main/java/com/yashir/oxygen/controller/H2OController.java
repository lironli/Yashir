package com.yashir.oxygen.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yashir.oxygen.service.H2OService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/h2o")
public class H2OController {
	
	private final H2OService h2oService;

    public H2OController(H2OService h2oService) {
        this.h2oService = h2oService;
    }

    @PostMapping("/hydrogen")
    @Operation(summary = "Add a hydrogen atom")
    public String addHydrogen() throws InterruptedException {
        h2oService.hydrogen();
        return "Hydrogen added";
    }

    @PostMapping("/oxygen")
    @Operation(summary = "Add an oxygen atom")
    public String addOxygen() throws InterruptedException {
        h2oService.oxygen();
        return "Oxygen added";
    }

    @GetMapping("/status")
    public String getStatus() {
        return String.format(
                "Molecules created: %d | H waiting: %d | O waiting: %d",
                h2oService.getWaterMoleculeCount(),
                h2oService.getHydrogenQueueSize(),
                h2oService.getOxygenQueueSize()
        );
    }
    
}
