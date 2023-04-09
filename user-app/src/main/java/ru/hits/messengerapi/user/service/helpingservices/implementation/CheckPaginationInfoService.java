package ru.hits.messengerapi.user.service.helpingservices.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.user.service.helpingservices.CheckPaginationInfoInterface;

@Service
@RequiredArgsConstructor
public class CheckPaginationInfoService implements CheckPaginationInfoInterface {

    /**
     * Метод для проверки на корректность номера страницы.
     *
     * @param pageNumber номер страницы.
     * @throws BadRequestException некорректный запрос.
     */
    @Override
    public void checkPageNumber(int pageNumber) {
        if (pageNumber <= 0) {
            throw new BadRequestException("Номер страницы должен быть больше 0.");
        }
    }

    /**
     * Метод для проверки на корректность размера страницы.
     *
     * @param pageSize размер страницы.
     * @throws BadRequestException некорректный запрос.
     */
    @Override
    public void checkPageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new BadRequestException("Размер страницы должен быть больше 0.");
        }
    }

    /**
     * Обобщающий метод для проверки всех данных необходимых для пагинации.
     *
     * @param pageNumber номер страницы.
     * @param pageSize   размер страницы.
     */
    @Override
    public void checkPagination(int pageNumber, int pageSize) {
        checkPageNumber(pageNumber);
        checkPageSize(pageSize);
    }
}
