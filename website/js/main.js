(() => {
  const root = document.documentElement;
  root.classList.add('js');

  document.addEventListener('DOMContentLoaded', () => {
    // Mobile menu
    const menuBtn = document.querySelector('.menu-btn');
    const links = document.querySelector('.nav-links');
    if (menuBtn && links) {
      const close = () => { links.classList.remove('open'); menuBtn.setAttribute('aria-expanded', 'false'); };
      menuBtn.addEventListener('click', () => {
        const open = links.classList.toggle('open');
        menuBtn.setAttribute('aria-expanded', String(open));
      });
      links.addEventListener('click', (e) => { if (e.target.closest('a')) close(); });
      document.addEventListener('keydown', (e) => { if (e.key === 'Escape') close(); });
    }

    // Header hairline once the page scrolls
    const header = document.querySelector('.site-header');
    const onScroll = () => header && header.classList.toggle('scrolled', window.scrollY > 8);
    onScroll();
    window.addEventListener('scroll', onScroll, { passive: true });

    // Screenshot rail arrows
    const rail = document.querySelector('.rail');
    const prev = document.querySelector('[data-rail="prev"]');
    const next = document.querySelector('[data-rail="next"]');
    if (rail && prev && next) {
      const step = () => (rail.querySelector('figure')?.getBoundingClientRect().width || 260) + 16;
      const sync = () => {
        prev.disabled = rail.scrollLeft <= 4;
        next.disabled = rail.scrollLeft + rail.clientWidth >= rail.scrollWidth - 4;
      };
      prev.addEventListener('click', () => rail.scrollBy({ left: -step(), behavior: 'smooth' }));
      next.addEventListener('click', () => rail.scrollBy({ left: step(), behavior: 'smooth' }));
      rail.addEventListener('scroll', sync, { passive: true });
      window.addEventListener('resize', sync);
      sync();
    }

    // Fade sections in as they arrive
    const reveals = document.querySelectorAll('.reveal');
    if ('IntersectionObserver' in window) {
      const io = new IntersectionObserver((entries) => {
        entries.forEach((en) => { if (en.isIntersecting) { en.target.classList.add('in'); io.unobserve(en.target); } });
      }, { rootMargin: '0px 0px -8% 0px' });
      reveals.forEach((el) => io.observe(el));
    } else {
      reveals.forEach((el) => el.classList.add('in'));
    }

    // Legal pages: highlight the section being read in the table of contents
    const tocLinks = document.querySelectorAll('.toc a');
    if (tocLinks.length && 'IntersectionObserver' in window) {
      const byId = new Map([...tocLinks].map((a) => [a.getAttribute('href').slice(1), a]));
      const spy = new IntersectionObserver((entries) => {
        entries.forEach((en) => {
          if (!en.isIntersecting) return;
          tocLinks.forEach((a) => a.classList.remove('active'));
          byId.get(en.target.id)?.classList.add('active');
        });
      }, { rootMargin: '-20% 0px -70% 0px' });
      byId.forEach((_, id) => { const s = document.getElementById(id); if (s) spy.observe(s); });
    }

    document.querySelectorAll('[data-year]').forEach((el) => { el.textContent = new Date().getFullYear(); });
  });
})();
