import { createTheme } from '@mui/material/styles';

declare module '@mui/material/styles' {
  interface Theme {
    themeName: string;
  }
  interface ThemeOptions {
    themeName: string;
  }
}

declare module '@mui/material/Typography' {
  interface TypographyPropsVariantOverrides {
    title: true;
    titleCard: true;
    subtitle: true;
    hintText: true;
    subtitle1: false;
    subtitle2: false;
    text: true;
    errorMessage: true;
  }
}

const themeName = 'main-light';

const palette = {
  primary: { main: '#3d37c5' },
  secondary: { main: '#ff9000', light: '#ffc146a1' },
  warning: { main: '#ffb74b' },
  error: { main: '#e57373' },
  backgroundColor: { main: '#f1f1f1' },
  gray: { main: '#b3b3b3', dark: '#777' },
  black: { main: '#000' },
  white: { main: '#fff' },
};

const typography = {
  fontFamily: 'Inter, sans-serif',
  title: {
    fontSize: '1.7rem',
    textTransform: 'uppercase',
    fontWeight: 200,
    padding: '.6rem 0',
  },
  hintText: {
    fontSize: '1.5rem',
    color: palette.gray.dark,
    fontStyle: 'italic',
  },
  titleCard: {
    padding: '1.5rem 2rem',
    fontSize: '1.9rem',
    textTransform: 'uppercase',
    fontWeight: 200,
    letterSpacing: '2px',
    backgroundColor: palette.secondary.light,
  },
  subtitle: {
    fontWeight: 400,
    letterSpacing: '2px',
  },
  text: {
    fontSize: '1.6rem',
    fontWeight: 300,
    padding: '0 2rem',
    margin: '1.2rem 0',
  },
  caption: {
    fontStyle: 'italic',
    color: palette.gray.main,
    fontWeight: 200,
    fontSize: '1.5rem',
    paddingRight: '2rem',
    margin: '1.2rem 0 1.2rem auto',
  },
  errorMessage: {
    fontStyle: '',
    color: palette.error.main,
    fontWeight: 200,
    fontSize: '1.4rem',
    textTransform: 'uppercase',
  },
};

const spacing = (factor: number) => `${0.4 * factor}rem`;

export default createTheme({ palette, themeName, spacing, typography });
