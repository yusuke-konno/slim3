/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slim3.util.ConversionUtil;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.SortPredicate;

/**
 * A query class for select.
 * 
 * @author higa
 * @param <M>
 *            the model type
 * @since 3.0
 * 
 */
public class ModelQuery<M> extends AbstractQuery<ModelQuery<M>> {

    /**
     * The meta data of model.
     */
    protected ModelMeta<M> modelMeta;

    /**
     * The in-memory filter criteria.
     */
    protected List<FilterCriterion> inMemoryFilterCriteria =
        new ArrayList<FilterCriterion>();

    /**
     * The in-memory sort criteria.
     */
    protected List<SortCriterion> inMemorySortCriteria =
        new ArrayList<SortCriterion>();

    /**
     * Constructor.
     * 
     * @param modelMeta
     *            the meta data of model
     * @throws NullPointerException
     *             if the modelMeta parameter is null
     */
    public ModelQuery(ModelMeta<M> modelMeta) throws NullPointerException {
        if (modelMeta == null) {
            throw new NullPointerException("The modelMeta parameter is null.");
        }
        this.modelMeta = modelMeta;
        setUpQuery(modelMeta.getKind());
    }

    /**
     * Constructor.
     * 
     * @param modelMeta
     *            the meta data of model
     * @param ancestorKey
     *            the ancestor key
     * @throws NullPointerException
     *             if the modelMeta parameter is null or if the ancestorKey
     *             parameter is null
     */
    public ModelQuery(ModelMeta<M> modelMeta, Key ancestorKey)
            throws NullPointerException {
        if (modelMeta == null) {
            throw new NullPointerException("The modelMeta parameter is null.");
        }
        if (ancestorKey == null) {
            throw new NullPointerException("The ancestorKey parameter is null.");
        }
        this.modelMeta = modelMeta;
        setUpQuery(modelMeta.getKind(), ancestorKey);
    }

    /**
     * Constructor.
     * 
     * @param tx
     *            the transaction
     * @param modelMeta
     *            the meta data of model
     * @param ancestorKey
     *            the ancestor key
     * @throws NullPointerException
     *             if the modelMeta parameter is null or if the ancestorKey
     *             parameter is null
     */
    public ModelQuery(Transaction tx, ModelMeta<M> modelMeta, Key ancestorKey)
            throws NullPointerException {
        if (modelMeta == null) {
            throw new NullPointerException("The modelMeta parameter is null.");
        }
        if (ancestorKey == null) {
            throw new NullPointerException("The ancestorKey parameter is null.");
        }
        setTx(tx);
        this.modelMeta = modelMeta;
        setUpQuery(modelMeta.getKind(), ancestorKey);
    }

    /**
     * Adds the filter criteria.
     * 
     * @param criteria
     *            the filter criteria
     * @return this instance
     * @throws NullPointerException
     *             if the element of the criteria parameter is null
     */
    public ModelQuery<M> filter(FilterCriterion... criteria)
            throws NullPointerException {
        for (FilterCriterion c : criteria) {
            if (c == null) {
                throw new NullPointerException(
                    "The element of the criteria parameter must not be null.");
            }
            for (FilterPredicate p : c.getFilterPredicates()) {
                query.addFilter(p.getPropertyName(), p.getOperator(), p
                    .getValue());
            }
        }
        return this;
    }

    /**
     * Adds the filters.
     * 
     * @param filters
     *            the filters
     * @return this instance
     * @throws NullPointerException
     *             if the element of the filters parameter is null
     */
    public ModelQuery<M> filter(FilterPredicate... filters)
            throws NullPointerException {
        for (FilterPredicate f : filters) {
            if (f == null) {
                throw new NullPointerException(
                    "The element of the filters parameter must not be null.");
            }
            query.addFilter(f.getPropertyName(), f.getOperator(), f.getValue());
        }
        return this;
    }

    /**
     * Adds the filter.
     * 
     * @param propertyName
     *            the property name
     * @param operator
     *            the {@link FilterOperator}
     * @param value
     *            the value
     * 
     * @return this instance
     * @throws NullPointerException
     *             if the propertyName parameter is null or if the operator
     *             parameter is null
     */
    public ModelQuery<M> filter(String propertyName, FilterOperator operator,
            Object value) throws NullPointerException {
        if (propertyName == null) {
            throw new NullPointerException(
                "The propertyName parameter must not be null.");
        }
        if (operator == null) {
            throw new NullPointerException(
                "The operator parameter must not be null.");
        }
        query.addFilter(propertyName, operator, value);
        return this;
    }

    /**
     * Adds the in-memory filter criteria.
     * 
     * @param criteria
     *            the in-memory filter criteria
     * @return this instance
     * @throws NullPointerException
     *             if the element of the criteria parameter is null
     */
    public ModelQuery<M> filterInMemory(FilterCriterion... criteria)
            throws NullPointerException {
        for (FilterCriterion c : criteria) {
            if (c == null) {
                throw new NullPointerException(
                    "The element of the criteria parameter must not be null.");
            }
            inMemoryFilterCriteria.add(c);
        }
        return this;
    }

    /**
     * Adds the sort criteria.
     * 
     * @param criteria
     *            the sort criteria
     * @return this instance
     * @throws NullPointerException
     *             if the element of the criteria parameter is null
     */
    public ModelQuery<M> sort(SortCriterion... criteria)
            throws NullPointerException {
        for (SortCriterion c : criteria) {
            if (c == null) {
                throw new NullPointerException(
                    "The element of the criteria parameter must not be null.");
            }
            SortPredicate p = c.getSortPredicate();
            query.addSort(p.getPropertyName(), p.getDirection());
        }
        return this;
    }

    /**
     * Adds the sorts.
     * 
     * @param sorts
     *            the array of sorts
     * @return this instance
     * @throws NullPointerException
     *             if the element of the sorts parameter is null
     */
    public ModelQuery<M> sort(SortPredicate... sorts)
            throws NullPointerException {
        for (SortPredicate s : sorts) {
            if (s == null) {
                throw new NullPointerException(
                    "The element of the sorts parameter must not be null.");
            }
            query.addSort(s.getPropertyName(), s.getDirection());
        }
        return this;
    }

    /**
     * Adds the sort.
     * 
     * @param propertyName
     *            the property name
     * @return this instance
     * @throws NullPointerException
     *             if the propertyName parameter is null
     */
    public ModelQuery<M> sort(String propertyName) throws NullPointerException {
        return sort(propertyName, SortDirection.ASCENDING);
    }

    /**
     * Adds the sort.
     * 
     * @param propertyName
     *            the property name
     * @param direction
     *            the sort direction
     * @return this instance
     * @throws NullPointerException
     *             if the propertyName parameter is null or if the direction
     *             parameter is null
     */
    public ModelQuery<M> sort(String propertyName, SortDirection direction)
            throws NullPointerException {
        if (propertyName == null) {
            throw new NullPointerException(
                "The propertyName parameter must not be null.");
        }
        if (direction == null) {
            throw new NullPointerException(
                "The direction parameter must not be null.");
        }
        query.addSort(propertyName, direction);
        return this;
    }

    /**
     * Adds the in-memory sort criteria.
     * 
     * @param criteria
     *            the in-memory sort criteria
     * @return this instance
     * @throws NullPointerException
     *             if the element of the criteria parameter is null
     */
    public ModelQuery<M> sortInMemory(SortCriterion... criteria)
            throws NullPointerException {
        for (SortCriterion c : criteria) {
            if (c == null) {
                throw new NullPointerException(
                    "The element of the criteria parameter must not be null.");
            }
            inMemorySortCriteria.add(c);
        }
        return this;
    }

    /**
     * Returns the result as a list.
     * 
     * @return the result as a list
     */
    public List<M> asList() {
        addFilterIfPolyModel();
        List<Entity> entityList = asEntityList();
        List<M> ret = new ArrayList<M>(entityList.size());
        for (Entity e : entityList) {
            ModelMeta<M> mm = DatastoreUtil.getModelMeta(modelMeta, e);
            ret.add(mm.entityToModel(e));
        }
        ret = DatastoreUtil.filterInMemory(ret, inMemoryFilterCriteria);
        return DatastoreUtil.sortInMemory(ret, inMemorySortCriteria);
    }

    /**
     * Returns the single result or null if no entities match.
     * 
     * @return the single result
     * @throws PreparedQuery.TooManyResultsException
     *             if the number of the results are more than 1 entity.
     * 
     */
    public M asSingle() throws PreparedQuery.TooManyResultsException {
        List<M> list = asList();
        if (list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new PreparedQuery.TooManyResultsException();
        }
        return list.get(0);
    }

    /**
     * Returns a list of keys.
     * 
     * @return a list of keys
     * @throws IllegalStateException
     *             if in-memory filers are specified
     */
    @Override
    public List<Key> asKeyList() throws IllegalStateException {
        if (inMemoryFilterCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of asKeyList(), you cannot specify filterInMemory().");
        }
        addFilterIfPolyModel();
        List<Key> keys = super.asKeyList();
        if (inMemorySortCriteria.size() > 0 && inMemorySortCriteria.size() == 1) {
            SortCriterion c = inMemorySortCriteria.get(0);
            if (c instanceof AbstractCriterion) {
                if (AbstractCriterion.class.cast(c).attributeMeta.name
                    .equals(Entity.KEY_RESERVED_PROPERTY)) {
                    if (c instanceof AscCriterion) {
                        Collections.sort(keys);
                    } else {
                        Collections.sort(keys, KeyDescComparator.INSTANCE);
                    }
                    return keys;
                }
            }
            throw new IllegalStateException(
                "In the case of asKeyList(), you cannot specify sortInMemory() except for primary key.");
        }
        return keys;
    }

    /**
     * Return a minimum value of the property. The value does not include null.
     * 
     * @param <A>
     *            the attribute type
     * @param attributeMeta
     *            the meta data of attribute
     * @return a minimum value of the property
     * @throws NullPointerException
     *             if the attributeMeta parameter is null
     * @throws IllegalStateException
     *             if in-memory filers are specified or if in-memory sorts are
     *             specified
     */
    @SuppressWarnings("unchecked")
    public <A> A min(CoreAttributeMeta<M, A> attributeMeta)
            throws NullPointerException, IllegalStateException {
        if (attributeMeta == null) {
            throw new NullPointerException(
                "The attributeMeta parameter is null.");
        }
        if (inMemoryFilterCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of min(), you cannot specify filterInMemory().");
        }
        if (inMemorySortCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of min(), you cannot specify sortInMemory().");
        }
        addFilterIfPolyModel();
        Object value = super.min(attributeMeta.getName());
        return (A) ConversionUtil.convert(value, attributeMeta
            .getAttributeClass());
    }

    /**
     * Return a maximum value of the property.
     * 
     * @param <A>
     *            the attribute type
     * @param attributeMeta
     *            the meta data of attribute
     * @return a maximum value of the property
     * @throws NullPointerException
     *             if the attributeMeta parameter is null
     * @throws IllegalStateException
     *             if in-memory filters are specified or if in-memory sorts are
     *             specified
     */
    @SuppressWarnings("unchecked")
    public <A> A max(CoreAttributeMeta<M, A> attributeMeta)
            throws NullPointerException, IllegalStateException {
        if (attributeMeta == null) {
            throw new NullPointerException(
                "The attributeMeta parameter is null.");
        }
        if (inMemoryFilterCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of max(), you cannot specify filterInMemory().");
        }
        if (inMemorySortCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of max(), you cannot specify sortInMemory().");
        }
        addFilterIfPolyModel();
        Object value = super.max(attributeMeta.getName());
        return (A) ConversionUtil.convert(value, attributeMeta
            .getAttributeClass());
    }

    /**
     * Returns a number of entities.
     * 
     * @return a number of entities
     * @throws IllegalStateException
     *             if in-memory filers are specified or if in-memory sorts are
     *             specified
     */
    @Override
    public int count() throws IllegalStateException {
        if (inMemoryFilterCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of count(), you cannot specify filterInMemory().");
        }
        if (inMemorySortCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of count(), you cannot specify sortInMemory().");
        }
        addFilterIfPolyModel();
        return super.count();
    }

    /**
     * Returns a number of entities. This method can only return up to 1,000
     * results, but this method can return the results quickly.
     * 
     * @return a number of entities
     * @throws IllegalStateException
     *             if in-memory filers are specified
     */
    @Override
    public int countQuickly() throws IllegalStateException {
        if (inMemoryFilterCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of countQuickly(), you cannot specify filterInMemory().");
        }
        if (inMemorySortCriteria.size() > 0) {
            throw new IllegalStateException(
                "In the case of countQuickly(), you cannot specify sortInMemory().");
        }
        addFilterIfPolyModel();
        return super.countQuickly();
    }

    /**
     * Adds a filter for polymorphic model.
     */
    protected void addFilterIfPolyModel() {
        if (modelMeta.getClassHierarchyList().isEmpty()) {
            return;
        }
        query.addFilter(
            ModelMeta.CLASS_HIERARCHY_LIST_RESERVED_PROPERTY,
            FilterOperator.EQUAL,
            modelMeta.getModelClass().getName());
    }
}