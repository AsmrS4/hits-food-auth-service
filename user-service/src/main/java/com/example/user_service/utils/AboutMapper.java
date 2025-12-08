package com.example.user_service.utils;

import com.example.user_service.domain.dto.about.AboutDTO;
import com.example.user_service.domain.dto.about.EditAbout;
import com.example.user_service.domain.entities.About;
import org.springframework.stereotype.Component;

@Component
public class AboutMapper {
    public AboutDTO map(About about) {
        AboutDTO aboutDTO = new AboutDTO();
        aboutDTO.setId(about.getId());
        aboutDTO.setContactEmail(about.getContactEmail());
        aboutDTO.setCompanyName(about.getCompanyName());
        aboutDTO.setMailAddress(about.getMailAddress());
        aboutDTO.setManagerPhone(about.getManagerPhone());
        aboutDTO.setOperatorPhone(about.getOperatorPhone());
        return aboutDTO;
    }
    public About map(About about, EditAbout editAbout) {
        about.setCompanyName(editAbout.getCompanyName());
        about.setContactEmail(editAbout.getContactEmail());
        about.setMailAddress(editAbout.getMailAddress());
        about.setManagerPhone(editAbout.getManagerPhone());
        about.setOperatorPhone(editAbout.getOperatorPhone());
        return about;
    }


}
