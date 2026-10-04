package com.admin.edu_track.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}


/*
ResourceNotFoundException
BadRequestException
ConflictException
UnauthorizedException

 */


/*
  public class ResourceNotFoundException extends RuntimeException {

      public ResourceNotFoundException(String entity, Object id) {
          super(entity + " not found with id: " + id);
      }
  }




  throw new ResourceNotFoundException("Student", id);
*/
