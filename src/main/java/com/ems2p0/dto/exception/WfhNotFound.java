package com.ems2p0.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WfhNotFound extends CustomExceptionDto{
   public WfhNotFound(String message) {
	   super (message);
   }
}
