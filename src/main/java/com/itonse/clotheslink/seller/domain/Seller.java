package com.itonse.clotheslink.seller.domain;

import com.itonse.clotheslink.common.BaseEntity;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class Seller extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private boolean authenticated;
}
