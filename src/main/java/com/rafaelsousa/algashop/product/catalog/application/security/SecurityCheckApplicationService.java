package com.rafaelsousa.algashop.product.catalog.application.security;

import java.util.UUID;

public interface SecurityCheckApplicationService {
	UUID getAuthenticatedUserId();
	boolean isAuthenticated();
	boolean isMachineAuthenticated();
}