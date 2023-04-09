package ru.hits.messengerapi.user.service.helpingservices;

public interface CheckPaginationInfoInterface {

    void checkPageNumber(int pageNumber);

    void checkPageSize(int pageSize);

    void checkPagination(int pageNumber, int pageSize);

}
