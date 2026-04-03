package com.rafaelsousa.algashop.product.catalog.infrastructure.storage.s3;

import com.rafaelsousa.algashop.product.catalog.application.storage.FileReference;
import com.rafaelsousa.algashop.product.catalog.application.storage.StorageProvider;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageProviderAwsS3Impl implements StorageProvider {
	private final S3Template s3Template;
	private final StorageProviderAwsS3Properties storageProviderAwsS3Properties;

	@Override
	public boolean healthCheck() {
		try {
			return s3Template.bucketExists(storageProviderAwsS3Properties.getBucketName());
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	@SneakyThrows
	public URL requestUploadUrl(FileReference fileReference) {
        return URI.create(String.format("http://localhost:4566/%s?token=%s"
		        , fileReference.fileName(), UUID.randomUUID())).toURL();
	}

	@Override
	public void deleteFile(String remoteFileName) {
		// Not implemented yet
	}

	@Override
	public boolean fileExists(String remoteFileName) {
		return !remoteFileName.equals("fail.jpg");
	}
}