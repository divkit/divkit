# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class EntityWithJsonProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_json_property',
        json_property: typing.Optional[typing.Dict[str, typing.Any]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            json_property=json_property,
            **kwargs,
        )

    type: str = Field(default='entity_with_json_property')
    json_property: typing.Optional[typing.Dict[str, typing.Any]] = Field(
    )


EntityWithJsonProperty.update_forward_refs()
