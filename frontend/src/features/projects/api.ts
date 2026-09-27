import { request } from '../../lib/http'
import type { Project, CreateProject } from './types'

export const projectsApi = {
  list: (signal?: AbortSignal) => request<Project[]>(`/api/projects`, { signal }),
  create: (input: CreateProject) => request<Project>(`/api/projects`, { method: 'POST', body: JSON.stringify(input) }),
  get: (id: number, signal?: AbortSignal) => request<Project>(`/api/projects/${id}`, { signal }),
}
