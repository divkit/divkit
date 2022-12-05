# pylint: disable=no-self-argument
from pydantic import UUID4, BaseModel, root_validator


class CreateTagRequest(BaseModel):
    tag: str

    @root_validator
    def check_tag_length(cls, values: dict) -> ValueError | dict:  # type: ignore
        t = values.get("tag")
        if len(t) > 30:  # type: ignore
            raise ValueError("Tag length must be less or equal to 30.")
        return values


class CreateTagResponse(BaseModel):
    id: UUID4


class UpdateTagRequest(BaseModel):
    id: UUID4
    tag: str

    @root_validator
    def check_tag_length(cls, values: dict) -> ValueError | dict:  # type: ignore
        t = values.get("tag")
        if len(t) > 30:  # type: ignore
            raise ValueError("Tag length must be less or equal to 30.")
        return values


class UpdateTagResponse(BaseModel):
    id: UUID4


class TagSchema(BaseModel):
    id: UUID4
    tag: str


class TagForListSchema(TagSchema):
    max_use: int


class GetTagsResponse(BaseModel):
    tags: list[TagForListSchema]
