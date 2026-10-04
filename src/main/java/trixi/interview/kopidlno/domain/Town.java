package trixi.interview.kopidlno.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "town")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Town {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_town", nullable = false)
    private Long id;
    @Column(name = "code", unique = true, nullable = false)
    private Long code;
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(targetEntity = TownPart.class, mappedBy = "town",
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TownPart> townParts;
}
