package com.rafaelsousa.algashop.product.catalog.application.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMediaTypeExtractor {

	public static MediaType fromFileName(String fileName) {
		String extension = FilenameUtils.getExtension(fileName);

		if (extension.equalsIgnoreCase("jpg")) {
			extension = "jpeg";
		}

		return MediaType.valueOf("image/" + extension.toLowerCase());
	}
}