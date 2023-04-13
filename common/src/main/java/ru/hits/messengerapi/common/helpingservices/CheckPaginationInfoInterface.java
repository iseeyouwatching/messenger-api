package ru.hits.messengerapi.common.helpingservices;

/**
 * Интерфейс для проверки параметров пагинации страницы и размера страницы.
 */
public interface CheckPaginationInfoInterface {

    void checkPageNumber(int pageNumber);

    void checkPageSize(int pageSize);

    void checkPagination(int pageNumber, int pageSize);

}