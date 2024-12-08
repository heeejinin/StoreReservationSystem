package com.project.storereservation.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다."),

    // 매장 ErrorCode
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    NOT_STORE_OWNER(HttpStatus.FORBIDDEN, "매장 주인이 아닙니다."),
    USER_NOT_MANAGER(HttpStatus.FORBIDDEN, "매장 관리자 권한이 아닙니다."),
    INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 형식입니다."),



    // 예약 ErrorCode
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "현재보다 이후의 예약 시간은 유효하지 않습니다."),
    RESERVATION_TIME_UNAVAILABLE(HttpStatus.CONFLICT, "해당 시간대에 중복 예약이 존재합니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND,"예약을 찾을 수 없습니다."),
    NOT_RESERVATION_OWNER(HttpStatus.FORBIDDEN, "사용자는 예약자가 아닙니다."),
    INVALID_ARRIVAL_TIME(HttpStatus.BAD_REQUEST, "예약 시간 10분 전이 아닙니다."),
    RESERVATION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "예약 완료가 되지 않았습니다."),
    NOT_AUTHORIZED_TO_VIEW_RESERVATION(HttpStatus.UNAUTHORIZED, "예약 조회 권한이 없습니다."),

    // 리뷰 ErrorCode
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 존재하는지 리뷰가 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"리뷰를 찾을 수 없습니다."),
    NOT_REVIEW_OWNER(HttpStatus.FORBIDDEN, "리뷰 작성자가 아닙니다."),
    NOT_AUTHORIZED_TO_DELETE_REVIEW(HttpStatus.FORBIDDEN, "리뷰 작성자나 매장 주인만 삭제 가능합니다."),

    // 인증 ErrorCode
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다..");


    private final HttpStatus status;
    private final String message;
}