package gigedi.dev.domain.shoot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import gigedi.dev.domain.auth.domain.Figma;
import gigedi.dev.global.common.model.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShootTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shootTagId;

    @Column(nullable = false)
    private boolean is_read;

    @ManyToOne
    @JoinColumn(name = "shoot_id")
    @Column(nullable = false)
    private Shoot shoot;

    @ManyToOne
    @JoinColumn(name = "figma_id")
    @Column(nullable = false)
    private Figma figma;

    @Builder(access = AccessLevel.PRIVATE)
    private ShootTag(boolean is_read, Shoot shoot, Figma figma) {
        this.is_read = is_read;
        this.shoot = shoot;
        this.figma = figma;
    }

    public static ShootTag createShootTag(Shoot shoot, Figma figma) {
        return ShootTag.builder().is_read(false).shoot(shoot).figma(figma).build();
    }
}