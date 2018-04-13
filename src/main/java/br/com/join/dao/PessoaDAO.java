package br.com.join.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaQuery;

import br.com.join.model.Pessoa;
import br.com.join.model.PessoaFisica;
import br.com.join.model.PessoaJuridica;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PessoaDAO {

	@PersistenceContext(unitName = "people-crud-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public Pessoa findById(Long id) {
		log.debug("Finding 'Pessoa' by id {}", id);
		return this.entityManager.find(PessoaFisica.class, id);
	}

	public void update(Pessoa pessoa) {
		if (pessoa.getId() == null) {
			log.debug("Saving new 'Pessoa'. {}", pessoa);
			this.entityManager.persist(pessoa);
		} else {
			log.debug("Updating 'Pessoa' id {}. {}", pessoa.getId(), pessoa);
			this.entityManager.merge(pessoa);
		}
	}

	public void delete(Pessoa pessoa) {
		Pessoa deletableEntity = findById(pessoa.getId());
		log.debug("Deleting 'Pessoa'. {}", pessoa.getId());
		this.entityManager.remove(deletableEntity);
		this.entityManager.flush();
	}

	public List<Pessoa> getAll() {
		log.debug("Find all 'Pessoa'.");
		CriteriaQuery<Pessoa> criteria = this.entityManager.getCriteriaBuilder().createQuery(Pessoa.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(Pessoa.class)))
							.getResultList();
	}
	
	public List<PessoaFisica> getAllPessoaFisica() {
		log.debug("Find all 'Pessoa Fisica'.");
		CriteriaQuery<PessoaFisica> criteria = this.entityManager.getCriteriaBuilder().createQuery(PessoaFisica.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(PessoaFisica.class)))
							.getResultList();
	}
	
	public List<PessoaJuridica> getAllPessoaJuridica() {
		log.debug("Find all 'Pessoa Juridica'.");
		CriteriaQuery<PessoaJuridica> criteria = this.entityManager.getCriteriaBuilder().createQuery(PessoaJuridica.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(PessoaJuridica.class)))
							.getResultList();
	}
}
