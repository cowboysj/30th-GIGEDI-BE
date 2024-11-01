package gigedi.dev.domain.shoot.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import gigedi.dev.domain.auth.domain.Figma;
import gigedi.dev.domain.block.domain.Block;
import gigedi.dev.global.common.model.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shoot extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shootId;

    @Column(nullable = false, length = 1000)
    private String content;

    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "figma_id")
    @Column(nullable = false)
    private Figma figma;

    @ManyToOne
    @JoinColumn(name = "block_id")
    @Column(nullable = false)
    private Block block;

    @Builder(access = AccessLevel.PRIVATE)
    private Shoot(String content, Figma figma, Block block) {
        this.content = content;
        this.figma = figma;
        this.block = block;
    }

    public static Shoot createShoot(String content, Figma figma, Block block) {
        return Shoot.builder().content(content).figma(figma).block(block).build();
    }
}