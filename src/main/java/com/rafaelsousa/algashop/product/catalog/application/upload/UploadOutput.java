package com.rafaelsousa.algashop.product.catalog.application.upload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Builder
@Getter @Setter
public class UploadOutput {
	private String remoteFileName;
	private Long contentLength;
	private String contentType;
	private String uploadSignedUrl;
	private OffsetDateTime expiresAt;
}