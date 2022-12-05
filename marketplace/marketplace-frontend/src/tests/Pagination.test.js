import { render, screen } from '@testing-library/react'
import { Provider } from 'react-redux';
import { PaginationControlled } from '../components/PaginationControlled/PaginationControlled';
import { store } from '../store/store'
describe('Header', () => {
  test('Render tags', async () => {
    render(<Provider store={store}><PaginationControlled totalPages={5} /></Provider>)
    const pagination = await screen.findByTestId('pagination')
    const elems = pagination.querySelectorAll('li')

    expect(elems.length).toBe(7)
  })
})
