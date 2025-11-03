package com.dev.FileSharingApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Generates getters, setters, toString, equals/hashCode
@NoArgsConstructor // No-argument constructor for JPA/Hibernate
@AllArgsConstructor // All-arguments constructor for convenience
public class SharedFile {
    @Id
    private String id;          // Unique identifier for the file (UUID + extension)
    private String origName;    // Original filename as uploaded by user
    private long uploadTime;    // Epoch milliseconds of upload
    private long expiryMillis;  // Expiry duration (in milliseconds)
    private String ownerId;     // Email or login of uploader (Google or GitHub)
}
