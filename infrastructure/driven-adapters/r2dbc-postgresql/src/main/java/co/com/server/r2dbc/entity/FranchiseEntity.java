package co.com.server.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("franchise")
public class FranchiseEntity {

    @Id
    @Column("id")
    private String id;

    @Column("name")
    private String name;

    @Column("branches")
    private String branches;
}
