# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_action


# Timer.
class DivTimer(BaseDiv):

    def __init__(
        self, *,
        duration: typing.Optional[typing.Union[Expr, int]] = None,
        end_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        id: typing.Optional[typing.Union[Expr, str]] = None,
        tick_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        tick_interval: typing.Optional[typing.Union[Expr, int]] = None,
        value_variable: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            duration=duration,
            end_actions=end_actions,
            id=id,
            tick_actions=tick_actions,
            tick_interval=tick_interval,
            value_variable=value_variable,
            **kwargs,
        )

    duration: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Timer duration in milliseconds. If the parameter is `0` or "
            "not specified, thetimer runs indefinitely (until the timer "
            "stop event occurs)."
        ),
    )
    end_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description=(
            "Actions performed when the timer ends: when the timer has "
            "counted to the`duration` value or the "
            "`div-action://timer?action=stop&id=<id>` command has "
            "beenreceived."
        ),
    )
    id: typing.Union[Expr, str] = Field(
        min_length=1, 
        description=(
            "Timer ID. Must be unique. Used when calling actions for the "
            "selected timer, forexample: start, stop."
        ),
    )
    tick_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description="Actions that are performed on each count of the timer.",
    )
    tick_interval: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Duration of time intervals in milliseconds between counts. "
            "If the parameter isnot specified, the timer counts down "
            "from `0` to `duration` without calling`tick_actions`."
        ),
    )
    value_variable: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Name of the variable where the current timer value is "
            "stored. Updated on eachcount or when the timer commands are "
            "called (start, stop, and so on), except thecancel command."
        ),
    )


DivTimer.update_forward_refs()
