package com.project.transfers.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "set")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "app")
public class UserEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID uuid;
    private String name;
    private DocumentEntity doc;
    @Column(name = "date_reg")
    private LocalDateTime dateRegistration;
    @Version
    @Column(name = "date_upd")
    @EqualsAndHashCode.Include
    private LocalDateTime dateLastUpd;

    public UserEntity(UUID uuid) {
        this.uuid = uuid;
    }
}
