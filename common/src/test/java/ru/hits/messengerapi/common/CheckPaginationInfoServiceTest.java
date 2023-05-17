package ru.hits.messengerapi.common;

import org.junit.jupiter.api.Test;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;

import static org.junit.jupiter.api.Assertions.*;

public class CheckPaginationInfoServiceTest {

    private final CheckPaginationInfoService paginationInfoService = new CheckPaginationInfoService();

    @Test
    void checkPageNumber_shouldThrowBadRequestException_whenPageNumberIsZero() {
        int pageNumber = 0;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPageNumber(pageNumber));
        assertEquals("Номер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPageNumber_shouldThrowBadRequestException_whenPageNumberIsNegative() {
        int pageNumber = -1;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPageNumber(pageNumber));
        assertEquals("Номер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPageNumber_shouldNotThrowException_whenPageNumberIsPositive() {
        int pageNumber = 1;

        assertDoesNotThrow(() -> paginationInfoService.checkPageNumber(pageNumber));
    }

    @Test
    void checkPageSize_shouldThrowBadRequestException_whenPageSizeIsZero() {
        int pageSize = 0;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPageSize(pageSize));
        assertEquals("Размер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPageSize_shouldThrowBadRequestException_whenPageSizeIsNegative() {
        int pageSize = -1;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPageSize(pageSize));
        assertEquals("Размер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPageSize_shouldNotThrowException_whenPageSizeIsPositive() {
        int pageSize = 10;

        assertDoesNotThrow(() -> paginationInfoService.checkPageSize(pageSize));
    }

    @Test
    void checkPagination_shouldThrowBadRequestException_whenPageNumberIsZero() {
        int pageNumber = 0;
        int pageSize = 10;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPagination(pageNumber, pageSize));
        assertEquals("Номер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPagination_shouldThrowBadRequestException_whenPageSizeIsZero() {
        int pageNumber = 1;
        int pageSize = 0;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPagination(pageNumber, pageSize));
        assertEquals("Размер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPagination_shouldThrowBadRequestException_whenPageNumberAndPageSizeAreZero() {
        int pageNumber = 0;
        int pageSize = 0;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> paginationInfoService.checkPagination(pageNumber, pageSize));
        assertEquals("Номер страницы должен быть больше 0.", exception.getMessage());
    }

    @Test
    void checkPagination_shouldNotThrowException_whenPageNumberAndPageSizeArePositive() {
        int pageNumber = 1;
        int pageSize = 10;

        assertDoesNotThrow(() -> paginationInfoService.checkPagination(pageNumber, pageSize));
    }
}
