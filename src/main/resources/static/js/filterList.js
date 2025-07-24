document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('input.list-filter').forEach(input => {
    const container = document.getElementById(input.dataset.target);
    if (!container) return;

    // prendiamo tutti gli elementi "card" come figli diretti del container
    const cards = Array.from(container.children).filter(el => el.nodeType === 1);
    const items = cards.map(el => ({
      element: el,
      // cercare sempre dentro il primo <h2>
      text: (el.querySelector('h2')?.textContent || '').trim().toLowerCase()
    }));

    // assumiamo che il div di "no-results" sia il fratello immediatamente dopo il container
    const noMsg = container.nextElementSibling;

    input.addEventListener('input', () => {
      const filtro = input.value.trim().toLowerCase();
      let anyVisible = false;

      items.forEach(({ element, text }) => {
        const show = !filtro || text.includes(filtro);
        element.style.display = show ? '' : 'none';
        if (show) anyVisible = true;
      });

      // mostra/nascondi il messaggio "nessun elemento trovato"
      if (noMsg && noMsg.nodeType === 1) {
        noMsg.style.display = anyVisible ? 'none' : '';
      }
    });
  });
});
