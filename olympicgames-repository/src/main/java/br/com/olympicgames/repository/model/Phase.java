package br.com.olympicgames.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author tramuce
 * 
 *         Modelo da entidade Phase.
 *
 */
@Entity(name = "phase")
public class Phase implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -710947176876814617L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "tag")
    private String tag;

    @Column(name = "allow_same_country")
    private boolean allowSameCountry;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getTag() {
	return tag;
    }

    public void setTag(String tag) {
	this.tag = tag;
    }

    public boolean isAllowSameCountry() {
	return allowSameCountry;
    }

    public void setAllowSameCountry(boolean allowSameCountry) {
	this.allowSameCountry = allowSameCountry;
    }
}
