package co.com.server.model;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Franchise {

    private String id;

    private String name;

    private List<Branch> branches;
}
