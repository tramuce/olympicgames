package br.com.olympicgames.repository.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import br.com.olympicgames.repository.model.Country;

/**
 * @author tramuce
 * 
 *         Interface utilizada para consulta ao elemento Country. Por utilizar
 *         Spring Data, não é necessário implementar a interface, a não ser que
 *         seja necessário subescrever as consultas padrões já disponibilizadas.
 *         A Interface JpaRepository e QueryByExampleExecutor já disponibiliza
 *         todos os método básicos necessários.
 *
 */
@Repository
public interface ICountryRepository extends JpaRepository<Country, Long>, QueryByExampleExecutor<Country> {

}
