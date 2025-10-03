package com.example.chatapp.specification;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.Topic;
import com.example.chatapp.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RoomSpecifications {
    public static Specification<Room> hasSearchWord(String searchWord) {
        return (root, query, cb) -> {
            if (searchWord == null || searchWord.isEmpty()) return cb.conjunction();

            query.distinct(true);

            String pattern = "%" + searchWord.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.join("topics").get("name")), pattern)
            );
        };
    }

    public static Specification<Room> hasTopics(List<Long> topicIds) {
        return (root, query, cb) -> {
            if (topicIds == null || topicIds.isEmpty()) return cb.conjunction();

            query.distinct(true);

            Join<Room, Topic> topicsJoin = root.join("topics", JoinType.INNER);

            query.groupBy(root.get("id"));
            query.having(cb.equal(cb.countDistinct(topicsJoin.get("id")), topicIds.size()));
            return topicsJoin.get("id").in(topicIds);
        };
    }

    public static Specification<Room> orderByMembersCount(Sort.Direction direction) {
        return (root, query, cb) -> {
            Join<Room, User> membersJoin = root.join("members", JoinType.LEFT);
            query.groupBy(root.get("id"));

            if (direction.isAscending()) {
                query.orderBy(cb.asc(cb.count(membersJoin.get("id"))));
            } else {
                query.orderBy(cb.desc(cb.count(membersJoin.get("id"))));
            }

            return cb.conjunction();
        };
    }

    public static Specification<Room> hasMember(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            Join<Object, Object> members = root.join("members", JoinType.INNER);
            return cb.equal(members.get("id"), userId);
        };
    }
}
