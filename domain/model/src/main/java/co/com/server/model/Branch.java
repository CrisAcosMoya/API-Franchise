package co.com.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    private String id;

    private String name;

    private List<Product> products;
}
