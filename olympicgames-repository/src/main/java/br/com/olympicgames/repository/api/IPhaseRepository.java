package br.com.olympicgames.repository.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import br.com.olympicgames.repository.model.Phase;

@Repository
public interface IPhaseRepository extends JpaRepository<Phase, Long>, QueryByExampleExecutor<Phase> {

}
