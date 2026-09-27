import { request } from '../../lib/http'
import type { Member, CreateMember } from './types'

export const membersApi = {
  list: (projectId: number, signal?: AbortSignal) => request<Member[]>(`/api/projects/${projectId}/members`, { signal }),
  create: (projectId: number, input: CreateMember) => request<Member>(`/api/projects/${projectId}/members`, { method: 'POST', body: JSON.stringify(input) }),
}
