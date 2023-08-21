# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing

from . import entity


class EntityWithEntityProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_entity_property',
        entity: typing.Optional[entity.Entity] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            entity=entity,
            **kwargs,
        )

    type: str = Field(default='entity_with_entity_property')
    entity: typing.Optional[entity.Entity] = Field(
    )


EntityWithEntityProperty.update_forward_refs()
