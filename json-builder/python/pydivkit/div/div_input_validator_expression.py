# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Expression validator.
class DivInputValidatorExpression(BaseDiv):

    def __init__(
        self, *,
        type: str = "expression",
        allow_empty: typing.Optional[typing.Union[Expr, bool]] = None,
        condition: typing.Optional[typing.Union[Expr, str]] = None,
        label_id: typing.Optional[typing.Union[Expr, str]] = None,
        variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            allow_empty=allow_empty,
            condition=condition,
            label_id=label_id,
            variable=variable,
            **kwargs,
        )

    type: str = Field(default="expression")
    allow_empty: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Whether an empty value is correct. By default, false.",
    )
    condition: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Expression condition for evaluating.",
    )
    label_id: typing.Union[Expr, str] = Field(
        min_length=1, 
        description=(
            "ID of the text div containing the error message, which will "
            "also be used foraccessibility."
        ),
    )
    variable: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Name of validation storage variable.",
    )


DivInputValidatorExpression.update_forward_refs()
