# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Regex validator.
class DivInputValidatorRegex(BaseDiv):

    def __init__(
        self, *,
        type: str = "regex",
        allow_empty: typing.Optional[typing.Union[Expr, bool]] = None,
        label_id: typing.Optional[typing.Union[Expr, str]] = None,
        pattern: typing.Optional[typing.Union[Expr, str]] = None,
        variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            allow_empty=allow_empty,
            label_id=label_id,
            pattern=pattern,
            variable=variable,
            **kwargs,
        )

    type: str = Field(default="regex")
    allow_empty: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Whether an empty value is correct. By default, false.",
    )
    label_id: typing.Union[Expr, str] = Field(
        min_length=1, 
        description=(
            "ID of the text div containing the error message, which will "
            "also be used foraccessibility."
        ),
    )
    pattern: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Regex pattern for matching.",
    )
    variable: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Name of validation storage variable.",
    )


DivInputValidatorRegex.update_forward_refs()
