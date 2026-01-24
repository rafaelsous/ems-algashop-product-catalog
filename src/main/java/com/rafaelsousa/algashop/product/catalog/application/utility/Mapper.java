package com.rafaelsousa.algashop.product.catalog.application.utility;

public interface Mapper {
    <T> T convert(Object source, Class<T> destinationType);
}