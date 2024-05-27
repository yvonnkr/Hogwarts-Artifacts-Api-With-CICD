package com.yvolabs.hogwartsartifactsapi.artifact;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Yvonne N
 *
 * @apiNote We define some specification then can use them in repo to create a query and can combine the specs using logical operators(and,or etc)
 *
 *  @apiNote criteriaBuilder.like(), There are 2 wildcards often used in conjunction with the .like() method, to check if provided is contained in the value
 *            % = represents zero, one or multiple characters
 *            _ = represents one, single character
 */
public class ArtifactSpecs {

    public static Specification<Artifact> hasId(String providedId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), providedId);
    }

    public static Specification<Artifact> containsName(String providedName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + providedName.toLowerCase() + "%");
    }

    public static Specification<Artifact> containsDescription(String providedDescription) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + providedDescription.toLowerCase() + "%");
    }

    public static Specification<Artifact> hasOwnerName(String providedOwnerName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("owner").get("name")), providedOwnerName.toLowerCase());
    }


}
