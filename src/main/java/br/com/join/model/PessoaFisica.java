package br.com.join.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@DiscriminatorValue("PF")
public class PessoaFisica extends Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;

	@OneToMany
	@JoinTable(
		      name="pessoa_dependente",
		      joinColumns={ @JoinColumn(name="pessoa_id", referencedColumnName="id") },
		      inverseJoinColumns={ @JoinColumn(name="dependente_id", referencedColumnName="id") }
		  )
	private Set<Dependente> dependentes = new HashSet<Dependente>();

	@Column(name = "birth_date")
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	public Set<Dependente> getDependentes() {
		return dependentes;
	}

	public void setDependentes(Set<Dependente> dependentes) {
		this.dependentes = dependentes;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

}