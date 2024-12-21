package ru.practicum.common.repository.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.practicum.common.enums.State;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.EventSearch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventSearchRepositoryImpl implements EventSearchRepository {

    private final EntityManager entityManager;

    private final CriteriaBuilder criteriaBuilder;

    @Autowired
    public EventSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public Page<Event> findWithFilter(EventSearch eventSearch, Pageable pageable) {
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);
        Predicate predicate = getfilterPredicate(root, eventSearch);

        query.where(predicate);

        if (pageable.getSort().isUnsorted()) {
            query.orderBy(criteriaBuilder.desc(root.get("createdOn")));
        }

        TypedQuery<Event> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Event> events = typedQuery.getResultList();

        return new PageImpl<>(events);
    }


    private Predicate getfilterPredicate(Root<Event> root, EventSearch eventSearch) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(eventSearch.getText())) {
            Predicate annotation = criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                    "%" + eventSearch.getText().toLowerCase() + "%");
            Predicate description = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + eventSearch.getText().toLowerCase() + "%");
            predicates.add(criteriaBuilder.or(annotation, description));
        }

        if (!CollectionUtils.isEmpty(eventSearch.getCategories())) {
            predicates.add(root.get("category").get("id").in(eventSearch.getCategories()));
        }

        LocalDateTime start = eventSearch.getRangeStart() != null ?
                eventSearch.getRangeStart() : LocalDateTime.now();
        LocalDateTime end = eventSearch.getRangeEnd() != null ?
                eventSearch.getRangeEnd() : LocalDateTime.now().plusYears(100);
        predicates.add(criteriaBuilder.between(root.get("eventDate"), start, end));

        predicates.add(criteriaBuilder.equal(root.get("state"), State.PUBLISHED));

        if (eventSearch.getPaid() != null) {
            predicates.add(criteriaBuilder.equal(root.get("paid"), eventSearch.getPaid()));
        }

        if (Boolean.TRUE.equals(eventSearch.getOnlyAvailable())) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("participantLimit")),
                    criteriaBuilder.greaterThan(
                            criteriaBuilder.diff(root.get("participantLimit"), root.get("confirmedRequests")), 0L)
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
