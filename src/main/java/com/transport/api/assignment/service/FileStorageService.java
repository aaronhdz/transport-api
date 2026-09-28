package com.transport.api.assignment.service;

import com.transport.api.common.exception.FileStorageException;
import com.transport.api.common.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path storageLocation;

    private static final byte[] PDF_SIGNATURE = {
            0x25, 0x50, 0x44, 0x46, 0x2D
    };

    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4E, 0x47,
            0x0D, 0x0A, 0x1A, 0x0A
    };

    private static final byte[] JPEG_SIGNATURE = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
    };

    public FileStorageService(
            @Value("${app.storage.location}") String storageLocation) {

        this.storageLocation = Path.of(storageLocation)
                .toAbsolutePath()
                .normalize();
    }

    public String storeDocument(
            UUID assignmentId,
            MultipartFile file) {

        validateNotEmpty(file);

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())
                || !hasSignature(file, PDF_SIGNATURE)) {

            log.warn(
                    "event=file_rejected type=document assignmentId={} reason=invalid_pdf",
                    assignmentId
            );

            throw new InvalidFileException(
                    "Only valid PDF documents are allowed"
            );
        }

        return store(
                assignmentId,
                file,
                "document",
                ".pdf"
        );
    }

    public String storeImage(
            UUID assignmentId,
            MultipartFile file) {

        validateNotEmpty(file);

        String contentType = file.getContentType();

        if ("image/png".equalsIgnoreCase(contentType)) {

            if (!hasSignature(file, PNG_SIGNATURE)) {

                log.warn(
                        "event=file_rejected type=image assignmentId={} reason=invalid_png",
                        assignmentId
                );

                throw new InvalidFileException(
                        "Invalid PNG image"
                );
            }

            return store(
                    assignmentId,
                    file,
                    "image",
                    ".png"
            );
        }

        if ("image/jpeg".equalsIgnoreCase(contentType)) {

            if (!hasSignature(file, JPEG_SIGNATURE)) {

                log.warn(
                        "event=file_rejected type=image assignmentId={} reason=invalid_jpg",
                        assignmentId
                );

                throw new InvalidFileException(
                        "Invalid JPG image"
                );
            }

            return store(
                    assignmentId,
                    file,
                    "image",
                    ".jpg"
            );
        }

        log.warn(
                "event=file_rejected type=image assignmentId={} reason=unsupported_content_type contentType={}",
                assignmentId,
                contentType
        );

        throw new InvalidFileException(
                "Only PNG or JPG images are allowed"
        );
    }

    private String store(
            UUID assignmentId,
            MultipartFile file,
            String type,
            String extension) {

        try {
            Path directory = storageLocation
                    .resolve("assignments")
                    .resolve(assignmentId.toString())
                    .resolve(type);

            Files.createDirectories(directory);

            String fileName =
                    UUID.randomUUID() + extension;

            Path target = directory
                    .resolve(fileName)
                    .normalize();

            if (!target.startsWith(storageLocation)) {

                log.warn(
                        "event=file_rejected type={} assignmentId={} reason=invalid_path",
                        type,
                        assignmentId
                );

                throw new InvalidFileException(
                        "Invalid file path"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String relativePath = storageLocation
                    .relativize(target)
                    .toString()
                    .replace("\\", "/");

            log.info(
                    "event=file_stored type={} assignmentId={} path={}",
                    type,
                    assignmentId,
                    relativePath
            );

            return relativePath;

        } catch (IOException exception) {

            log.error(
                    "event=file_storage_failed type={} assignmentId={} reason={}",
                    type,
                    assignmentId,
                    exception.getMessage()
            );

            throw new FileStorageException(
                    "Could not store file",
                    exception
            );
        }
    }

    private void validateNotEmpty(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            log.warn(
                    "event=file_rejected reason=empty_file"
            );

            throw new InvalidFileException(
                    "File must not be empty"
            );
        }
    }

    private boolean hasSignature(
            MultipartFile file,
            byte[] expectedSignature) {

        try {
            byte[] bytes = file.getBytes();

            if (bytes.length < expectedSignature.length) {
                return false;
            }

            for (int i = 0; i < expectedSignature.length; i++) {
                if (bytes[i] != expectedSignature[i]) {
                    return false;
                }
            }

            return true;

        } catch (IOException exception) {

            log.error(
                    "event=file_read_failed reason={}",
                    exception.getMessage()
            );

            throw new FileStorageException(
                    "Could not read file",
                    exception
            );
        }
    }
}