package com.transport.api.assignment.service;

import com.transport.api.common.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDirectory;

    private FileStorageService fileStorageService;
    private UUID assignmentId;

    @BeforeEach
    void setUp() {

        fileStorageService =
                new FileStorageService(
                        tempDirectory.toString()
                );

        assignmentId = UUID.randomUUID();
    }

    @Test
    void shouldStoreValidPdfDocument() throws Exception {

        byte[] pdfContent = {
                0x25, 0x50, 0x44, 0x46, 0x2D,
                0x31, 0x2E, 0x37
        };

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "document.pdf",
                        "application/pdf",
                        pdfContent
                );

        String storedPath =
                fileStorageService.storeDocument(
                        assignmentId,
                        file
                );

        assertNotNull(storedPath);
        assertTrue(storedPath.endsWith(".pdf"));

        Path physicalFile =
                tempDirectory.resolve(storedPath);

        assertTrue(
                Files.exists(physicalFile)
        );

        assertArrayEquals(
                pdfContent,
                Files.readAllBytes(physicalFile)
        );
    }

    @Test
    void shouldRejectFakePdfDocument() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "fake.pdf",
                        "application/pdf",
                        "This is not a PDF".getBytes()
                );

        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileStorageService.storeDocument(
                                assignmentId,
                                file
                        )
                );

        assertEquals(
                "Only valid PDF documents are allowed",
                exception.getMessage()
        );
    }

    @Test
    void shouldStoreValidPngImage() throws Exception {

        byte[] pngContent = {
                (byte) 0x89,
                0x50,
                0x4E,
                0x47,
                0x0D,
                0x0A,
                0x1A,
                0x0A,
                0x00
        };

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.png",
                        "image/png",
                        pngContent
                );

        String storedPath =
                fileStorageService.storeImage(
                        assignmentId,
                        file
                );

        assertNotNull(storedPath);
        assertTrue(storedPath.endsWith(".png"));

        Path physicalFile =
                tempDirectory.resolve(storedPath);

        assertTrue(
                Files.exists(physicalFile)
        );

        assertArrayEquals(
                pngContent,
                Files.readAllBytes(physicalFile)
        );
    }

    @Test
    void shouldStoreValidJpegImage() throws Exception {

        byte[] jpegContent = {
                (byte) 0xFF,
                (byte) 0xD8,
                (byte) 0xFF,
                (byte) 0xE0,
                0x00
        };

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.jpg",
                        "image/jpeg",
                        jpegContent
                );

        String storedPath =
                fileStorageService.storeImage(
                        assignmentId,
                        file
                );

        assertNotNull(storedPath);
        assertTrue(storedPath.endsWith(".jpg"));

        Path physicalFile =
                tempDirectory.resolve(storedPath);

        assertTrue(
                Files.exists(physicalFile)
        );

        assertArrayEquals(
                jpegContent,
                Files.readAllBytes(physicalFile)
        );
    }

    @Test
    void shouldRejectInvalidPngImage() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "fake.png",
                        "image/png",
                        "This is not a PNG".getBytes()
                );

        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileStorageService.storeImage(
                                assignmentId,
                                file
                        )
                );

        assertEquals(
                "Invalid PNG image",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnsupportedImageType() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.gif",
                        "image/gif",
                        new byte[]{0x47, 0x49, 0x46}
                );

        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileStorageService.storeImage(
                                assignmentId,
                                file
                        )
                );

        assertEquals(
                "Only PNG or JPG images are allowed",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectEmptyFile() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "empty.pdf",
                        "application/pdf",
                        new byte[0]
                );

        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileStorageService.storeDocument(
                                assignmentId,
                                file
                        )
                );

        assertEquals(
                "File must not be empty",
                exception.getMessage()
        );
    }
}