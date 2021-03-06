package org.apereo.cas.support.events.mongo;

import org.apereo.cas.support.events.dao.AbstractCasEventRepository;
import org.apereo.cas.support.events.dao.CasEvent;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * This is {@link MongoDbCasEventRepository} that stores event data into a mongodb database.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class MongoDbCasEventRepository extends AbstractCasEventRepository {

    private final String collectionName;
    private final MongoOperations mongoTemplate;

    public MongoDbCasEventRepository(final MongoOperations mongoTemplate, final String collectionName, final boolean dropCollection) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = collectionName;

        Assert.notNull(this.mongoTemplate);

        if (dropCollection) {
            logger.debug("Dropping database collection: {}", this.collectionName);
            this.mongoTemplate.dropCollection(this.collectionName);
        }

        if (!this.mongoTemplate.collectionExists(this.collectionName)) {
            logger.debug("Creating database collection: {}", this.collectionName);
            this.mongoTemplate.createCollection(this.collectionName);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void save(final CasEvent event) {
        this.mongoTemplate.save(event, this.collectionName);
    }

    @Override
    public Collection<CasEvent> load() {
        return this.mongoTemplate.findAll(CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> getEventsForPrincipal(final String id) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("principalId").is(id));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> getEventsOfType(final String type) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> getEventsOfTypeForPrincipal(final String type, final String principal) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type).and("principalId").is(principal));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> load(final ZonedDateTime dateTime) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("creationTime").gte(dateTime.toString()));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> getEventsOfTypeForPrincipal(final String type, final String principal, final ZonedDateTime dateTime) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type).and("principalId").is(principal).and("creationTime").gte(dateTime.toString()));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> getEventsOfType(final String type, final ZonedDateTime dateTime) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type).and("creationTime").gte(dateTime.toString()));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }

    @Override
    public Collection<CasEvent> getEventsForPrincipal(final String principal, final ZonedDateTime dateTime) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("principalId").is(principal).and("creationTime").gte(dateTime.toString()));
        return this.mongoTemplate.find(query, CasEvent.class, this.collectionName);
    }
}
