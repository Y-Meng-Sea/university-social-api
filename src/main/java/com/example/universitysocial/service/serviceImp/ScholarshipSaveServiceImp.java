package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.dto.ScholarshipResponse;
import com.example.universitysocial.entity.Scholarship;
import com.example.universitysocial.entity.ScholarshipSave;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.ScholarshipRepository;
import com.example.universitysocial.repository.ScholarshipSaveRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.NotificationService;
import com.example.universitysocial.service.ScholarshipSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarshipSaveServiceImp implements ScholarshipSaveService {

    private final ScholarshipSaveRepository scholarshipSaveRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void saveScholarship(Long scholarshipId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        if (scholarshipSaveRepository.existsByUserAndScholarship(user, scholarship)) {
            throw new RuntimeException("Scholarship already saved");
        }

        ScholarshipSave scholarshipSave = ScholarshipSave.builder()
                .user(user)
                .scholarship(scholarship)
                .build();

        scholarshipSaveRepository.save(scholarshipSave);

        // Create notification if user is not saving their own scholarship
        if (!scholarship.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                    user.getId(),
                    scholarship.getAuthor().getId(),
                    com.example.universitysocial.entity.Notification.NotificationType.SCHOLARSHIP_SAVE,
                    null,
                    scholarship.getId(),
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " saved your scholarship",
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " saved your scholarship: " + scholarship.getTitle()
            );
        }
    }

    @Override
    @Transactional
    public void unsaveScholarship(Long scholarshipId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        ScholarshipSave scholarshipSave = scholarshipSaveRepository.findByUserAndScholarship(user, scholarship)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship save not found"));

        scholarshipSaveRepository.delete(scholarshipSave);
    }

    @Override
    public List<ScholarshipResponse> getSavedScholarships(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ScholarshipSave> saves = scholarshipSaveRepository.findByUser(user);
        return saves.stream()
                .map(save -> mapToScholarshipResponse(save.getScholarship(), user))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isScholarshipSaved(Long scholarshipId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        return scholarshipSaveRepository.existsByUserAndScholarship(user, scholarship);
    }

    private ScholarshipResponse mapToScholarshipResponse(Scholarship scholarship, User currentUser) {
        boolean isSaved = scholarshipSaveRepository.existsByUserAndScholarship(currentUser, scholarship);

        return ScholarshipResponse.builder()
                .id(scholarship.getId())
                .title(scholarship.getTitle())
                .description(scholarship.getDescription())
                .imageUrl(scholarship.getImageUrl())
                .link(scholarship.getLink())
                .authorId(scholarship.getAuthor().getId())
                .authorUsername(scholarship.getAuthor().getUsername())
                .authorNickName(scholarship.getAuthor().getNickName())
                .authorProfileImage(scholarship.getAuthor().getProfileImage())
                .savesCount((long) scholarship.getSaves().size())
                .isSaved(isSaved)
                .createdAt(scholarship.getCreatedAt())
                .updatedAt(scholarship.getUpdatedAt())
                .build();
    }
}
