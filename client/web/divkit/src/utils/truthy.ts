export const Truthy = Boolean as unknown as <T>(x: T | null | 0 | '' | false | undefined) => x is T;
