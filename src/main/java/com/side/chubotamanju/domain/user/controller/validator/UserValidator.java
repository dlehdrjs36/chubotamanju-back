package com.side.chubotamanju.domain.user.controller.validator;


import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionCreateRequest;
import com.side.chubotamanju.global.exception.ValidationException;
import org.springframework.validation.BeanPropertyBindingResult;

/**
 * User 컨트롤러로 들어오는 요청 검증
 * 요청 객체의 단일 필드에 대한 검증은 @Valid를 사용한다.
 * 요청 객체에서 여러 필드를 묶어서 복합적으로 검증해야하는 경우에는 Validator 클래스로 검증한다.
 * Validator 에서는 복합 필드의 입력 값 범위, 형식의 유효성 정도만 검증하고 아이디 중복과 같은 비즈니스 룰은 서비스 계층에서 별도로 검증하여 실패 시, BusinessException 를 상속받아 구현한 도메인 Exception을 발생시켜서 처리한다.
 *
 */
public class UserValidator {
    /**
     * ProductCreateRequest 객체 검증
     * @param request
     */
    public static void validate(BountyMissionCreateRequest request) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, request.getClass().getSimpleName());
        validateSameNameDescription(request, bindingResult);
        validateGuildIdTitle(request, bindingResult);

        // 검증에서 발생한 에러가 있으면 예외 발생
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }

    /**
     * 복합 규칙: 길드 설정되었으면 제목도 반드시 입력
     * @param request
     * @param bindingResult
     */
    private static void validateGuildIdTitle(BountyMissionCreateRequest request, BeanPropertyBindingResult bindingResult) {
        if (request.getGuildId() != null && request.getTitle() == null) {
            bindingResult.reject("Object.BountyMissionCreateRequest.validatGuildIdTitle"
                    , null
                    , "길드 설정 후 제목도 입력해야 합니다."
            );
        }
    }

    /**
     * 복합 규칙: 이름과 설명이 같으면 안 된다
     * @param request
     * @param bindingResult
     */
    private static void validateSameNameDescription(BountyMissionCreateRequest request, BeanPropertyBindingResult bindingResult) {
        if (request.getTitle() != null && request.getTitle().equals(request.getDescription())) {
            bindingResult.rejectValue("description"
                    , "Object.BountyMissionCreateRequest.validateSameNameDescription"
                    , new Object[]{request.getTitle(), request.getDescription()}
                    , "이름과 설명은 같을 수 없습니다."
            );
        }
    }
}
