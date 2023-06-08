# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Mask for entering text with a fixed number of characters.
class DivFixedLengthInputMask(BaseDiv):

    def __init__(
        self, *,
        type: str = "fixed_length",
        always_visible: typing.Optional[typing.Union[Expr, bool]] = None,
        pattern: typing.Optional[typing.Union[Expr, str]] = None,
        pattern_elements: typing.Optional[typing.Sequence[DivFixedLengthInputMaskPatternElement]] = None,
        raw_text_variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            always_visible=always_visible,
            pattern=pattern,
            pattern_elements=pattern_elements,
            raw_text_variable=raw_text_variable,
            **kwargs,
        )

    type: str = Field(default="fixed_length")
    always_visible: typing.Optional[typing.Union[Expr, bool]] = Field(
        description=(
            "If this option is enabled, the text field contains the mask "
            "before being filledin."
        ),
    )
    pattern: typing.Union[Expr, str] = Field(
        min_length=1, 
        description=(
            "String that sets the text input template. For example, the "
            "`+7 (###) ###-##-#`template for a phone number."
        ),
    )
    pattern_elements: typing.Sequence[DivFixedLengthInputMaskPatternElement] = Field(
        min_items=1, 
        description=(
            "Template decoding is a description of the characters that "
            "will be replaced withuser input."
        ),
    )
    raw_text_variable: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Name of the variable to store the unprocessed value.",
    )


# Template decoding is a description of the characters that will be replaced with
# user input.
class DivFixedLengthInputMaskPatternElement(BaseDiv):

    def __init__(
        self, *,
        key: typing.Optional[typing.Union[Expr, str]] = None,
        placeholder: typing.Optional[typing.Union[Expr, str]] = None,
        regex: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            key=key,
            placeholder=placeholder,
            regex=regex,
            **kwargs,
        )

    key: typing.Union[Expr, str] = Field(
        min_length=1, 
        description=(
            "A character in the template that will be replaced with a "
            "user-defined character."
        ),
    )
    placeholder: typing.Optional[typing.Union[Expr, str]] = Field(
        description=(
            "The character that\'s displayed in the input field where "
            "the user is expected toenter text. This is used if mask "
            "display is enabled."
        ),
    )
    regex: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Regular expression for validating character inputs. For "
            "example, when a mask isdigit-only."
        ),
    )


DivFixedLengthInputMaskPatternElement.update_forward_refs()


DivFixedLengthInputMask.update_forward_refs()
