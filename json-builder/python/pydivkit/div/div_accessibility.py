# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Accessibility for disabled people.
class DivAccessibility(BaseDiv):

    def __init__(
        self, *,
        description: typing.Optional[str] = None,
        hint: typing.Optional[str] = None,
        mode: typing.Optional[DivAccessibilityMode] = None,
        mute_after_action: typing.Optional[bool] = None,
        state_description: typing.Optional[str] = None,
        type: typing.Optional[DivAccessibilityType] = None,
    ):
        super().__init__(
            description=description,
            hint=hint,
            mode=mode,
            mute_after_action=mute_after_action,
            state_description=state_description,
            type=type,
        )

    description: typing.Optional[str] = Field(
        min_length=1, 
        description=(
            "Element description. It is used as the main description for "
            "screen readingapplications."
        ),
    )
    hint: typing.Optional[str] = Field(
        min_length=1, 
        description=(
            "A tooltip of what will happen during interaction. If Speak "
            "Hints is enabled inthe VoiceOver settings on iOS, a tooltip "
            "is played after `description`."
        ),
    )
    mode: typing.Optional[DivAccessibilityMode] = Field(
        description=(
            "The way the accessibility tree is organized. In the `merge` "
            "mode theaccessibility service perceives an element together "
            "with a subtree as a whole. Inthe `exclude` mode an element "
            "together with a subtree isn\'t available foraccessibility."
        ),
    )
    mute_after_action: typing.Optional[bool] = Field(
        description=(
            "Mutes the sound of the screen reader after interacting with "
            "the element."
        ),
    )
    state_description: typing.Optional[str] = Field(
        min_length=1, 
        description=(
            "Description of the current state of an element. For "
            "example, in the descriptionyou can specify a selected date "
            "for a date selection element and an on/off statefor a "
            "switch."
        ),
    )
    type: typing.Optional[DivAccessibilityType] = Field(
        description=(
            "Element role. It is used for correct identification of an "
            "element by anaccessibility service."
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


DivAccessibility.update_forward_refs()
