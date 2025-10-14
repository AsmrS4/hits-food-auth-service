package com.example.demo.controllers;

import com.example.demo.dtos.Bin;
import com.example.demo.services.BinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bin")
@RequiredArgsConstructor
public class BinController {
    private final BinService binService;

    @GetMapping("/{clientId}")
    public Bin getClientBin(@PathVariable UUID clientId) {
        return binService.getClientBin(clientId);
    }

    @PostMapping("/{clientId}/add/{foodId}")
    public Bin addFood(@PathVariable UUID clientId, @PathVariable UUID foodId) {
        return binService.addFoodToBin(clientId, foodId);
    }

    @DeleteMapping("/{clientId}/remove/{foodId}")
    public Bin removeFood(@PathVariable UUID clientId, @PathVariable UUID foodId) {
        return binService.removeFoodFromBin(clientId, foodId);
    }
}


