package EcommerceProject.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;                     // ID of the cart
    private Double totalPrice = 0.0;         // Total price of all cart items
    private List<CartItemDTO> items = new ArrayList<>();  // List of cart items
}
