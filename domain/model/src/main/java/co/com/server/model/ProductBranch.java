package co.com.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor

public class ProductBranch {

    private String branchId;

    private String branchName;

    private String productId;

    private String productName;

    private int stock;
}
