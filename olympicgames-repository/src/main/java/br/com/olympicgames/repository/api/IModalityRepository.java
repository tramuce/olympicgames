package br.com.olympicgames.repository.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import br.com.olympicgames.repository.model.Modality;

@Repository
public interface IModalityRepository extends JpaRepository<Modality, Long>, QueryByExampleExecutor<Modality> {

}
