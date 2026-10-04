package trixi.interview.kopidlno.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "town_part")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TownPart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_town_part", nullable = false)
    private Long id;
    @Column(name = "code", unique = true, nullable = false)
    private Long code;
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Town.class)
    @JoinColumn(name = "id_town", nullable = false)
    private Town town;
}
