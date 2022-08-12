from __future__ import annotations
from typing import Optional, Union, List
from ..utils import is_list_of_type


class Text:
    def __init__(self, init_lines: Optional[Union[str, List[str]]] = None, indent_width: int = 0):
        self._lines = []
        self._indent_width = indent_width
        if init_lines is not None:
            if isinstance(init_lines, str):
                self._lines.append(init_lines)
            elif is_list_of_type(init_lines, str):
                self._lines.extend(init_lines)
            else:
                raise TypeError

    def __str__(self):
        return '\n'.join(map(lambda s: ' ' * self.indent_width + s, self._lines))

    def __add__(self, other) -> Text:
        if isinstance(other, str):
            return Text(self._lines + [other])
        elif is_list_of_type(other, str):
            return Text(self._lines + other)
        elif is_list_of_type(other, Text):
            sum_text = Text(self._lines, self.indent_width)
            for other_text in other:
                sum_text += other_text
            return sum_text
        elif isinstance(other, Text):
            return Text(self._lines + [str(other)])
        else:
            raise TypeError

    def indented(self, level: int = 1, indent_width: int = 2) -> Text:
        return Text(init_lines=self._lines, indent_width=self._indent_width + level * indent_width)

    @property
    def indent_width(self) -> int:
        return self._indent_width


EMPTY: Text = Text('')
