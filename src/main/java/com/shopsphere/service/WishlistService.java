
package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse addToWishlist(UUID productId);

    List<WishlistResponse> getMyWishlist();

    boolean isProductWishlisted(UUID productId);

    void removeFromWishlist(UUID productId);
}

