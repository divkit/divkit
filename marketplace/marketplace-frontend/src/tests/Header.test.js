import { render, screen } from '@testing-library/react'
import { Header } from '../components/Header/Header';
import { renderApplication } from '../mocks/helper';

describe('Header', () => {
  test('Header exists', async () => {
    const view = renderApplication('/', <Header />)

    render(view)
    const header = screen.getAllByRole('link')[0]
    expect(header).toBeTruthy()
  })

  test('Logo redirects to Home page', async () => {
    const view = renderApplication('/', <Header />)
    render(view)
    const header = screen.getAllByRole('link')[0]
    expect(header.getAttribute('href')).toBe('/')
  })
})
