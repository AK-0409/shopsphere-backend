package com.shopsphere.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartResponse {

    private UUID cartId;
    private List<CartItemResponse> items;
    private BigDecimal cartTotal;

    public CartResponse() {
    }

    public CartResponse(
            UUID cartId,
            List<CartItemResponse> items,
            BigDecimal cartTotal) {

        this.cartId = cartId;
        this.items = items;
        this.cartTotal = cartTotal;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }

	@Override
	public String toString() {
		return "CartResponse [cartId=" + cartId + ", items=" + items + ", cartTotal=" + cartTotal + "]";
	}
    
    
}