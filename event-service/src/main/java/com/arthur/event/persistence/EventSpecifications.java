package com.arthur.event.persistence;

import com.arthur.event.domain.Event;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class EventSpecifications {

    private EventSpecifications () {
    }

    public static Specification<Event> nameContains(String q) {
        if(q == null || q.isBlank()) return null;
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), like);
    }

    public static Specification<Event> locationEquals(String location){
        if(location == null || location.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("location")), location.trim().toLowerCase());
    }

    public static Specification<Event> startBetween(LocalDateTime from, LocalDateTime to){
        if(from == null && to == null) return null;
        if(from != null && to != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("startTime"), from, to);
        }
        if(from != null) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), from);
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), to);

    }

}
