package br.com.olympicgames.repository.model;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * @author tramuce
 * 
 *         Modelo da entidade Competition. São utilizadas os novos tipo
 *         OffsetDateTime do java 8 para as datas. Tal data já é formatado nas
 *         especificações ISO8601 com time zone. Obs. Deve-se verificar a
 *         compatibilidade do tipo com o banco de dados (existente na versão
 *         2.4.0 do HSQLDB)
 *
 */
@Entity(name = "competition")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_location")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "id_modality")
    private Modality modality;

    @ManyToOne
    @JoinColumn(name = "id_phase")
    private Phase phase;

    @Column(name = "init_date")
    private OffsetDateTime initDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "competition_country", joinColumns = {
	    @JoinColumn(name = "id_competition") }, inverseJoinColumns = { @JoinColumn(name = "id_country") })
    private List<Country> countries;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }

    public Modality getModality() {
	return modality;
    }

    public void setModality(Modality modality) {
	this.modality = modality;
    }

    public Phase getPhase() {
	return phase;
    }

    public void setPhase(Phase phase) {
	this.phase = phase;
    }

    public OffsetDateTime getInitDate() {
	return initDate;
    }

    public void setInitDate(OffsetDateTime initDate) {
	this.initDate = initDate;
    }

    public OffsetDateTime getEndDate() {
	return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
	this.endDate = endDate;
    }

    public List<Country> getCountries() {
	return countries;
    }

    public void setCountries(List<Country> contries) {
	this.countries = contries;
    }
}
