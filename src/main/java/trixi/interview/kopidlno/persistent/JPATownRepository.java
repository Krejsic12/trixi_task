package trixi.interview.kopidlno.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trixi.interview.kopidlno.domain.Town;

@Repository
public interface JPATownRepository extends JpaRepository<Town, Long> {
    Town findByCode(Long code);
}
