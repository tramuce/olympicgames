package br.com.olympicgames.repository.api;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.repository.model.Location;

/**
 * @author tramuce
 * 
 *         Interface utilizada para consulta ao elemento Competition. Por
 *         utilizar Spring Data, não é necessário implementar a interface, a não
 *         ser que seja necessário subescrever as consultas padrões já
 *         disponibilizadas.
 *
 */
@Repository
public interface ICompetitionRepository extends JpaRepository<Competition, Long>, QueryByExampleExecutor<Competition> {

    /**
     * @param idModality
     * @param idLocation
     * @param initDate
     * @param endDate
     * @return long representando a quantidade de itens encontrados
     * 
     *         A consulta @Query foi criada para possibilitar melhor
     *         entendimento e formatação de um única query que represente a
     *         intersecção entre as datas. Nesse caso, a montagem do nome do
     *         método não interfere na query que será realizada.
     */
    @Query("select count(c) from competition c where c.modality.id = ?1 and c.location.id = ?2 and "
	    + "((c.initDate between ?3 and ?4 or c.endDate between ?3 and ?4)"
	    + "	or (c.initDate between ?3 and ?4 and c.endDate between ?3 and ?4)"
	    + "	or (?3 between c.initDate and c.endDate and ?4 between c.initDate and c.endDate))")
    long countCompetitionsSameModalityLocationAndPeriod(Long idModality, Long idLocation, OffsetDateTime initDate,
	    OffsetDateTime endDate);

    /**
     * @param location
     * @param initDate
     * @param endDate
     * @return long representando a quantidade de itens encontrados
     * 
     *         A Query é montada dinâmicamente pelo próprio Spring Data, fazendo
     *         um count nas Competições onde a data de início esta entre os
     *         intervalor informados.
     */
    long countByLocationAndInitDateBetween(Location location, OffsetDateTime initDate, OffsetDateTime endDate);

}
