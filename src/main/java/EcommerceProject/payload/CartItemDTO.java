package EcommerceProject.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;      // ID of this cart item
    private ProductCartDTO product;    // Product details
    private Integer quantity;      // Quantity in cart
    private Double price; // original price of product
    private Double discount;       // Discount on this item
    private Double productPrice;   // Price per unit
}
