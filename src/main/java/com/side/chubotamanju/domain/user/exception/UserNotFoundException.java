package com.side.chubotamanju.domain.user.exception;

import com.side.chubotamanju.global.exception.BusinessException;
import com.side.chubotamanju.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
