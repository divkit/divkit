# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivInputMaskBase(BaseDiv):

    def __init__(
        self, *,
        raw_text_variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            raw_text_variable=raw_text_variable,
            **kwargs,
        )

    raw_text_variable: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Name of the variable to store the unprocessed value.",
    )


DivInputMaskBase.update_forward_refs()
