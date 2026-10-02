package trixi.interview.kopidlno.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_town")
    @NotNull
    private Long id;
    @Column(name = "code", unique = true)
    @NotNull
    private Long code;
    @Column(name = "name")
    @NotNull
    private String name;

    @OneToMany(targetEntity = TownPart.class, mappedBy = "town",
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TownPart> townParts;
}
