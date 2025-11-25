package EcommerceProject.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCartDTO {
    private Long productId;
    private String productName;
    private String image;
    private String description;
}
