package ru.hits.messengerapi.common.helpingservices;


/**
 * Интерфейс для проверки параметров пагинации страницы и размера страницы.
 */
public interface CheckPaginationInfoInterface {

    /**
     * Метод для проверки на корректность номера страницы.
     *
     * @param pageNumber номер страницы.
     */
    void checkPageNumber(int pageNumber);

    /**
     * Метод для проверки на корректность размера страницы.
     *
     * @param pageSize размер страницы.
     */
    void checkPageSize(int pageSize);

    /**
     * Обобщающий метод для проверки всех данных необходимых для пагинации.
     *
     * @param pageNumber номер страницы.
     * @param pageSize   размер страницы.
     */
    void checkPagination(int pageNumber, int pageSize);

}
