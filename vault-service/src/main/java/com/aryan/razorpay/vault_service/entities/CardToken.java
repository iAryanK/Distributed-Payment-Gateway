package com.aryan.razorpay.vault_service.entities;

import com.aryan.razorpay.common_lib.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card_token")
public class CardToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vault_card_id", nullable = false)
    private VaultCard vaultCard;

    private UUID customer;

    @Column(nullable = false)
    private UUID merchant;

    private LocalDateTime revokedAt;
}
