from typing import Optional, Type

from fastapi import (
    APIRouter,
    Depends,
    File,
    Form,
    HTTPException,
    Path,
    Request,
    Response,
    UploadFile,
)
from fastapi.encoders import jsonable_encoder
from miniopy_async import Minio
from pydantic import UUID4, BaseModel, ValidationError
from sqlalchemy.ext.asyncio import AsyncSession
from starlette import status

from app.config import DefaultSettings
from app.db.connection import get_session
from app.db.storage import templates as templates_storage
from app.schemas import (
    CreateTemplateRequest,
    CreateTemplateResponse,
    GetTemplatesResponse,
    TemplateSchema,
    UpdateTemplateRequest,
    UpdateTemplateResponse,
)
from app.utils import templates as templates_utils
from app.utils.custom_exceptions import NotExistingIdsError
from app.utils.s3 import get_client

settings = DefaultSettings()

template_router = APIRouter(prefix="/v1/template", tags=["template"])


@template_router.get(
    "",
    status_code=status.HTTP_200_OK,
)
async def get_template_list(  # pylint: disable=too-many-arguments
    _: Request,
    tag_ids: Optional[str] = None,
    title: Optional[str] = None,
    page: int = settings.PAGINATION_DEFAULT_PAGE,
    size: int = settings.PAGINATION_DEFAULT_SIZE,
    client: Minio = Depends(get_client),
    session: AsyncSession = Depends(get_session),
) -> GetTemplatesResponse:
    if page <= 0 or size < 10:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Not valid pagination params. PAGE should be >= 1. SIZE should be >= 10.",
        )
    page = (page - settings.PAGINATION_OFFSET) * size
    list_tag_ids = list(map(str.strip, tag_ids.split(","))) if tag_ids else None
    try:
        template_list = await templates_utils.get_templates(
            list_tag_ids, title, page, size, session
        )
    except ValueError as exc:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Tag in tag_ids is not a valid uuid.",
        ) from exc
    template_list_with_tags = await templates_utils.get_templates_with_tags(
        template_list, client, session
    )
    total_pages = await templates_storage.get_template_list_total_pages(
        list_tag_ids, title, size, session
    )
    return GetTemplatesResponse(
        templates=template_list_with_tags, total_pages=total_pages
    )


class DataChecker:
    def __init__(self, name: Type[BaseModel]):
        self.name = name

    def __call__(self, template: str = Form(...)) -> BaseModel:
        try:
            model = self.name.parse_raw(template)
        except ValidationError as e:
            raise HTTPException(
                detail=jsonable_encoder(e.errors()),
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            ) from e
        return model


create_checker = DataChecker(CreateTemplateRequest)
update_checker = DataChecker(UpdateTemplateRequest)


@template_router.post(
    "",
    response_model=CreateTemplateResponse,
    status_code=status.HTTP_201_CREATED,
)
async def create_template(
    template: CreateTemplateRequest = Depends(create_checker),
    file: Optional[UploadFile] = File(None),
    client: Minio = Depends(get_client),
    session: AsyncSession = Depends(get_session),
) -> CreateTemplateResponse:
    try:
        template_id = await templates_utils.create_template(
            template, file, client, session
        )
    except NotExistingIdsError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Not existing tag_ids",
        ) from e
    return CreateTemplateResponse(id=template_id)


@template_router.put(
    "",
    response_model=UpdateTemplateResponse,
    status_code=status.HTTP_200_OK,
)
async def update_template(
    template: UpdateTemplateRequest = Depends(update_checker),
    file: Optional[UploadFile] = File(None),
    client: Minio = Depends(get_client),
    session: AsyncSession = Depends(get_session),
) -> UpdateTemplateResponse:
    try:
        template_id = await templates_utils.update_template(
            template, file, client, session
        )
    except NotExistingIdsError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Not existing template_id or tag_ids",
        ) from e
    return UpdateTemplateResponse(id=template_id)


@template_router.get(
    "/{template_id}",
    status_code=status.HTTP_200_OK,
    response_model=TemplateSchema,
    responses={
        status.HTTP_404_NOT_FOUND: {
            "description": "Template not found",
        },
    },
)
async def get_template(
    _: Request,
    template_id: UUID4 = Path(...),
    client: Minio = Depends(get_client),
    session: AsyncSession = Depends(get_session),
) -> Optional[TemplateSchema]:
    template = await templates_storage.get_template(template_id, session)
    if template:
        return await templates_utils.get_one_template_with_tags(
            template, client, session
        )
    raise HTTPException(
        status_code=status.HTTP_404_NOT_FOUND,
        detail="Template not found",
    )


@template_router.delete(
    "/{template_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    response_class=Response,
    responses={
        status.HTTP_404_NOT_FOUND: {
            "description": "Template not found",
        },
    },
)
async def delete(
    _: Request,
    template_id: UUID4 = Path(...),
    session: AsyncSession = Depends(get_session),
) -> None:
    template = await templates_storage.get_template(template_id, session)
    if template:
        await templates_storage.delete_template(template_id, session)
    else:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Template not found",
        )
