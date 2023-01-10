FROM python:3.10-slim

RUN python -m pip install --upgrade pip
RUN pip install poetry

WORKDIR /divkit-marketplace-backend/app/

ADD poetry.lock pyproject.toml ./

ENV PYTHONPATH=/divkit-marketplace-backend/app/

ENV PATH=/root/.poetry/bin:${PATH}
RUN poetry config virtualenvs.create false
RUN poetry install --only main
RUN poetry show

COPY . .

ENTRYPOINT alembic upgrade head
ENTRYPOINT ["poetry","run","python","-m","app"]