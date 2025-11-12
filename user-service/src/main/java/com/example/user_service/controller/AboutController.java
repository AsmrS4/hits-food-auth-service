package com.example.user_service.controller;

import com.example.user_service.domain.dto.about.AboutDTO;
import com.example.user_service.domain.dto.about.EditAbout;
import com.example.user_service.services.interfaces.AboutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/about")
@Tag(name = "About controller")
public class AboutController {
    @Autowired
    private AboutService aboutService;
    @GetMapping()
    public ResponseEntity<AboutDTO> getAboutInfo()  {
        return ResponseEntity.ok(aboutService.getAboutInfo());
    }
    @PutMapping()
    public ResponseEntity<AboutDTO> editAbout(@RequestBody @Valid EditAbout editAbout) {
        return ResponseEntity.ok(aboutService.editAboutInfo(editAbout));
    }
}
