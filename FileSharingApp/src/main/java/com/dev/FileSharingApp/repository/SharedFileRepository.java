package com.dev.FileSharingApp.repository;

import com.dev.FileSharingApp.model.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedFileRepository extends JpaRepository<SharedFile, String> {
    // Custom queries if needed
}
