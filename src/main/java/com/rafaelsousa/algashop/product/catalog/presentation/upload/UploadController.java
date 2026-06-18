package com.rafaelsousa.algashop.product.catalog.presentation.upload;

import com.rafaelsousa.algashop.product.catalog.application.upload.UploadInput;
import com.rafaelsousa.algashop.product.catalog.application.upload.UploadOutput;
import com.rafaelsousa.algashop.product.catalog.application.upload.management.UploadManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.infrastructure.security.check.SecurityAnnotations.CanWriteProducts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/upload-requests")
@RequiredArgsConstructor
public class UploadController {
	private final UploadManagementApplicationService uploadManagementApplicationService;

	@PostMapping
	@CanWriteProducts
	public UploadOutput requestUpload(@RequestBody @Valid UploadInput uploadInput) {
		return uploadManagementApplicationService.requestPreSignedUrl(uploadInput);
	}
}