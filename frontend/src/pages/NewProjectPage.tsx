import { useState } from 'react'
import { Form, Link, useActionData, useNavigation } from 'react-router-dom'
import type { ProjectActionError } from '../app/router'

export function NewProjectPage() {
  const error = useActionData() as ProjectActionError | undefined
  const navigation = useNavigation()
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [deadline, setDeadline] = useState('')
  const today = new Date()
  const minimum = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  const busy = navigation.state !== 'idle'
  return <div className="form-page"><Link className="back-link" to="/projects">← 프로젝트 목록</Link><p className="eyebrow">A NEW START</p><h1>어떤 프로젝트를 만들까요?</h1><p className="subtitle">팀이 함께 바라볼 목표를 정해 주세요.</p>
    <Form method="post" className="panel project-form">
      {error && <div className="error-message" role="alert"><p>{error.message}</p>{Object.entries(error.fields).map(([field, message]) => <p key={field}>{({ name: '프로젝트 이름', description: '설명', deadline: '마감일' } as Record<string, string>)[field] || field}: {message}</p>)}</div>}
      <label htmlFor="name">프로젝트 이름 <span>필수</span></label><input id="name" name="name" required maxLength={100} value={name} onChange={e => setName(e.target.value)} placeholder="예: 팀장봇 MVP 만들기" />
      <label htmlFor="description">프로젝트 설명</label><textarea id="description" name="description" maxLength={2000} rows={4} value={description} onChange={e => setDescription(e.target.value)} placeholder="어떤 문제를 해결하고 싶은가요?" />
      <label htmlFor="deadline">마감일 <span>필수</span></label><input id="deadline" name="deadline" type="date" required min={minimum} value={deadline} onChange={e => setDeadline(e.target.value)} />
      <div className="form-footer"><Link to="/projects" className="button secondary">취소</Link><button type="submit" disabled={busy}>{busy ? '만드는 중…' : '프로젝트 만들기'}</button></div>
    </Form>
  </div>
}
