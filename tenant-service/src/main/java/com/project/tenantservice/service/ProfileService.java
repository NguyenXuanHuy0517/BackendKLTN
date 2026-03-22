package com.project.tenantservice.service;

import com.project.datalayer.entity.User;
import com.project.datalayer.repository.UserRepository;
import com.project.tenantservice.dto.profile.ProfileResponseDTO;
import com.project.tenantservice.dto.profile.ProfileUpdateDTO;
import com.project.tenantservice.exception.ResourceNotFoundException;
import com.project.tenantservice.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    public ProfileResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));
        return profileMapper.toDTO(user);
    }

    public ProfileResponseDTO updateProfile(Long userId, ProfileUpdateDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getFcmToken() != null) {
            user.setFcmToken(request.getFcmToken());
        }

        userRepository.save(user);
        return profileMapper.toDTO(user);
    }
}