import { isRouteErrorResponse, Link, useRevalidator, useRouteError } from 'react-router-dom'
import { ApiError, errorMessage } from '../lib/http'

export function RouteError() {
  const error = useRouteError()
  const revalidator = useRevalidator()
  const missing = (error instanceof ApiError && error.status === 404) || (isRouteErrorResponse(error) && error.status === 404)
  return <section className="panel empty" role="alert">
    <p className="eyebrow">{missing ? 'NOT FOUND' : 'CONNECTION'}</p>
    <h1>{missing ? '찾는 페이지가 없어요' : '잠시 연결을 확인해 주세요'}</h1>
    <p>{missing ? '주소가 올바른지 확인하거나 프로젝트 목록으로 돌아가세요.' : errorMessage(error)}</p>
    <div className="actions"><Link className="button secondary" to="/projects">프로젝트 목록</Link>
      {!missing && <button onClick={() => void revalidator.revalidate()} disabled={revalidator.state !== 'idle'}>다시 시도</button>}
    </div>
  </section>
}
