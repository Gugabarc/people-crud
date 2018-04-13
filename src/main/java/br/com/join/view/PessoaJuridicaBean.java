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
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.join.model.PessoaJuridica;
import br.com.join.report.ReportServlet;
import lombok.extern.slf4j.Slf4j;
import br.com.join.dao.PessoaDAO;
import br.com.join.model.BrazilianState;
import br.com.join.model.Pessoa;

/**
 * Backing bean for PessoaJuridica entities.
 * <p/>
 * This class provides CRUD functionality for all PessoaJuridica entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
@Slf4j
public class PessoaJuridicaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	@Transient
	private transient PessoaDAO pessoaDAO;

	/*
	 * Support creating and retrieving PessoaJuridica entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private PessoaJuridica pessoaJuridica;

	public PessoaJuridica getPessoaJuridica() {
		return this.pessoaJuridica;
	}

	public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
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
			this.pessoaJuridica = this.example;
		} else {
			this.pessoaJuridica = (PessoaJuridica) findById(getId());
		}
	}

	public Pessoa findById(Long id) {
		return pessoaDAO.findById(id);
	}

	/*
	 * Support updating and deleting PessoaJuridica entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				pessoaDAO.update(this.pessoaJuridica);
				return "search?faces-redirect=true";
			} else {
				pessoaDAO.update(this.pessoaJuridica);
				return "view?faces-redirect=true&id="
						+ this.pessoaJuridica.getId();
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
			pessoaDAO.delete(pessoaJuridica);
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching PessoaJuridica entities with pagination
	 */

	private int page;
	private long count;
	private List<PessoaJuridica> pageItems;

	private PessoaJuridica example = new PessoaJuridica();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public PessoaJuridica getExample() {
		return this.example;
	}

	public void setExample(PessoaJuridica example) {
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
		Root<PessoaJuridica> root = countCriteria.from(PessoaJuridica.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<PessoaJuridica> criteria = builder
				.createQuery(PessoaJuridica.class);
		root = criteria.from(PessoaJuridica.class);
		TypedQuery<PessoaJuridica> query = this.entityManager
				.createQuery(criteria.select(root).where(
						getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<PessoaJuridica> root) {

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

	public List<PessoaJuridica> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back PessoaJuridica entities (e.g. from
	 * inside an HtmlSelectOneMenu)
	 */

	public List<PessoaJuridica> getAll() {
		return pessoaDAO.getAllPessoaJuridica();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final PessoaJuridicaBean ejbProxy = this.sessionContext
				.getBusinessObject(PessoaJuridicaBean.class);

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

				return String.valueOf(((PessoaJuridica) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private PessoaJuridica add = new PessoaJuridica();

	public PessoaJuridica getAdd() {
		return this.add;
	}

	public PessoaJuridica getAdded() {
		PessoaJuridica added = this.add;
		this.add = new PessoaJuridica();
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
