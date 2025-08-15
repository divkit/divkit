# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class WithDefault(BaseDiv):

    def __init__(
        self, *,
        type: str = 'default',
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            **kwargs,
        )

    type: str = Field(default='default')


WithDefault.update_forward_refs()
