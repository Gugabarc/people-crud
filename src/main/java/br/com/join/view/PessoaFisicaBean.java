package br.com.join.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.join.model.BrazilianState;
import br.com.join.model.PessoaFisica;
import br.com.join.report.ReportServlet;
import lombok.extern.slf4j.Slf4j;

/**
 * Backing bean for PessoaFisica entities.
 * <p/>
 * This class provides CRUD functionality for all PessoaFisica entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
@Slf4j
public class PessoaFisicaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving PessoaFisica entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private PessoaFisica pessoaFisica;

	public PessoaFisica getPessoaFisica() {
		return this.pessoaFisica;
	}

	public void setPessoaFisica(PessoaFisica pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "people-crud-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.pessoaFisica = this.example;
		} else {
			this.pessoaFisica = findById(getId());
		}
	}

	public PessoaFisica findById(Long id) {

		return this.entityManager.find(PessoaFisica.class, id);
	}

	/*
	 * Support updating and deleting PessoaFisica entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.pessoaFisica);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.pessoaFisica);
				return "view?faces-redirect=true&id="
						+ this.pessoaFisica.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			PessoaFisica deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching PessoaFisica entities with pagination
	 */

	private int page;
	private long count;
	private List<PessoaFisica> pageItems;

	private PessoaFisica example = new PessoaFisica();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public PessoaFisica getExample() {
		return this.example;
	}

	public void setExample(PessoaFisica example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<PessoaFisica> root = countCriteria.from(PessoaFisica.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<PessoaFisica> criteria = builder
				.createQuery(PessoaFisica.class);
		root = criteria.from(PessoaFisica.class);
		TypedQuery<PessoaFisica> query = this.entityManager
				.createQuery(criteria.select(root).where(
						getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<PessoaFisica> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		if (name != null && !"".equals(name)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("name")),
					'%' + name.toLowerCase() + '%'));
		}
		BrazilianState uf = this.example.getUf();
		if (uf != null) {
			predicatesList.add(builder.equal(root.get("uf"), uf));
		}
		String city = this.example.getCity();
		if (city != null && !"".equals(city)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("city")),
					'%' + city.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<PessoaFisica> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back PessoaFisica entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<PessoaFisica> getAll() {

		CriteriaQuery<PessoaFisica> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(PessoaFisica.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(PessoaFisica.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final PessoaFisicaBean ejbProxy = this.sessionContext
				.getBusinessObject(PessoaFisicaBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((PessoaFisica) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private PessoaFisica add = new PessoaFisica();

	public PessoaFisica getAdd() {
		return this.add;
	}

	public PessoaFisica getAdded() {
		PessoaFisica added = this.add;
		this.add = new PessoaFisica();
		return added;
	}
	
	/**
	 * Redirects to {@link ReportServlet} servlet
	 */
	public void generateReport() {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ectx = ctx.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
			HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
			RequestDispatcher dispatcher = request.getRequestDispatcher("/generatePessoaReport");
			dispatcher.forward(request, response);
			ctx.responseComplete();
		} catch (Exception e) {
			log.error("Error when trying to redirec to ReportServlet", e);
		}
	}
}
