package com.rafaelsousa.algashop.product.catalog.infrastructure.storage.s3;

import com.rafaelsousa.algashop.product.catalog.application.storage.FileReference;
import com.rafaelsousa.algashop.product.catalog.application.storage.StorageProvider;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@RequiredArgsConstructor
public class StorageProviderAwsS3Impl implements StorageProvider {
	private final S3Template s3Template;
	private final StorageProviderAwsS3Properties storageProviderAwsS3Properties;

	@Override
	public boolean healthCheck() {
		try {
			return s3Template.bucketExists(storageProviderAwsS3Properties.getBucketName());
		} catch (Exception _) {
			return false;
		}
	}

	@Override
	@SneakyThrows
	public URL requestUploadUrl(FileReference fileReference) {
		String bucketName = storageProviderAwsS3Properties.getBucketName();
		String key = fileReference.getFileName();

		if (fileExists(key)) {
			throw new StorageProviderException(String.format("Remote file %s already exists", key));
        }

		ObjectMetadata.Builder metadataBuilder = ObjectMetadata.builder();

		if (fileReference.isAllowPublicRead()) {
			metadataBuilder.acl("public-read");
		}

		try {
			return s3Template.createSignedPutURL(bucketName, key, fileReference.getExpiresIn(), metadataBuilder.build(),
					fileReference.getContentType().toString());
		} catch (S3Exception ex) {
            throw new StorageProviderException(String.format("Unknown error when tried to create presigned URL for file %s", key), ex);
		}
	}

	@Override
	public void deleteFile(String remoteFileName) {
		if (!fileExists(remoteFileName)) {
			throw new StorageProviderException(String.format("Remote file %s does not exist", remoteFileName));
		}

		try {
			s3Template.deleteObject(storageProviderAwsS3Properties.getBucketName(), remoteFileName);
		} catch (S3Exception ex) {
			throw new StorageProviderException(String.format("Unknown error when tried to delete file %s", remoteFileName), ex);
		}
	}

	@Override
	public boolean fileExists(String remoteFileName) {
		return s3Template.objectExists(storageProviderAwsS3Properties.getBucketName(), remoteFileName);
	}
}