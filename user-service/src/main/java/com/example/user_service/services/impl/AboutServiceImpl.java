package com.example.user_service.services.impl;

import com.example.user_service.domain.dto.about.AboutDTO;
import com.example.user_service.domain.dto.about.EditAbout;
import com.example.user_service.domain.entities.About;
import com.example.user_service.repository.AboutRepository;
import com.example.user_service.services.interfaces.AboutService;
import com.example.user_service.utils.AboutMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {
    private final AboutRepository aboutRepository;
    private final AboutMapper mapper;
    @Override
    public AboutDTO getAboutInfo() {
        List<About> about = aboutRepository.findAll();
        if(about.isEmpty()) {
            About newAbout = new About();
            newAbout.setId(UUID.randomUUID());
            newAbout.setCompanyName("TSU HIT's");
            newAbout.setContactEmail("tsu@example.com");
            newAbout.setManagerPhone("88005553535");
            newAbout.setOperatorPhone("88005553535");
            newAbout.setMailAddress("Some address");
            return mapper.map(aboutRepository.save(newAbout));
        }

        return mapper.map(about.getFirst());
    }

    @Override
    public AboutDTO editAboutInfo(EditAbout editAbout) {
        About about = aboutRepository.findAll().getFirst();
        about = mapper.map(about, editAbout);
        return mapper.map(aboutRepository.save(about));
    }
}
