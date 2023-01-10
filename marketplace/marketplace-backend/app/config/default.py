from pydantic import BaseSettings


class DefaultSettings(BaseSettings):
    """Размер отображаемых данных за один запрос"""

    PAGINATION_DEFAULT_SIZE = 12
    """Номер страницы"""
    PAGINATION_DEFAULT_PAGE = 1
    """
    Начальной страницей считаем - 1
    Поэтому делаем смещение номера страницы на 1
    Чтобы первая страница не приводила к пропуску первой партии данных
    """
    PAGINATION_OFFSET = 1
