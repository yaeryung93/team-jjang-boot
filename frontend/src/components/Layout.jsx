import { Link, NavLink, Outlet, useNavigation } from 'react-router-dom';
export function Layout() {
    const navigation = useNavigation();
    return <>
    <a className="skip-link" href="#main">본문으로 이동</a>
    <header className="header"><div className="header-inner">
      <Link to="/projects" className="brand"><span className="brand-icon" aria-hidden="true">팀</span>팀장봇<span className="badge">WORKSPACE</span></Link>
      <nav aria-label="주 메뉴"><NavLink to="/projects">프로젝트</NavLink></nav>
    </div></header>
    <main id="main" className="container" aria-busy={navigation.state !== 'idle'}>
      {navigation.state !== 'idle' && <p role="status" className="loading">불러오는 중…</p>}
      <Outlet />
    </main>
    <footer className="footer">팀의 계획이, 함께하는 실행으로.</footer>
  </>;
}
