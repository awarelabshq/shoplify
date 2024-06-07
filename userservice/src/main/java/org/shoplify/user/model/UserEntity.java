package org.shoplify.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    @Column(name = "type")
    private String type;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FopEntity> paymentCards;

    public void addPaymentCard(FopEntity paymentCard) {
        paymentCards.add(paymentCard);
        paymentCard.setUser(this);
    }
}
