
package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.WishlistResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.Wishlist;
import com.shopsphere.enums.ProductStatus;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.exception.WishlistOperationException;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.repository.WishlistRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.WishlistService;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(UUID productId) {

        UUID userId = getLoggedInUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found"
                        ));

        if (product.getProductStatus() != ProductStatus.ACTIVE) {
            throw new WishlistOperationException(
                    "Product is not available"
            );
        }

        boolean alreadyExists =
                wishlistRepository
                        .existsByUserUserIdAndProductProductId(
                                userId,
                                productId
                        );

        if (alreadyExists) {
            throw new WishlistOperationException(
                    "Product is already in wishlist"
            );
        }

        Wishlist wishlist = new Wishlist();

        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setCreatedAt(LocalDateTime.now());

        Wishlist savedWishlist =
                wishlistRepository.save(wishlist);

        return mapToResponse(savedWishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getMyWishlist() {

        UUID userId = getLoggedInUserId();

        List<Wishlist> wishlistItems =
                wishlistRepository
                        .findByUserUserIdOrderByCreatedAtDesc(
                                userId
                        );

        return wishlistItems.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductWishlisted(UUID productId) {

        UUID userId = getLoggedInUserId();

        return wishlistRepository
                .existsByUserUserIdAndProductProductId(
                        userId,
                        productId
                );
    }

    @Override
    @Transactional
    public void removeFromWishlist(UUID productId) {

        UUID userId = getLoggedInUserId();

        boolean exists =
                wishlistRepository
                        .existsByUserUserIdAndProductProductId(
                                userId,
                                productId
                        );

        if (!exists) {
            throw new WishlistOperationException(
                    "Product is not in wishlist"
            );
        }

        wishlistRepository
                .deleteByUserUserIdAndProductProductId(
                        userId,
                        productId
                );
    }

    private WishlistResponse mapToResponse(
            Wishlist wishlist) {

        Product product = wishlist.getProduct();

        return new WishlistResponse(
                wishlist.getWishlistId(),
                product.getProductId(),
                product.getProductName(),
                product.getProductDescription(),
                product.getProductPrice(),
                product.getProductStock(),
                product.getProductImageUrl(),
                wishlist.getCreatedAt()
        );
    }

    private UUID getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new WishlistOperationException(
                    "User is not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new WishlistOperationException(
                    "Invalid authenticated user"
            );
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) principal;

        return userDetails.getUserId();
    }
}
