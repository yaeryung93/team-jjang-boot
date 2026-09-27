export interface Member { id: number; projectId: number; name: string; role: string }
export type CreateMember = Pick<Member, 'name' | 'role'>
