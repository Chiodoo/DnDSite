document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('input.list-filter').forEach(input => {
    const container = document.getElementById(input.dataset.target);
    if (!container) return;

    const items = Array.from(container.querySelectorAll('.campagna-card')).map(el => ({
      element: el,
      text: el.querySelector('h2').textContent.trim().toLowerCase()
    }));

    input.addEventListener('input', () => {
      const filtro = input.value.trim().toLowerCase();
      let anyVisible = false;

      items.forEach(({ element, text }) => {
        const show = !filtro || text.includes(filtro);
        element.style.display = show ? '' : 'none';
        if (show) anyVisible = true;
      });

      const noMsg = document.getElementById('no-campagna');
      if (noMsg) noMsg.style.display = anyVisible ? 'none' : '';
    });
  });
});
