from fastapi import APIRouter, Depends, HTTPException, Request
from sqlalchemy.ext.asyncio import AsyncSession
from starlette import status

from app.config import DefaultSettings
from app.db.connection import get_session
from app.schemas import (
    CreateTagRequest,
    CreateTagResponse,
    GetTagsResponse,
    UpdateTagRequest,
    UpdateTagResponse,
)
from app.utils import tags as tags_utils

settings = DefaultSettings()

tag_router = APIRouter(prefix="/v1/tag", tags=["tag"])


@tag_router.get(
    "/{tag}",
    status_code=status.HTTP_200_OK,
)
async def get_tag_list(
    _: Request,
    tag: str,
    page: int = settings.PAGINATION_DEFAULT_PAGE,
    size: int = settings.PAGINATION_DEFAULT_SIZE,
    session: AsyncSession = Depends(get_session),
) -> GetTagsResponse:
    if page <= 0 or size < 10:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Not valid pagination params. PAGE should be >= 1. SIZE should be >= 10",
        )
    page = (page - settings.PAGINATION_OFFSET) * size
    tag_list = await tags_utils.get_tag_list(tag, page, size, session)
    return GetTagsResponse(tags=tag_list)


@tag_router.post(
    "",
    response_model=CreateTagResponse,
    status_code=status.HTTP_201_CREATED,
)
async def create_tag(
    tag: CreateTagRequest,
    session: AsyncSession = Depends(get_session),
) -> CreateTagResponse:
    tag_id = await tags_utils.create_tag(tag, session)
    return CreateTagResponse(id=tag_id)


@tag_router.put(
    "",
    response_model=UpdateTagResponse,
    status_code=status.HTTP_200_OK,
)
async def update_tag(
    tag: UpdateTagRequest,
    session: AsyncSession = Depends(get_session),
) -> UpdateTagResponse:
    tag_id = await tags_utils.update_tag(tag, session)
    return UpdateTagResponse(id=tag_id)
