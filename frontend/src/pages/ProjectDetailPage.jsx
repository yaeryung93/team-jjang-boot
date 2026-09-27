import { Link, useLoaderData } from 'react-router-dom';
export function ProjectDetailPage() {
    const { project, members, tasks } = useLoaderData();
    return <>
    <Link className="back-link" to="/projects">← 프로젝트 목록</Link>
    <section className="page-heading"><div><p className="eyebrow">PROJECT OVERVIEW</p><h1>{project.name}</h1><p>{project.description || '프로젝트 설명이 아직 없어요.'}</p></div><span className="deadline">마감 <time dateTime={project.deadline}>{project.deadline}</time></span></section>
    <div className="detail-grid"><section className="panel detail-panel"><div className="section-heading"><h2>팀원 <span className="count">{members.length}</span></h2></div>
      {members.length === 0 ? <p className="muted">등록된 팀원이 없어요. 팀원 등록 화면은 다음 단계에서 연결할 예정이에요.</p> : <ul className="member-list">{members.map(member => <li key={member.id}><span className="avatar" aria-hidden="true">{member.name.slice(0, 1)}</span><div><strong>{member.name}</strong><span>{member.role}</span></div></li>)}</ul>}
    </section><section className="panel detail-panel"><div className="section-heading"><h2>작업 <span className="count">{tasks.length}</span></h2></div>
      {tasks.length === 0 ? <p className="muted">아직 등록된 작업이 없어요. 작업 등록 화면은 다음 단계에서 연결할 예정이에요.</p> : <ul className="task-list">{tasks.map(task => <li key={task.id}><h3>{task.title}</h3><p>{members.find(member => member.id === task.assigneeId)?.name || '담당자 미배정'}</p><span className="estimate">낙관 {task.optimisticHours}h · 보통 {task.likelyHours}h · 비관 {task.pessimisticHours}h</span></li>)}</ul>}
    </section></div>
    <aside className="simulation-note"><span className="badge">준비 중</span><div><h2>일정 위험 시뮬레이션</h2><p>작업 의존관계와 잔여시간을 연결한 뒤 마감 가능성을 분석할 예정이에요.</p></div></aside>
  </>;
}
