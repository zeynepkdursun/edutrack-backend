package com.admin.edu_track.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(String message) {
    super(message);
  }
}


/*
EntityNotFoundException
BadRequestException
ConflictException
UnauthorizedException

 */


/*
  public class EntityNotFoundException extends RuntimeException {

      public EntityNotFoundException(String entity, Object id) {
          super(entity + " not found with id: " + id);
      }
  }




  throw new EntityNotFoundException("Student", id);
*/
