package github.gc.hibernate.session.proxy.impl;

import github.gc.hibernate.session.proxy.QueryProxySupport;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Parameter;
import jakarta.persistence.TemporalType;
import org.hibernate.FlushMode;
import org.hibernate.StatelessSession;
import org.hibernate.query.BindableType;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.QueryFlushMode;
import org.hibernate.query.QueryParameter;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class MutationQueryProxy extends QueryProxySupport implements MutationQuery {

	private final MutationQuery delegate;

	public MutationQueryProxy(MutationQuery delegate, StatelessSession session) {
		super(session);

		Assert.notNull(delegate, "MutationQuery delegate must not be null");
		this.delegate = delegate;
	}

	@Override
	public int executeUpdate() {
		return execute(this.delegate::executeUpdate);
	}

	@Override
	public MutationQuery setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setHibernateFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Integer getTimeout() {
		return this.delegate.getTimeout();
	}

	@Override
	public MutationQuery setTimeout(int timeout) {
		this.delegate.setTimeout(timeout);
		return this;
	}

	@Override
	public String getComment() {
		return this.delegate.getComment();
	}

	@Override
	public MutationQuery setComment(String comment) {
		this.delegate.setComment(comment);
		return this;
	}

	@Override
	public MutationQuery setHint(String hintName, Object value) {
		this.delegate.setHint(hintName, value);
		return this;
	}

	@Override
	public MutationQuery setParameter(String name, Object value) {
		this.delegate.setParameter(name, value);
		return this;
	}

	@Override
	public <P> MutationQuery setParameter(String name, P value, Class<P> type) {
		this.delegate.setParameter(name, value, type);
		return this;
	}

	@Override
	public <P> MutationQuery setParameter(String name, P value, BindableType<P> type) {
		this.delegate.setParameter(name, value, type);
		return this;
	}

	@Override
	public MutationQuery setParameter(String name, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameter(String name, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameter(String name, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameter(int position, Object value) {
		this.delegate.setParameter(position, value);
		return this;
	}

	@Override
	public <P> MutationQuery setParameter(int position, P value, Class<P> type) {
		this.delegate.setParameter(position, value, type);
		return this;
	}

	@Override
	public <P> MutationQuery setParameter(int position, P value, BindableType<P> type) {
		this.delegate.setParameter(position, value, type);
		return this;
	}

	@Override
	public MutationQuery setParameter(int position, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameter(int position, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameter(int position, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public <T> MutationQuery setParameter(QueryParameter<T> parameter, T value) {
		this.delegate.setParameter(parameter, value);
		return this;
	}

	@Override
	public <P> MutationQuery setParameter(QueryParameter<P> parameter, P value, Class<P> type) {
		this.delegate.setParameter(parameter, value, type);
		return this;
	}

	@Override
	public <P> MutationQuery setParameter(QueryParameter<P> parameter, P value, BindableType<P> type) {
		this.delegate.setParameter(parameter, value, type);
		return this;
	}

	@Override
	public <T> MutationQuery setParameter(Parameter<T> param, T value) {
		this.delegate.setParameter(param, value);
		return this;
	}

	@Override
	public MutationQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public MutationQuery setParameterList(String name, Collection values) {
		this.delegate.setParameterList(name, values);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(String name, Collection<? extends P> values, Class<P> javaType) {
		this.delegate.setParameterList(name, values, javaType);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(String name, Collection<? extends P> values, BindableType<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public MutationQuery setParameterList(String name, Object[] values) {
		this.delegate.setParameterList(name, values);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(String name, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(name, values, javaType);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(String name, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public MutationQuery setParameterList(int position, Collection values) {
		this.delegate.setParameterList(position, values);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(int position, Collection<? extends P> values, Class<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(int position, Collection<? extends P> values, BindableType<P> type) {
		this.delegate.setParameterList(position, values, type);
		return this;
	}

	@Override
	public MutationQuery setParameterList(int position, Object[] values) {
		this.delegate.setParameterList(position, values);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(int position, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(int position, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(position, values, type);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(QueryParameter<P> parameter, Collection<? extends P> values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			Class<P> javaType) {
		this.delegate.setParameterList(parameter, values, javaType);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			BindableType<P> type) {
		this.delegate.setParameterList(parameter, values, type);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(QueryParameter<P> parameter, P[] values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(QueryParameter<P> parameter, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(parameter, values, javaType);
		return this;
	}

	@Override
	public <P> MutationQuery setParameterList(QueryParameter<P> parameter, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(parameter, values, type);
		return this;
	}

	@Override
	public MutationQuery setProperties(Object bean) {
		this.delegate.setProperties(bean);
		return this;
	}

	@Override
	public MutationQuery setProperties(Map bean) {
		this.delegate.setProperties(bean);
		return this;
	}

	@Override
	public QueryFlushMode getQueryFlushMode() {
		return this.delegate.getQueryFlushMode();
	}

	@Override
	public MutationQuery setQueryFlushMode(QueryFlushMode queryFlushMode) {
		this.delegate.setQueryFlushMode(queryFlushMode);
		return this;
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}
}