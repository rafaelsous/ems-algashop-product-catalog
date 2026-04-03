package com.rafaelsousa.algashop.product.catalog.domain.model.product;

import com.rafaelsousa.algashop.product.catalog.domain.model.DomainException;
import com.rafaelsousa.algashop.product.catalog.domain.model.IdGenerator;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "products")
@CompoundIndex(name = "pidx_product_by_category_enabledTrue_salePrice",
        def = "{'category.id': 1, 'salePrice': 1}",
        partialFilter = "{'enabled': true}")
@CompoundIndex(name = "pidx_product_by_category_enabledTrue_createdAt",
        def = "{'category.id': 1, 'createdAt': -1}",
        partialFilter = "{'enabled': true}")
public class Product extends AbstractAggregateRoot<Product> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @TextIndexed(weight = 1)
    private String name;

    @Indexed(name = "idx_product_by_brand")
    private String brand;

    @Setter
    @TextIndexed(weight = 5)
    private String description;

    private Integer quantityInStock;
    private Boolean enabled;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;

    @Version
    private Long version;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    private ProductCategory category;

    private Integer discountPercentageRounded;

    @TextScore
    private Float score;

	private Image mainImage;

	private Set<Image> images = new HashSet<>();

    @Builder
    public Product(String name, String brand, String description, Boolean enabled,
                   BigDecimal regularPrice, BigDecimal salePrice, Category category) {
        this.setId(IdGenerator.generateTimeBasedUUID());
        
        this.setName(name);
        this.setBrand(brand);
        this.setDescription(description);
        this.setEnabled(enabled);
        this.setRegularPrice(regularPrice);
        this.setSalePrice(salePrice);
        this.setQuantityInStock(0);
        this.setCategory(category);

        super.registerEvent(ProductAddedEvent.builder().productId(this.id).build());
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public void setBrand(String brand) {
        if (StringUtils.isBlank(brand)) {
            throw new IllegalArgumentException();
        }

        this.brand = brand;
    }

    public void setEnabled(Boolean enabled) {
        Objects.requireNonNull(enabled);

        Boolean wasEnabled = this.enabled;
        this.enabled = enabled;

        if (wasEnabled != null && !wasEnabled && this.getEnabled()) {
            this.registerEvent(ProductListedEvent.builder().productId(this.id).build());
        } else if (wasEnabled != null && wasEnabled && !this.getEnabled()) {
            this.registerEvent(ProductDelistedEvent.builder().productId(this.id).build());
        }
    }

    public void setCategory(Category category) {
        Objects.requireNonNull(category);

        this.category = ProductCategory.of(category);
    }

    public void disable() {
        this.setEnabled(false);
    }

    public void enable() {
        this.setEnabled(true);
    }

    public boolean isInStock() {
        return Objects.nonNull(quantityInStock) && quantityInStock > 0;
    }

    public boolean getHasDiscount() {
        return getDiscountPercentageRounded() != null && getDiscountPercentageRounded() > 0;
    }

	public Set<Image> getImages() {
		return Collections.unmodifiableSet(this.images);
	}

	public Optional<Image> getImage(UUID imageId) {
		Objects.requireNonNull(imageId);

		return this.images.stream().filter(image -> image.getId().equals(imageId)).findFirst();
	}

	public UUID addImage(String imageName) {
		Objects.requireNonNull(imageName);

		Image image = new Image(imageName);
		this.images.add(image);

		if (this.mainImage == null) {
			this.setMainImage(image);
		}

		return image.getId();
	}

	public void changeMainImage(UUID imageId) {
		Objects.requireNonNull(imageId);

		Image image = findImageByIdOrThrowException(imageId);

		setMainImage(image);
	}

	public void  removeImage(UUID imageId) {
		Objects.requireNonNull(imageId);

		Image image = findImageByIdOrThrowException(imageId);

		this.images.remove(image);

		if (image.equals(this.mainImage)) {
			this.setMainImage(this.images.stream().findFirst().orElse(null));
		}
	}

	public void changePrice(BigDecimal regularPrice, BigDecimal salePrice) {
        Objects.requireNonNull(regularPrice);
        Objects.requireNonNull(salePrice);

        BigDecimal oldRegularPrice = this.regularPrice;
        BigDecimal oldSalePrice = this.salePrice;

        boolean wasOnSale = getHasDiscount();

        if (this.salePrice.compareTo(regularPrice) > 0) {
            throw new DomainException("Sale price cannot be greater than regular price");
        }

        setRegularPrice(regularPrice);
        setSalePrice(salePrice);

        if (pricesDidNotChange(oldRegularPrice, oldSalePrice)) return;

        registerPriceChangedEvent(oldRegularPrice, oldSalePrice);

        if (isNewlyOnSale(wasOnSale)) {
            registerProductPlacedOnSale();
        }
    }

    private void setId(UUID id) {
        Objects.requireNonNull(id);

        this.id = id;
    }

    private void setRegularPrice(BigDecimal regularPrice) {
        Objects.requireNonNull(regularPrice);

        if (regularPrice.signum() == -1) {
            throw new IllegalArgumentException();
        }

        this.regularPrice = regularPrice;
        this.calculateDiscountPercentageRounded();
    }

    private void setSalePrice(BigDecimal salePrice) {
        Objects.requireNonNull(salePrice);

        if (salePrice.signum() == -1) {
            throw new IllegalArgumentException();
        }

        this.salePrice = salePrice;
        this.calculateDiscountPercentageRounded();
    }

    private void setQuantityInStock(Integer quantityInStock) {
        Objects.requireNonNull(quantityInStock);

        if (quantityInStock < 0) {
            throw new IllegalArgumentException();
        }

        this.quantityInStock = quantityInStock;
    }

	private void setMainImage(Image mainImage) {
		this.mainImage = mainImage;
	}

    private void calculateDiscountPercentageRounded() {
        if (regularPrice == null || salePrice == null || regularPrice.signum() == 0) {
            discountPercentageRounded = 0;
            return;
        }

        discountPercentageRounded = BigDecimal.ONE
                .subtract(salePrice.divide(regularPrice, 4, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .intValue();
    }

    private boolean pricesDidNotChange(BigDecimal oldRegularPrice, BigDecimal oldSalePrice) {
        return Objects.equals(this.regularPrice, oldRegularPrice)
                && Objects.equals(this.salePrice, oldSalePrice);
    }

    private void registerPriceChangedEvent(BigDecimal oldRegularPrice, BigDecimal oldSalePrice) {
        super.registerEvent(
                ProductPriceChangedEvent.builder()
                        .productId(this.id)
                        .newSalePrice(this.salePrice)
                        .newRegularPrice(this.regularPrice)
                        .oldSalePrice(oldSalePrice)
                        .oldRegularPrice(oldRegularPrice)
                    .build()
        );
    }

    private boolean isNewlyOnSale(boolean wasOnSale) {
        return getHasDiscount() && !wasOnSale;
    }

    private void registerProductPlacedOnSale() {
        super.registerEvent(
                ProductPlacedOnSaleEvent.builder()
                        .productId(this.id)
                        .regularPrice(this.regularPrice)
                        .salePrice(this.salePrice)
                    .build()
        );
    }

	private Image findImageByIdOrThrowException(UUID imageId) {
		return getImage(imageId)
				.orElseThrow(() -> new DomainException(
						String.format("Image of id %s was not found on product %s", imageId, this.id)));
	}
}