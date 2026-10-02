package trixi.interview.kopidlno.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trixi.interview.kopidlno.domain.TownPart;

@Repository
public interface JPATownPartRepository extends JpaRepository<TownPart, Long> {
    TownPart findByCode(Long code);
}
