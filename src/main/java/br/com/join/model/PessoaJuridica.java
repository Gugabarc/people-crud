package br.com.join.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@DiscriminatorValue("PJ")
public class PessoaJuridica extends Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "foundation_date")
	@Temporal(TemporalType.DATE)
	private Date foundationDate;

	public Date getFoundationDate() {
		return foundationDate;
	}

	public void setFoundationDate(Date foundationDate) {
		this.foundationDate = foundationDate;
	}

}