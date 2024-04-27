package com.yvolabs.hogwartsartifactsapi.system.exception;

/**
 * @author Yvonne N
 */
public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String objectName, String stringId) {
        super("Could not find " + objectName + " with Id " + stringId);
    }

    public ObjectNotFoundException(String objectName, Integer integerId) {
        super("Could not find " + objectName + " with Id " + integerId);
    }

}
