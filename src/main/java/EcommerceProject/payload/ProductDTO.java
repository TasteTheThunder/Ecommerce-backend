package EcommerceProject.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank
    @Size(min=3,message = "Product name must consist at least 3 letter")
    private String productName;

    private String image;

    @NotBlank
    @Size(min=6,message = "Description must consist at least 6 letter")
    private String description;
    private Integer quantity;

    @NotNull
    private double price;
    private double discount;
    private double specialPrice;
}
