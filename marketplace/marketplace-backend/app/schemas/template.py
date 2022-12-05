# pylint: disable=no-self-argument
from datetime import datetime
from typing import List, Optional

from pydantic import UUID4, BaseModel, root_validator

from app.schemas.tag import TagSchema


class CreateTemplateRequest(BaseModel):
    template: dict
    description: Optional[str]
    title: Optional[str]
    preview_filename: Optional[str]
    tag_ids: Optional[List[UUID4]]

    @root_validator
    def check_title_length(cls, values: dict) -> ValueError | dict:  # type: ignore
        t = values.get("title")
        if len(t) > 50:  # type: ignore
            raise ValueError("Title length must be less or equal to 50.")
        return values

    @root_validator
    def check_description_length(cls, values: dict) -> ValueError | dict:  # type: ignore
        t = values.get("description")
        if len(t) > 300:  # type: ignore
            raise ValueError("Description length must be less or equal to 300.")
        return values


class CreateTemplateResponse(BaseModel):
    id: UUID4


class UpdateTemplateRequest(BaseModel):
    id: UUID4
    template: dict
    description: Optional[str]
    title: Optional[str]
    preview_filename: Optional[str]
    tag_ids: Optional[List[UUID4]]

    @root_validator
    def check_title_length(cls, values: dict) -> ValueError | dict:  # type: ignore
        t = values.get("title")
        if len(t) > 50:  # type: ignore
            raise ValueError("Title length must be less or equal to 50.")
        return values

    @root_validator
    def check_description_length(cls, values: dict) -> ValueError | dict:  # type: ignore
        t = values.get("description")
        if len(t) > 300:  # type: ignore
            raise ValueError("Description length must be less or equal to 300.")
        return values


class UpdateTemplateResponse(BaseModel):
    id: UUID4


class TemplateSchema(BaseModel):
    id: UUID4
    template: dict
    description: Optional[str]
    title: Optional[str]
    dt_updated: datetime
    tags: List[TagSchema]
    preview_filename: Optional[str]
    preview_url: Optional[str]


class GetTemplatesResponse(BaseModel):
    templates: List[TemplateSchema]
    total_pages: int
