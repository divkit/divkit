# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Accessibility settings.
class DivAccessibility(BaseDiv):

    def __init__(
        self, *,
        description: typing.Optional[typing.Union[Expr, str]] = None,
        hint: typing.Optional[typing.Union[Expr, str]] = None,
        mode: typing.Optional[typing.Union[Expr, DivAccessibilityMode]] = None,
        mute_after_action: typing.Optional[typing.Union[Expr, bool]] = None,
        state_description: typing.Optional[typing.Union[Expr, str]] = None,
        type: typing.Optional[typing.Union[Expr, DivAccessibilityType]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            description=description,
            hint=hint,
            mode=mode,
            mute_after_action=mute_after_action,
            state_description=state_description,
            type=type,
            **kwargs,
        )

    description: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Element description. It is used as the main description for "
            "screen readingapplications."
        ),
    )
    hint: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "A tooltip of what will happen during interaction. If Speak "
            "Hints is enabled inthe VoiceOver settings on iOS, a tooltip "
            "is played after `description`."
        ),
    )
    mode: typing.Optional[typing.Union[Expr, DivAccessibilityMode]] = Field(
        description=(
            "The way the accessibility tree is organized. In the `merge` "
            "mode theaccessibility service perceives an element together "
            "with a subtree as a whole. Inthe `exclude` mode an element "
            "together with a subtree isn\'t available foraccessibility."
        ),
    )
    mute_after_action: typing.Optional[typing.Union[Expr, bool]] = Field(
        description=(
            "Mutes the screen reader sound after interacting with the "
            "element."
        ),
    )
    state_description: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Description of the current state of an element. For "
            "example, in the descriptionyou can specify a selected date "
            "for a date selection element and an on/off statefor a "
            "switch."
        ),
    )
    type: typing.Optional[typing.Union[Expr, DivAccessibilityType]] = Field(
        description=(
            "Element role. Used to correctly identify an element by the "
            "accessibility service.For example, the `list` element is "
            "used to group list elements into one element."
        ),
    )


class DivAccessibilityMode(str, enum.Enum):
    DEFAULT = "default"
    MERGE = "merge"
    EXCLUDE = "exclude"


class DivAccessibilityType(str, enum.Enum):
    NONE = "none"
    BUTTON = "button"
    IMAGE = "image"
    TEXT = "text"
    EDIT_TEXT = "edit_text"
    HEADER = "header"
    TAB_BAR = "tab_bar"
    LIST = "list"


DivAccessibility.update_forward_refs()
