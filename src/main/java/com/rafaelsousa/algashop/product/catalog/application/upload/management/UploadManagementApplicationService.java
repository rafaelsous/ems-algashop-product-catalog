package com.rafaelsousa.algashop.product.catalog.application.upload.management;

import com.rafaelsousa.algashop.product.catalog.application.storage.FileReference;
import com.rafaelsousa.algashop.product.catalog.application.storage.StorageProvider;
import com.rafaelsousa.algashop.product.catalog.application.upload.UploadInput;
import com.rafaelsousa.algashop.product.catalog.application.upload.UploadOutput;
import com.rafaelsousa.algashop.product.catalog.application.utility.ImageMediaTypeExtractor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadManagementApplicationService {
	private final StorageProvider storageProvider;

	public UploadOutput requestPreSignedUrl(UploadInput uploadInput) {
		MediaType mediaType = ImageMediaTypeExtractor.fromFileName(uploadInput.getOriginalFileName());

		if (!(mediaType.equals(MediaType.IMAGE_JPEG) || mediaType.equals(MediaType.IMAGE_PNG))) {
			throw new IllegalArgumentException("Invalid image type");
		}

		String extension = FilenameUtils.getExtension(uploadInput.getOriginalFileName());

		FileReference fileReference = FileReference.builder()
				.fileName(UUID.randomUUID() + "." + extension)
				.contentType(mediaType)
				.contentLength(uploadInput.getContentLength())
				.expiresIn(Duration.ofMinutes(5))
				.allowPublicRead(true)
				.build();

		URL preSignedUrl = storageProvider.requestUploadUrl(fileReference);
        OffsetDateTime expiresAt = OffsetDateTime.now().plus(fileReference.getExpiresIn());

	    return UploadOutput.builder()
	        .remoteFileName(fileReference.getFileName())
	        .contentLength(fileReference.getContentLength())
	        .contentType(fileReference.getContentType().toString())
	        .uploadSignedUrl(preSignedUrl.toString())
	        .expiresAt(expiresAt)
	        .build();
	}
}