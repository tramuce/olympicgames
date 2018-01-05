package br.com.olympicgames.repository.api;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.repository.model.Location;

@Repository
public interface ICompetitionRepository extends JpaRepository<Competition, Long>, QueryByExampleExecutor<Competition> {

    @Query("select count(c) from competition c where c.modality.id = ?1 and c.location.id = ?2 and "
	    + "((c.initDate between ?3 and ?4 or c.endDate between ?3 and ?4)"
	    + "	or (c.initDate between ?3 and ?4 and c.endDate between ?3 and ?4)"
	    + "	or (?3 between c.initDate and c.endDate and ?4 between c.initDate and c.endDate))")
    long countCompetitionsSameModalityLocationAndPeriod(Long idModality, Long idLocation, OffsetDateTime initDate,
	    OffsetDateTime endDate);

    long countByLocationAndInitDateBetween(Location location, OffsetDateTime initDate, OffsetDateTime endDate);

}
