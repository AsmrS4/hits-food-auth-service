package com.example.user_service.services.interfaces;

import com.example.user_service.domain.dto.about.AboutDTO;
import com.example.user_service.domain.dto.about.EditAbout;

import java.util.UUID;

public interface AboutService {
    AboutDTO getAboutInfo();
    AboutDTO editAboutInfo(EditAbout editAbout);
}
