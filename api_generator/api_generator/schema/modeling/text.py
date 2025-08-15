from __future__ import annotations
from typing import Optional, Union, List
from ..utils import is_list_of_type
from ...utils import indented


class Text:
    def __init__(
        self,
        init_lines: Optional[Union[str, List[str]]] = None,
        indent_width: int = 0,
        indent_char: str = ' ',
    ):
        self._lines = []
        self._indent_width = indent_width
        self._indent_char: str = indent_char
        if init_lines is not None:
            if isinstance(init_lines, str):
                self._lines.append(init_lines)
            elif is_list_of_type(init_lines, str):
                self._lines.extend(init_lines)
            else:
                raise TypeError

    def __str__(self):
        def transform(string: str) -> str:
            if not string or string.isspace():
                return ''
            else:
                return indented(string, indent_width=self.indent_width, indent_char=self._indent_char)
        return '\n'.join(map(transform, self._lines))

    def with_(
        self,
        init_lines: Optional[Union[str, List[str]]] = None,
        indent_width: Optional[int] = None,
        indent_char: Optional[str] = None
    ) -> "Text":
        return Text(
            init_lines=init_lines or self.lines,
            indent_width=indent_width or self.indent_width,
            indent_char=indent_char or self.indent_char,
        )

    def __add__(self, other) -> Text:
        if isinstance(other, str):
            return self.with_(init_lines=self._lines + [other])
        elif is_list_of_type(other, str):
            return self.with_(init_lines=self._lines + other)
        elif is_list_of_type(other, Text):
            sum_lines = self._lines.copy()
            for other_text in other:
                sum_lines.append(str(other_text))
            return self.with_(init_lines=sum_lines)
        elif isinstance(other, Text):
            return self.with_(init_lines=self._lines + [str(other)])
        else:
            raise TypeError

    def indented(self, level: int = 1, indent_width: int = 2) -> Text:
        return self.with_(indent_width=self._indent_width + level * indent_width)

    @property
    def indent_width(self) -> int:
        return self._indent_width

    @property
    def lines(self) -> List[str]:
        return self._lines

    @property
    def indent_char(self) -> str:
        return self._indent_char


EMPTY: Text = Text('')
