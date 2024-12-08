package com.project.storereservation.domain.reservation.service;

import com.project.storereservation.common.exception.CustomException;
import com.project.storereservation.common.exception.ErrorCode;
import com.project.storereservation.domain.reservation.dto.ArrivalRequest;
import com.project.storereservation.domain.reservation.dto.CreateReservationDto;
import com.project.storereservation.domain.reservation.entity.Reservation;
import com.project.storereservation.domain.reservation.entity.ReservationStatus;
import com.project.storereservation.domain.reservation.repository.ReservationRepository;
import com.project.storereservation.domain.store.entity.Store;
import com.project.storereservation.domain.store.repository.StoreRepository;
import com.project.storereservation.domain.user.entity.User;
import com.project.storereservation.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;


    // 예약 생성
    @Override
    @Transactional
    public CreateReservationDto.createResponse createReservation(Long userId, CreateReservationDto.createRequest request) {
        // 유저와 매장 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByName(request.getStoreName())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 예약 시간 생성 (날짜와 시간 결합)
        LocalDateTime reservationDateTime = request.toLocalDateTime();

        // 예약 시간 유효성 검사
        validateReservationDateTime(reservationDateTime);

        // 중복 예약 확인
        validateDuplicateReservation(store.getId(), request.toLocalDateTime());

        // 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .store(store)
                .reservationDateTime(reservationDateTime)
                .numberOfPeople(request.getNumberOfPeople())
                .build();

        // 예약 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        return CreateReservationDto.createResponse.fromEntity(savedReservation);
    }

    // 사용자 예약 취소
    @Override
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = validateReservationOwner(userId, reservationId);

        // 예약 시간 1시간 전까지만 취소 가능
        if (LocalDateTime.now().plusHours(1)
                .isAfter(reservation.getReservationDateTime())) {
            throw new IllegalArgumentException("예약 취소는 예약 시간 1시간 전까지만 가능합니다.");
        }

        reservationRepository.delete(reservation);
    }

    // 예약 상태 변경 (매니저용)
    @Override
    @Transactional
    public void updateReservationStatus(Long managerId, Long reservationId,
                                        ReservationStatus newStatus) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 매니저 권한 확인
        if (!reservation.getStore().getOwner().getId().equals(managerId)) {
            throw new CustomException(ErrorCode.NOT_STORE_OWNER);
        }

        // 상태 변경
        reservation.updateStatus(newStatus);
    }

    // [유효성 검사 메서드]
    // 예약 시간 검증
    private void validateReservationDateTime(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }
    }

    // 중복 예약 검증
    private void validateDuplicateReservation(Long storeId, LocalDateTime dateTime) {
        if (reservationRepository.existsByStoreIdAndReservationDateTime(
                storeId, dateTime)) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_UNAVAILABLE);
        }
    }

    // 예약 본인 확인 검증
    private Reservation validateReservationOwner(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_RESERVATION_OWNER);
        }

        return reservation;
    }

    // 유저의 예약 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CreateReservationDto.createResponse> getUserReservations(Long userId, Pageable pageable) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Page<Reservation> reservations = reservationRepository
                .findByUserIdOrderByReservationDateTimeDesc(userId, pageable);

        return reservations.map(CreateReservationDto.createResponse::fromEntity);
    }

    // 매니저의 매장 예약 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CreateReservationDto.createResponse> getStoreReservations(
            Long managerId,
            Long storeId,
            ReservationStatus status,
            Pageable pageable
    ) {
        // 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 매장 관리자 권한 확인
        if (!store.getOwner().getId().equals(managerId)) {
            throw new CustomException(ErrorCode.NOT_STORE_OWNER);
        }

        // 상태별 예약 조회
        Page<Reservation> reservations = status != null ?
                reservationRepository.findByStoreIdAndStatusOrderByReservationDateTimeDesc(
                        storeId, status, pageable) :
                reservationRepository.findByStoreIdOrderByReservationDateTimeDesc(
                        storeId, pageable);

        return reservations.map(CreateReservationDto.createResponse::fromEntity);


    }

    // 예약 상세 조회
    @Override
    @Transactional(readOnly = true)
    public CreateReservationDto.createResponse getReservationDetail(
            Long userId,
            Long reservationId
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 예약자 본인 또는 매장 관리자만 조회 가능
        if (!reservation.getUser().getId().equals(userId) &&
                !reservation.getStore().getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED_TO_VIEW_RESERVATION);
        }

        return CreateReservationDto.createResponse.fromEntity(reservation);
    }

    // [키오스크] 매장 방문 확인
    @Override
    @Transactional
    public void confirmArrival(ArrivalRequest request) {
        // 이름과 전화번호로 현재 예약 찾기
        Reservation reservation = reservationRepository
                .findByUserNameAndUserPhoneAndStatus(
                        request.getUserName(),
                        request.getPhone(),
                        ReservationStatus.CONFIRMED,
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

//        // 예약 시간 10분 전인지 확인
//        if (!reservation.withinArrivalTime()) {
//            throw new CustomException(ErrorCode.INVALID_ARRIVAL_TIME);
//        }

        // 도착 처리
        reservation.confirmArrival();
    }
}