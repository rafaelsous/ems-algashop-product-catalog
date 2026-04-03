package com.rafaelsousa.algashop.product.catalog.application.product.management;

import com.rafaelsousa.algashop.product.catalog.application.product.query.ImageOutput;
import com.rafaelsousa.algashop.product.catalog.application.storage.StorageProvider;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Image;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageManagementApplicationService {
	private final Mapper mapper;
	private final StorageProvider storageProvider;
	private final ProductRepository productRepository;

	public ImageOutput create(UUID productId, ImageInput imageInput) {
		Objects.requireNonNull(productId);
		Objects.requireNonNull(imageInput);

		Product product = findByIdOrFail(productId);

		if (!storageProvider.fileExists(imageInput.getRemoteFileName())) {
			throw new DomainException(String.format("Image %s was not found on storage provider"
					, imageInput.getRemoteFileName()));
		}

		if (productRepository.existsByImagesName(imageInput.getRemoteFileName())) {
			throw new DomainException(String.format("Image %s is already in use by another product"
					, imageInput.getRemoteFileName()));
		}

		UUID imageId = product.addImage(imageInput.getRemoteFileName());
		productRepository.save(product);

		Image image = product.getImage(imageId).orElseThrow();

		return mapper.convert(image, ImageOutput.class);
	}

	public void delete(UUID productId, UUID imageId) {
		Objects.requireNonNull(productId);
		Objects.requireNonNull(imageId);

		Product product = findByIdOrFail(productId);
		Image image = findImageOrFail(imageId, product);

		product.removeImage(imageId);
		storageProvider.deleteFile(image.getName());

		productRepository.save(product);
	}

	public void primary(UUID productId, UUID imageId) {
		Objects.requireNonNull(productId);
		Objects.requireNonNull(imageId);

		Product product = findByIdOrFail(productId);
		product.changeMainImage(imageId);

		productRepository.save(product);
	}

	private Product findByIdOrFail(UUID productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));
	}

	private Image findImageOrFail(UUID imageId, Product product) {
		return product.getImage(imageId)
				.orElseThrow(() -> new DomainException(String.format("Image of id %s was not found on product %s"
						, imageId, product.getId())));
	}
}