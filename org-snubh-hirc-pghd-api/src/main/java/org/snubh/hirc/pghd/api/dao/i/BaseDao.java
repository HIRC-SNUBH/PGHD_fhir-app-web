package org.snubh.hirc.pghd.api.dao.i;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

public abstract class BaseDao<T> extends HibernateDaoSupport {

	private Class<?> persistentClass;

	public BaseDao() {

	}

	@SuppressWarnings("unchecked")
	public BaseDao(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@SuppressWarnings("unchecked")
	public DetachedCriteria getCriteria() throws InstantiationException, IllegalAccessException {

		T instance = (T) persistentClass.newInstance();
		DetachedCriteria crit = DetachedCriteria.forClass(persistentClass);
		crit.add(Example.create(instance));

		return crit;
	}

	@SuppressWarnings("unchecked")
	public DetachedCriteria getCriteria(String alias) throws InstantiationException, IllegalAccessException {

		T instance = (T) persistentClass.newInstance();
		DetachedCriteria crit = DetachedCriteria.forClass(persistentClass, alias);
		crit.add(Example.create(instance));

		return crit;
	}

	public <T> Serializable insert(T dto) {
		return getHibernateTemplate().save(dto);
	}

	public <T> void update(T dto) {
		getHibernateTemplate().saveOrUpdate(dto);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByCriteriaList(DetachedCriteria criteria) {
		List<T> list = (List<T>) getHibernateTemplate().findByCriteria(criteria);

		if (list == null || list.size() < 1) {
			return null;
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	public T findByCriteria(DetachedCriteria criteria) {
		List<T> list = (List<T>) getHibernateTemplate().findByCriteria(criteria);

		if (list == null || list.size() < 1) {
			return null;
		}

		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByCriteriaList(final DetachedCriteria criteria, final int firstResult, final int maxResults) throws DataAccessException {

		List<T> list = (List<T>) getHibernateTemplate().findByCriteria(criteria, firstResult, maxResults);
		if (list == null || list.size() < 1) {
			return null;
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	public Long findByCriteriaCount(DetachedCriteria criteria) {

		criteria.setProjection(Projections.rowCount());
		List<Long> list = (List<Long>) getHibernateTemplate().findByCriteria(criteria);

		if (list == null || list.size() < 1) {
			return 0L;
		}

		return list.get(0);
	}

}