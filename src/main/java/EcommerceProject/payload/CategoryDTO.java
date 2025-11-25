package EcommerceProject.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long categoryId;

    @NotBlank
    @Size(min=5,message = "Category name must consist at least 5 letter")
    private String categoryName;
}
