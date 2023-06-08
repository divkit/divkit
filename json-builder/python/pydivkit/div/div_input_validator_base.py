# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivInputValidatorBase(BaseDiv):

    def __init__(
        self, *,
        allow_empty: typing.Optional[typing.Union[Expr, bool]] = None,
        label_id: typing.Optional[typing.Union[Expr, str]] = None,
        variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            allow_empty=allow_empty,
            label_id=label_id,
            variable=variable,
            **kwargs,
        )

    allow_empty: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Whether an empty value is correct. By default, false.",
    )
    label_id: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "ID of the text div containing the error message, which will "
            "also be used foraccessibility."
        ),
    )
    variable: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description="Name of validation storage variable.",
    )


DivInputValidatorBase.update_forward_refs()
