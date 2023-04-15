package ru.hits.messengerapi.common.helpingservices.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoInterface;

/**
 * Вспомогательный сервис для проверки на корректность данных необходимых для пагинации.
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
            log.error("Номер страницы должен быть больше 0.");
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
            log.error("Размер страницы должен быть больше 0.");
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
