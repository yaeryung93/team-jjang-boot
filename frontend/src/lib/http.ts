const baseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/+$/, '')

export class ApiError extends Error {
  readonly status: number
  readonly fields: Record<string, string>
  constructor(message: string, status: number, fields: Record<string, string> = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fields = fields
  }
}

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  let response: Response
  try {
    const headers = new Headers(options.headers)
    if (options.body) headers.set('Content-Type', 'application/json')
    response = await fetch(`${baseUrl}${path}`, { ...options, headers })
  } catch (error) {
    if (options.signal?.aborted) throw error
    throw new ApiError('서버에 연결할 수 없습니다. 백엔드 실행 상태와 API 주소를 확인해 주세요.', 0)
  }
  if (!response.ok) {
    const problem = await response.json().catch(() => null)
    throw new ApiError(problem?.detail || `요청을 처리하지 못했습니다. (${response.status})`, response.status, problem?.errors || {})
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

export function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : '요청 중 오류가 발생했습니다.'
}
