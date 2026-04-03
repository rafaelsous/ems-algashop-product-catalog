package com.rafaelsousa.algashop.product.catalog.application.product.query;

import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageQueryService {
	private final Mapper mapper;
	private final ProductRepository productRepository;

	public List<ImageOutput> getAllImages(UUID productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));

		return product.getImages().stream().map(image -> mapper.convert(image, ImageOutput.class)).toList();
	}

	public ImageOutput getImage(UUID productId, UUID imageId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));

		return mapper.convert(product.getImage(imageId), ImageOutput.class);
	}
}