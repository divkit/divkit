# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Mask for entering currency in the specified regional format.
class DivCurrencyInputMask(BaseDiv):

    def __init__(
        self, *,
        type: str = "currency",
        locale: typing.Optional[typing.Union[Expr, str]] = None,
        raw_text_variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            locale=locale,
            raw_text_variable=raw_text_variable,
            **kwargs,
        )

    type: str = Field(default="currency")
    locale: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Language tag that the currency format should match, as per "
            "[IETF "
            "BCP47](https://en.wikipedia.org/wiki/IETF_language_tag). If "
            "the language is not set,it is defined automatically."
        ),
    )
    raw_text_variable: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Name of the variable to store the unprocessed value.",
    )


DivCurrencyInputMask.update_forward_refs()
